package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

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
        studentDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                studentRawData = task.getResult().getData();
                studentData = new Student(studentRawData);
                studentObservable.next(studentData);
            }
        });
    }

    public void addThesisToFavourites(Thesis thesis, CallbackFunction<Boolean> callback) {
        if (studentData.savedThesesIds.contains(thesis.id)) {
            studentData.savedThesesIds.remove(thesis.id);
        } else {
            studentData.savedThesesIds.add(thesis.id);
        }
        studentDocument.update("savedThesesIds", studentData.savedThesesIds).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    public boolean isThesisFavorite(Thesis thesis) {
        return studentData.savedThesesIds.contains(thesis.id);
    }

    public void saveStudent(User user, CallbackFunction<Boolean> callback) {
        studentDocument.set(new Student(user.uid, new ArrayList<>())).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
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
