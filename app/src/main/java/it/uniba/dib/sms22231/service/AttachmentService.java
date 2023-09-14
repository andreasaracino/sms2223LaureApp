package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.utility.CallbackFunction;

public class AttachmentService {
    private static final String COLLECTION_NAME = "attachments";
    private static AttachmentService instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private CollectionReference attachmentsCollection;

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

    private AttachmentService() {
        attachmentsCollection = db.collection(COLLECTION_NAME);
    }

    public static AttachmentService getInstance() {
        if (instance == null) {
            instance = new AttachmentService();
        }

        return instance;
    }
}
