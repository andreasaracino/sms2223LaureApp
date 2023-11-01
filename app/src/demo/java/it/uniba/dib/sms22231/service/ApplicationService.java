package it.uniba.dib.sms22231.service;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ApplicationService {
    private final List<Application> database;
    private static ApplicationService instance;
    private final ThesisService thesisService = ThesisService.getInstance();

    // Ottengo la lista delle richieste filtrate per stato (pending o approved)
    public Observable<List<Application>> getAllApplicationsByStatus(ApplicationStatus applicationStatus) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            thesisService.userOwnTheses.reset();
            thesisService.userOwnTheses.subscribe(theses -> {
                if (theses.size() == 0) {
                    next.apply(new ArrayList<>());
                    return;
                }
                List<String> thesesIds = theses.stream().map(thesis -> thesis.id).collect(Collectors.toList());
                List<Application> applications = database.stream().filter(application -> thesesIds.contains(application.thesisId) && application.status == applicationStatus).collect(Collectors.toList());
                next.apply(applications);
            });

            thesisService.getUserOwnTheses();
        });
    }

    // Ottengo una singola richiesta per id
    public Observable<Application> getApplicationById(String id) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            Optional<Application> application = database.stream().filter(a -> Objects.equals(a.id, id)).findFirst();

            next.apply(application.orElse(null));
        });
    }

    // salvataggio della richiesta passando una Entity di tipo Application
    public void createApplication(Application application, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Salvataggio del nuovo stato di una richiesta (approved o rejected) e invio di un messaggio di servizio alla chat associata
    public void setNewApplicationStatus(Context context, Application application, ApplicationStatus status, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    private ApplicationService() {
        database = Arrays.asList(
                new Application(
                        "X7vND9g2r5o3GSHW5iDx",
                        ApplicationStatus.approved,
                        "Y2fqZanpstgsL8uyVI8F",
                        "V1hHe9O2Oge3VpVP1cvmyGIlFqR2",
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
                        "Intent e comunicazione tra Activity",
                        "Andrea Saracino"
                ),
                new Application(
                        "icUSwn3MR6XM6opQe90a",
                        ApplicationStatus.pending,
                        "phdDlcqWXcBbnKNdepHM",
                        "6hEhKQEz5rgUTdcWfUP46w2lasn1",
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
                                        "25"
                                )
                        ),
                        "Linguaggi regolari",
                        "Antonio Bianchi"
                )
        );
    }

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }

        return instance;
    }
}
