package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.List;

import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.utility.Observable;

public class ApplicationService {
    private static ApplicationService instance;
    private static final String APPLICATIONS_COLLECTION = "applications";
    private final FirebaseFirestore db;
    private final CollectionReference applicationsCollection;

    private ApplicationService() {
        db = FirebaseFirestore.getInstance();
        applicationsCollection = db.collection(APPLICATIONS_COLLECTION);
    }

    private Observable<Application> getStudentApplication(String studentUid) {
        return new Observable<>((next) -> {
            applicationsCollection.whereEqualTo("studentUid", studentUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Application application = new Application(task.getResult().getDocuments().get(0).getData());
                    next.apply(application);
                }
            });
        });
    }

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }

        return instance;
    }
}
