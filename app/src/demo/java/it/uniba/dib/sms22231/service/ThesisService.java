package it.uniba.dib.sms22231.service;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.ChangeTypes;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Change;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ThesisService {
    private final List<Thesis> database;
    private static ThesisService instance;

    private final UserService userService = UserService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    public final Observable<List<Thesis>> userOwnTheses = new Observable<>(null);

    // Ottengo la lista di tutte le tesi salvate
    public Observable<List<Thesis>> getAllTheses() {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Thesis> theses = database.stream().sorted(Comparator.comparing(thesis -> thesis.title.toUpperCase())).collect(Collectors.toList());
            next.apply(theses);
        });
    }

    // Ottengo la lista delle tesi appartenenti all'utente corrente
    public void getUserOwnTheses() {
        String uid = userService.getUserData().uid;

        List<Thesis> theses = database.stream().filter(thesis -> thesis.teacherId.equals(uid)).collect(Collectors.toList());
        userOwnTheses.next(theses);
    }

    // Ottengo la lista delle tesi preferite associate allo studente loggato
    public Observable<List<Thesis>> getSavedTheses() {
        Observable<List<Thesis>> savedTheses = new Observable<>();

        studentService.updateStudent().subscribe(student -> {
            if (student.savedThesesIds.size() == 0) {
                savedTheses.next(new ArrayList<>());
                return;
            }

            List<Thesis> theses = new ArrayList<>();

            student.savedThesesIds.values().stream().sorted().forEach(order -> {
                String thesisId = student.savedThesesIds.get(order);
                Optional<Thesis> thesisOptional = database.stream().filter(thesis -> thesis.id.equals(thesisId)).findAny();
                thesisOptional.ifPresent(theses::add);
            });

            savedTheses.next(theses);
        });

        return savedTheses;
    }

    // Ottengo una specifica tesi per id
    public void getThesisById(String id, CallbackFunction<Thesis> callback) {
        Optional<Thesis> taskOptional = database.stream().filter(thesis -> thesis.id.equals(id)).findAny();
        callback.apply(taskOptional.orElse(null));
    }

    // Salvo una nuova tesi passando gli allegati e i requisiti
    public void saveNewThesis(Thesis thesis, List<Requirement> requirements, List<Uri> attachments, List<String> fileNames, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Aggiorno una tesi esistente passando le liste degli eventuali cambiamenti effettuati agli allegati e ai requisiti
    public void updateThesis(Thesis thesis, List<Change<Attachment>> changedAttachments, List<Change<Requirement>> changedRequirements, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    private ThesisService() {
        database = Arrays.asList(
                new Thesis(
                        "Y2fqZanpstgsL8uyVI8F",
                        "Intent e comunicazione tra Activity",
                        "La tesi consiste nel descrivere come funzionano gli intent e come questi permettono la comunicazione tra le diverse Activity",
                        "Mario Rossi",
                        "rf1Y5RWeiyg6NItgdtrptH01pCy2",
                        "Marcello Piteo",
                        Arrays.asList("file1", "file2"),
                        Arrays.asList(
                                new Requirement(
                                        "WaMzut2aE1KpJcpGDVPX",
                                        "Y2fqZanpstgsL8uyVI8F",
                                        RequirementTypes.exam,
                                        "Sviluppo Mobile Software"
                                ),
                                new Requirement(
                                        "jkx7f7L85fsGt7VkPyBE",
                                        "Y2fqZanpstgsL8uyVI8F",
                                        RequirementTypes.exam,
                                        "Programmazione 2"
                                ),
                                new Requirement(
                                        "nyUT6X6mmrJYxnix59PO",
                                        "Y2fqZanpstgsL8uyVI8F",
                                        RequirementTypes.average,
                                        "22"
                                )
                        ),
                        22
                ),
                new Thesis(
                        "phdDlcqWXcBbnKNdepHM",
                        "Linguaggi regolari",
                        "La tesi ha lo scopo di mostrare come si possono generare dei linguaggi utilizzando la grammatica di Chomsky",
                        "Giuseppe Verdi",
                        "rf1Y5RWeiyg6NItgdtrptH01pCy2",
                        "Marcello Piteo",
                        Arrays.asList("file2"),
                        Arrays.asList(
                                new Requirement(
                                        "F0Kvngsb0OXvAp82I72X",
                                        "phdDlcqWXcBbnKNdepHM",
                                        RequirementTypes.exam,
                                        "Programmazione 1"
                                ),
                                new Requirement(
                                        "eXsFfc1XObB0LMwC8J4H",
                                        "phdDlcqWXcBbnKNdepHM",
                                        RequirementTypes.exam,
                                        "Linguaggi di programmazione"
                                ),
                                new Requirement(
                                        "n3SMoafUyiZKnilFmGS1",
                                        "phdDlcqWXcBbnKNdepHM",
                                        RequirementTypes.average,
                                        "28"
                                )
                        ),
                        28
                )
        );
    }

    public static ThesisService getInstance() {
        if (instance == null) {
            instance = new ThesisService();
        }

        return instance;
    }

    public void removeThesis(String thesisId, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }
}
