package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.config.FileType;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class AttachmentService {
    private static AttachmentService instance;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public void saveAttachments(List<Uri> filesList, List<String> fileNames, CallbackFunction<List<String>> callback) {
        List<String> savedFilesPaths = new ArrayList<>();

        if (filesList.size() == 0) {
            callback.apply(savedFilesPaths);
        }

        AtomicReference<Integer> savedFiles = new AtomicReference<>(0);
        int index = 0;

        for (Uri file : filesList) {
            String uniqueFilePath = System.currentTimeMillis() + "_" + fileNames.get(index++);
            savedFilesPaths.add(uniqueFilePath);
            storageReference.child(uniqueFilePath).putFile(file).addOnCompleteListener(task -> {
                savedFiles.updateAndGet(v -> v + 1);

                if (savedFiles.get() == filesList.size()) {
                    callback.apply(savedFilesPaths);
                }
            });
        }
    }

    public Observable<List<Attachment>> getAttachmentsByThesis(Thesis thesis) {
        return new Observable<>((next) -> {
            List<String> filesIds = thesis.attachments;

            if (filesIds != null && filesIds.size() > 0) {
                List<Attachment> attachments = new ArrayList<>();

                for (String fileId : filesIds) {
                    StorageReference storedFile = storageReference.child(fileId);

                    storedFile.getDownloadUrl().addOnCompleteListener(task -> {
                        String fileName = storedFile.getName();
                        Attachment attachment = new Attachment();
                        attachment.id = fileId;
                        attachment.setFileName(fileName.substring(fileName.indexOf("_") + 1));
                        attachment.path = task.getResult();

                        attachments.add(attachment);

                        if (attachments.size() == filesIds.size()) {
                            next.apply(attachments);
                        }
                    });
                }
            } else {
                next.apply(new ArrayList<>());
            }
        });
    }

    private AttachmentService() {}

    public static AttachmentService getInstance() {
        if (instance == null) {
            instance = new AttachmentService();
        }

        return instance;
    }
}
