package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.config.UserTypes;
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
    private CollectionReference studentsCollection;
    private DocumentReference studentDocument;
    private Map<String, Object> studentRawData;
    private Student studentData;
    public Observable<Student> studentObservable = new Observable<>(null);

    private StudentService() {
        initData();
    }

    // Inizializzo i servizi di Firebase
    private void initData() {
        db = FirebaseFirestore.getInstance();
        studentsCollection = db.collection(COLLECTION_NAME);
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            if (user != null && user.userType == UserTypes.STUDENT) {
                getStudentByUid(user.uid);
            }
        });
    }

    // Ottengo il document dello studente in base all'id utente
    private void getStudentByUid(String uid) {
        studentDocument = studentsCollection.document(uid);
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

            studentDocument.update("savedThesesIds", student.savedThesesIds).addOnCompleteListener(task -> {
                updateStudent();
                callback.apply(!isFavorite);
                unsubscribe.apply();
            });
        });
    }

    // Aggiorno l'ordine delle tesi preferite in base all'impostazione dell'utente
    public void saveNewFavoritesOrder(List<String> savedTheses) {
        Map<String, String> thesesIdsMap = new HashMap<>();

        AtomicReference<Integer> i = new AtomicReference<>(0);
        savedTheses.forEach(thesisId -> {
            thesesIdsMap.put(i.getAndSet(i.get() + 1).toString(), thesisId);
        });
        studentDocument.update("savedThesesIds", thesesIdsMap).addOnCompleteListener(task -> updateStudent());
    }

    // Aggiorno l'observable dello studente
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

    // Aggiorno l'Application associata allo studente
    public void saveNewCurrentApplicationId(String studentUid, String applicationId) {
        studentsCollection.document(studentUid).update("currentApplicationId", applicationId).addOnCompleteListener(task -> updateStudent());
    }

    // Controllo se una specifica tesi è tra i preferiti dello studente
    public boolean isThesisFavorite(Thesis thesis) {
        return studentData.savedThesesIds != null && studentData.savedThesesIds.containsValue(thesis.id);
    }

    // Salvataggio dell'Entity studente associata all'utente loggato
    public void saveStudent(User user, CallbackFunction<Boolean> callback) {
        if (studentDocument == null) {
            studentDocument = studentsCollection.document(user.uid);
        }

        studentDocument.get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                studentDocument.set(new Student(user.uid, new HashMap<>())).addOnCompleteListener(task1 -> callback.apply(task1.isSuccessful()));
                updateStudent();
            } else {
                callback.apply(true);
            }
        });
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
