package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms22231.model.Teacher;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TeacherService {
    private static final String COLLECTION_NAME = "teachers";
    private static TeacherService instance;

    private FirebaseFirestore db;
    private UserService userService;
    private DocumentReference studentDocument;
    private Map<String, Object> teacherRawData;
    private Teacher teacherData;
    private final Observable<Teacher> teacherObservable = new Observable<>(null);

    private TeacherService() {
        initData();
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            if (user != null) {
                getTeacherByUid(user.uid);
            } else {
                studentDocument = null;
            }
        });
    }

    private void getTeacherByUid(String uid) {
        studentDocument = db.collection(COLLECTION_NAME).document(uid);
        studentDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                teacherRawData = task.getResult().getData();
                teacherData = new Teacher(Objects.requireNonNull(teacherRawData));
                teacherObservable.next(teacherData);
            }
        });
    }

    private void saveTeacher(Teacher teacher, CallbackFunction<Boolean> callback) {
        if (studentDocument != null) {
            studentDocument.set(teacher).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
        }
    }

    public static TeacherService getInstance() {
        if (instance == null) {
            instance = new TeacherService();
        }

        return instance;
    }
}
