package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Teacher;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class TeacherService {
    private final List<Teacher> database;
    private static TeacherService instance;

    public final Observable<Teacher> teacherObservable = new Observable<>(null);

    // Inizializzo i servizi di Firebase
    private void initData() {
        UserService.getInstance().userObservable.subscribe((user) -> {
            if (user != null && user.userType == UserTypes.TEACHER){
                getTeacherByUid(user.uid);
            }
        });
    }

    // Ottengo il professore tramite l'id utente
    public void getTeacherByUid(String uid) {
        Teacher teacherData = database.stream().filter(teacher -> teacher.uid.equals(uid)).findAny().orElse(null);
        teacherObservable.next(teacherData);
    }

    // Salvo l'entity Teacher associata all'utente loggato
    public void saveTeacher(User user, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    private TeacherService() {
        database = Arrays.asList(new Teacher("rf1Y5RWeiyg6NItgdtrptH01pCy2"));

        initData();
    }

    public static TeacherService getInstance() {
        if (instance == null) {
            instance = new TeacherService();
        }

        return instance;
    }
}
