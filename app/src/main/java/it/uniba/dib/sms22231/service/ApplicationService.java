package it.uniba.dib.sms22231.service;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Message;
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
    private final StudentService studentService = StudentService.getInstance();
    private final ChatService chatService = ChatService.getInstance();

    public Observable<List<Application>> getAllApplicationsByStatus(ApplicationStatus applicationStatus) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            thesisService.userOwnTheses.reset();
            thesisService.userOwnTheses.subscribe(theses -> {
                if (theses.size() == 0) {
                    next.apply(new ArrayList<>());
                    return;
                }
                List<String> thesesIds = theses.stream().map(thesis -> thesis.id).collect(Collectors.toList());
                applicationsCollection.whereIn("thesisId", thesesIds).whereEqualTo("status", applicationStatus).get().addOnCompleteListener(task -> {
                    mapApplications(task.getResult(), theses, next);
                });
            });

            thesisService.getUserOwnTheses();
        });
    }

    public Observable<Application> getApplicationById(String id) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            applicationsCollection.document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mapApplication(task.getResult().getId(), task.getResult().getData(), next);
                }
            });
        });
    }

    public void createApplication(Application application, CallbackFunction<Boolean> callback) {
        applicationsCollection.add(application).addOnCompleteListener(task -> {
            studentService.saveNewCurrentApplicationId(userService.getUserData().uid, task.getResult().getId());
            callback.apply(task.isSuccessful());
        });
    }

    public void setNewApplicationStatus(Context context, Application application, ApplicationStatus status, CallbackFunction<Boolean> callback) {
        applicationsCollection.document(application.id).update("status", status).addOnCompleteListener(task -> {
            if (status == ApplicationStatus.rejected) {
                studentService.saveNewCurrentApplicationId(application.studentUid, null);
            }

            getApplicationStatusMessage(context, application, status, message -> {
                chatService.sendApplicationStatusUpdate(application, message);
            });
            callback.apply(task.isSuccessful());
        });
    }

    private void mapApplications(QuerySnapshot querySnapshot, List<Thesis> theses, CallbackFunction<List<Application>> callback) {
        List<Application> applications = new ArrayList<>();

        if (querySnapshot.isEmpty()) {
            callback.apply(applications);
            return;
        }

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

    private void getApplicationStatusMessage(Context context, Application application, ApplicationStatus status, CallbackFunction<String> callback) {
        int itResId = status == ApplicationStatus.approved ? R.string.applicationApproved_it : R.string.applicationRejected_it;
        int enResId = status == ApplicationStatus.approved ? R.string.applicationApproved_en : R.string.applicationRejected_en;

        thesisService.getThesisById(application.thesisId, thesis -> {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("it", context.getString(itResId, thesis.title, userService.getUserData().fullName));
                jsonObject.put("en", context.getString(enResId, thesis.title, userService.getUserData().fullName));
                callback.apply(jsonObject.toString());
            } catch (JSONException e) {}
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
