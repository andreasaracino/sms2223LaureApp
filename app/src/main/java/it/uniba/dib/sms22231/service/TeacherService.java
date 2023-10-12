package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Teacher;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TeacherService {
    private static final String COLLECTION_NAME = "teachers";
    private static TeacherService instance;

    private FirebaseFirestore db;
    private UserService userService;
    private CollectionReference teacherCollection;
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
        teacherCollection = db.collection(COLLECTION_NAME);
        userService.userObservable.subscribe((user) -> {
            if (user != null && user.userType == UserTypes.TEACHER){
                getTeacherByUid(user.uid);
            } else {
                teacherDocument = null;
            }
        });
    }

    public void getTeacherByUid(String uid) {
        teacherDocument = teacherCollection.document(uid);
        teacherDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                teacherRawData = task.getResult().getData();
                teacherData = new Teacher(teacherRawData);
                teacherObservable.next(teacherData);
            }
        });
    }

    public void saveTeacher(User user, CallbackFunction<Boolean> callback) {

        if (teacherDocument == null) {
            teacherDocument = teacherCollection.document(user.uid);
        }

        teacherDocument.get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                teacherDocument.set(new Teacher(user.uid)).addOnCompleteListener(task1 -> callback.apply(task1.isSuccessful()));
            } else {
                callback.apply(true);
            }
        });
    }

    public static TeacherService getInstance() {
        if (instance == null) {
            instance = new TeacherService();
        }

        return instance;
    }
}
