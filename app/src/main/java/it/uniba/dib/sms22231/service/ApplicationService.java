package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
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

    public Observable<Application> getStudentApplication(String studentUid) {
        return new Observable<>((next) -> {
            applicationsCollection.whereEqualTo("studentUid", studentUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Application application = new Application(task.getResult().getDocuments().get(0).getData());
                    next.apply(application);
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
            Application application = new Application(rawApplication.getData());
            Optional<Thesis> linkedThesisOptional = theses.stream().filter(thesis -> Objects.equals(thesis.id, application.thesisId)).findAny();
            if (linkedThesisOptional.isPresent()) {
                Thesis linkedThesis = linkedThesisOptional.get();
                application.thesisTitle = linkedThesis.title;
            }

            userService.getUserByUid(application.studentUid, user -> {
                application.studentName = user.fullName;
                applications.add(application);

                if (applications.size() == querySnapshot.size()) {
                    callback.apply(applications);
                }
            });
        }

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
