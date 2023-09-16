package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ThesisService {
    private static final String COLLECTION_NAME = "theses";
    private static ThesisService instance;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference thesesCollection = db.collection(COLLECTION_NAME);
    private final UserService userService = UserService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private final Observable<List<Thesis>> userOwnTheses = new Observable<>(null);

    private ThesisService() {}

    public void getUserOwnTheses() {
        String uid = userService.getUserData().uid;

        thesesCollection.whereEqualTo("teacherId", uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Thesis> theses = mapThesesResult(task.getResult());
                userOwnTheses.next(theses);
            }
        });
    }

    public Observable<List<Thesis>> getSavedTheses() {
        Observable<List<Thesis>> savedTheses = new Observable<>();
        Student student = studentService.getStudentData();

        thesesCollection.whereIn("id", student.savedThesesIds).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Thesis> theses = new ArrayList<>();
                QuerySnapshot result = task.getResult();

                for (QueryDocumentSnapshot rawThesis : result) {
                    theses.add(new Thesis(rawThesis.getData()));
                }

                savedTheses.next(theses);
            }
        });

        return savedTheses;
    }

    public void getThesesById(List<String> ids, CallbackFunction<List<Thesis>> callback) {
        thesesCollection.whereIn("id", ids).get().addOnCompleteListener(task -> {
            callback.apply(mapThesesResult(task.getResult()));
        });
    }

    public void saveNewThesis(Thesis thesis, List<Requirement> requirements, List<Uri> attachments, CallbackFunction<Boolean> callback) {
        thesesCollection.add(thesis).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference savedThesis = task.getResult();
                attachmentService.saveAttachments(attachments, savedFiles -> {
                    savedThesis.update("attachmentIds", savedFiles);
                    callback.apply(savedFiles.size() > 0);
                });

                requirementService.addRequirements(requirements, savedThesis.getId(), callback);
            }
        });
    }

    public void modifyThesis(Thesis thesis, CallbackFunction<Boolean> callback) {
        thesesCollection.document(thesis.id).set(thesis).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    private List<Thesis> mapThesesResult(QuerySnapshot querySnapshot) {
        ArrayList<Thesis> theses = new ArrayList<>();

        for (QueryDocumentSnapshot thesisRaw : querySnapshot) {
            Thesis thesis = new Thesis(thesisRaw.getData());
            thesis.id = thesisRaw.getId();
            theses.add(thesis);
        }

        return theses;
    }

    public static ThesisService getInstance() {
        if (instance == null) {
            instance = new ThesisService();
        }

        return instance;
    }
}
