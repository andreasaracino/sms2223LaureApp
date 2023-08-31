package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.model.Teacher;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ThesisService {
    private static final String COLLECTION_NAME = "theses";
    private static ThesisService instance;

    private FirebaseFirestore db;
    private UserService userService;
    private CollectionReference thesesCollection;
    private final Observable<List<Thesis>> userOwnTheses = new Observable<>(null);

    private ThesisService() {
        initData();
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        userService = UserService.getInstance();
        thesesCollection = db.collection(COLLECTION_NAME);
    }

    public void getUserOwnTheses() {
        String uid = userService.userObservable.getValue().uid;

        thesesCollection.whereEqualTo("teacherId", uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Thesis> theses = mapThesesResult(task.getResult());
                userOwnTheses.next(theses);
            }
        });
    }

    public void getThesesById(List<String> ids, CallbackFunction<List<Thesis>> callback) {
        thesesCollection.whereIn("id", ids).get().addOnCompleteListener(task -> {
            callback.apply(mapThesesResult(task.getResult()));
        });
    }

    public void saveNewThesis(Thesis thesis, CallbackFunction<Boolean> callback) {
        thesesCollection.add(thesis).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
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
