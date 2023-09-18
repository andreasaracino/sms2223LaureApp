package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import it.uniba.dib.sms22231.model.Teacher;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TeacherService {
    private static final String COLLECTION_NAME = "teachers";
    private static TeacherService instance;

    private FirebaseFirestore db;
    private UserService userService;
    private DocumentReference teacherDocument;
    private Map<String, Object> teacherRawData;
    private Teacher teacherData;
    public final Observable<Teacher> teacherObservable = new Observable<>(null);

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
                teacherDocument = null;
            }
        });
    }

    public void getTeacherByUid(String uid) {
        teacherDocument = db.collection(COLLECTION_NAME).document(uid);
        teacherDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                teacherRawData = task.getResult().getData();
                teacherData = new Teacher(teacherRawData);
                teacherObservable.next(teacherData);
            }
        });
    }

    public void saveTeacher(User user, CallbackFunction<Boolean> callback) {
        teacherDocument.set(new Teacher(user.uid)).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    public static TeacherService getInstance() {
        if (instance == null) {
            instance = new TeacherService();
        }

        return instance;
    }
}
