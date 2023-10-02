package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class StudentService {
    private static final String COLLECTION_NAME = "students";
    private static StudentService instance;

    private FirebaseFirestore db;
    private UserService userService;
    private DocumentReference studentDocument;
    private Map<String, Object> studentRawData;
    private Student studentData;
    public Observable<Student> studentObservable = new Observable<>(null);

    private StudentService() {
        initData();
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            if (user != null) {
                getStudentByUid(user.uid);
            }
        });
    }

    private void getStudentByUid(String uid) {
        studentDocument = db.collection(COLLECTION_NAME).document(uid);
        updateStudent();
    }

    public void addThesisToFavourites(Thesis thesis, CallbackFunction<Boolean> callback) {
        boolean isFavorite = isThesisFavorite(thesis);
        if (isFavorite) {
            studentData.savedThesesIds.entrySet().removeIf(entry -> entry.getValue().equals(thesis.id));
        } else {
            studentData.savedThesesIds.put(String.valueOf(studentData.savedThesesIds.values().size() - 1), thesis.id);
        }

        studentDocument.update("savedThesesIds", studentData.savedThesesIds).addOnCompleteListener(task -> callback.apply(!isFavorite));
    }

    public void saveNewFavoritesOrder(List<String> savedTheses) {
        Map<String, String> thesesIdsMap = new HashMap<>();

        AtomicReference<Integer> i = new AtomicReference<>(0);
        savedTheses.forEach(thesisId -> {
            thesesIdsMap.put(i.getAndSet(i.get() + 1).toString(), thesisId);
        });
        studentDocument.update("savedThesesIds", thesesIdsMap);
    }

    public Observable<Student> updateStudent() {
        studentObservable.reset();

        studentDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                studentRawData = task.getResult().getData();
                studentData = new Student(studentRawData);
                studentObservable.next(studentData);
            }
        });

        return studentObservable;
    }

    public boolean isThesisFavorite(Thesis thesis) {
        return studentData.savedThesesIds.containsValue(thesis.id);
    }

    public void saveStudent(User user, CallbackFunction<Boolean> callback) {
        studentDocument.set(new Student(user.uid, new HashMap<>())).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    public Student getStudentData() {
        return studentObservable.getValue();
    }

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }

        return instance;
    }
}
