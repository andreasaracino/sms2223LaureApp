package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.FileType;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class AttachmentService {
    private final List<Attachment> database;
    private static AttachmentService instance;

    // Viene salvata nello storage di Firebase una lista di file associati a una tesi e viene restituita la lista dei riferimenti a tali file
    public void saveAttachments(List<Uri> filesList, List<String> fileNames, CallbackFunction<List<String>> callback) {
        callback.apply(new ArrayList<>());
    }

    // Si ottiene la lista dei file con link per il download attraverso un'Entity di tipo Thesis
    public Observable<List<Attachment>> getAttachmentsByThesis(Thesis thesis) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<String> filesIds = thesis.attachments;

            List<Attachment> attachments = database.stream().filter(attachment -> filesIds.contains(attachment.id)).collect(Collectors.toList());
            next.apply(attachments);
        });
    }

    private AttachmentService() {
        database = Arrays.asList(
                new Attachment(
                        "file1",
                        null,
                        "file_1.pdf",
                        FileType.document
                ),
                new Attachment(
                        "file2",
                        null,
                        "file_2.pdf",
                        FileType.image
                )
        );
    }

    public static AttachmentService getInstance() {
        if (instance == null) {
            instance = new AttachmentService();
        }

        return instance;
    }
}
