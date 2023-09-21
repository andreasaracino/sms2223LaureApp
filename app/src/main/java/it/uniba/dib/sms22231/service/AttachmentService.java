package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class AttachmentService {
    private static AttachmentService instance;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public void saveAttachments(List<Uri> filesList, CallbackFunction<List<String>> callback) {
        List<String> savedFilesPaths = new ArrayList<>();
        AtomicReference<Integer> savedFiles = new AtomicReference<>(0);

        for (Uri file : filesList) {
            String uniqueFilePath = System.currentTimeMillis() + "_" + file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
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

            if (filesIds != null) {
                List<Attachment> attachments = new ArrayList<>();

                for (String fileId : filesIds) {
                    StorageReference storedFile = storageReference.child(fileId);

                    Attachment attachment = new Attachment();
                    attachment.id = fileId;
                    attachment.fileName = storedFile.getName();
                    attachment.path = storedFile.getPath();

                    attachments.add(attachment);
                }

                next.apply(attachments);
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
