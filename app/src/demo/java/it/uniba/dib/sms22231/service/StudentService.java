package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class StudentService {
    private final List<Student> database;
    private static StudentService instance;

    private UserService userService;
    private Map<String, Object> studentRawData;
    private Student studentData;
    public Observable<Student> studentObservable = new Observable<>(null);


    // Inizializzo i servizi di Firebase
    private void initData() {
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            if (user != null && user.userType == UserTypes.STUDENT) {
                getStudentByUid(user.uid);
            }
        });
    }

    // Ottengo il document dello studente in base all'id utente
    private void getStudentByUid(String uid) {
        Optional<Student> studentOptional = database.stream().filter(student -> student.uid.equals(uid)).findAny();
        studentData = studentOptional.orElse(null);
        updateStudent();
    }

    // Aggiungo l'id di una tesi nella lista di tesi preferite dell'Entity dello studente loggato
    public void addThesisToFavourites(Thesis thesis, CallbackFunction<Boolean> callback) {
        updateStudent().subscribe((student, unsubscribe) -> {
            boolean isFavorite = isThesisFavorite(thesis);
            if (isFavorite) {
                student.savedThesesIds.entrySet().removeIf(entry -> entry.getValue().equals(thesis.id));
            } else {
                student.savedThesesIds.put(String.valueOf(student.savedThesesIds.values().size()), thesis.id);
            }

            callback.apply(!isFavorite);
            unsubscribe.apply();
        });
    }

    // Aggiorno l'ordine delle tesi preferite in base all'impostazione dell'utente
    public void saveNewFavoritesOrder(List<String> savedTheses) {
        Map<String, String> thesesIdsMap = new HashMap<>();

        AtomicReference<Integer> i = new AtomicReference<>(0);
        savedTheses.forEach(thesisId -> {
            thesesIdsMap.put(i.getAndSet(i.get() + 1).toString(), thesisId);
        });

        studentData.savedThesesIds = thesesIdsMap;
        updateStudent();
    }

    // Aggiorno l'observable dello studente
    public Observable<Student> updateStudent() {
        studentObservable.reset();
        studentObservable.next(studentData);

        return studentObservable;
    }

    // Controllo se una specifica tesi è tra i preferiti dello studente
    public boolean isThesisFavorite(Thesis thesis) {
        return studentData.savedThesesIds != null && studentData.savedThesesIds.containsValue(thesis.id);
    }

    // Salvataggio dell'Entity studente associata all'utente loggato
    public void saveStudent(User user, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    public Student getStudentData() {
        return studentObservable.getValue();
    }

    private StudentService() {
        HashMap<String, String> savedTheses = new HashMap<>();
        savedTheses.put("1", "Y2fqZanpstgsL8uyVI8F");
        savedTheses.put("2", "phdDlcqWXcBbnKNdepHM");

        database = Arrays.asList(
                new Student("V1hHe9O2Oge3VpVP1cvmyGIlFqR2", "X7vND9g2r5o3GSHW5iDx", savedTheses),
                new Student("6hEhKQEz5rgUTdcWfUP46w2lasn1", "icUSwn3MR6XM6opQe90a", savedTheses)
        );

        initData();
    }

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }

        return instance;
    }
}
