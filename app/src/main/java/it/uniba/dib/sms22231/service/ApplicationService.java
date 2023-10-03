package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ApplicationService {
    private static ApplicationService instance;
    private static final String APPLICATIONS_COLLECTION = "applications";
    private final FirebaseFirestore db;
    private final CollectionReference applicationsCollection;
    private final ThesisService thesisService = ThesisService.getInstance();
    private final UserService userService = UserService.getInstance();

    public Observable<List<Application>> getAllApplications() {
        return new Observable<>(next -> {
            thesisService.userOwnTheses.subscribe(theses -> {
                List<String> thesesIds = theses.stream().map(thesis -> thesis.id).collect(Collectors.toList());
                applicationsCollection.whereIn("thesisId", thesesIds).get().addOnCompleteListener(task -> {
                    mapApplications(task.getResult(), theses, next);
                });
            });

            thesisService.getUserOwnTheses();
        });
    }

    public Observable<Application> getApplicationById(String id) {
        return new Observable<>((next) -> {
            applicationsCollection.document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mapApplication(task.getResult().getId(), task.getResult().getData(), next);
                }
            });
        });
    }

    public void createApplication(Application application, CallbackFunction<Boolean> callback) {
        applicationsCollection.document().set(application).addOnCompleteListener(task -> {
            callback.apply(task.isSuccessful());
        });
    }

    private void mapApplications(QuerySnapshot querySnapshot, List<Thesis> theses, CallbackFunction<List<Application>> callback) {
        List<Application> applications = new ArrayList<>();

        for (QueryDocumentSnapshot rawApplication : querySnapshot) {
            mapApplication(theses, rawApplication.getId(), rawApplication.getData(), application -> {
                applications.add(application);

                if (applications.size() == querySnapshot.size()) {
                    callback.apply(applications);
                }
            });
        }

    }

    private void mapApplication(List<Thesis> theses, String documentId, Map<String, Object> rawApplication, CallbackFunction<Application> callback) {
        Application application = new Application(rawApplication);
        application.id = documentId;
        Optional<Thesis> linkedThesisOptional = theses.stream().filter(thesis -> Objects.equals(thesis.id, application.thesisId)).findAny();
        if (linkedThesisOptional.isPresent()) {
            Thesis linkedThesis = linkedThesisOptional.get();
            application.thesisTitle = linkedThesis.title;
        }

        userService.getUserByUid(application.studentUid, user -> {
            application.studentName = user.fullName;
            callback.apply(application);
        });
    }

    private void mapApplication(String documentId, Map<String, Object> rawApplication, CallbackFunction<Application> callback) {
        Application application = new Application(rawApplication);
        application.id = documentId;
        final Boolean[] completed = {false};

        thesisService.getThesisById(application.thesisId, thesis -> {
            application.thesisTitle = thesis.title;
            if (completed[0]) {
                callback.apply(application);
            } else {
                completed[0] = true;
            }
        });

        userService.getUserByUid(application.studentUid, user -> {
            application.studentName = user.fullName;
            if (completed[0]) {
                callback.apply(application);
            } else {
                completed[0] = true;
            }
        });
    }

    private ApplicationService() {
        db = FirebaseFirestore.getInstance();
        applicationsCollection = db.collection(APPLICATIONS_COLLECTION);
    }

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }

        return instance;
    }
}
