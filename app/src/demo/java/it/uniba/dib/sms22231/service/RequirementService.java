package it.uniba.dib.sms22231.service;

import android.window.SplashScreen;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class RequirementService {
    private final List<Requirement> database;
    private static RequirementService instance;

    // Ottengo la lista dei requisiti associati a una tesi
    public Observable<List<Requirement>> getRequirementsByThesis(Thesis thesis) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Requirement> requirements = database.stream().filter(requirement -> requirement.thesisId.equals(thesis.id)).collect(Collectors.toList());
            next.apply(requirements);
        });
    }

    // Aggiunta di una lista di requisti associati a una tesi
    public void addRequirements(List<Requirement> requirements, String thesisId, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Rimozione di una lista di requisiti
    public void removeRequirements(List<String> requirementsIds, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Ottengo il requisito "media" di una specifica tesi, se esiste
    public void getAverageRequirementByThesis(String thesisId, CallbackFunction<Integer> callback) {
        Optional<Requirement> requirementOptional = database.stream().filter(requirement -> requirement.thesisId.equals(thesisId) && requirement.description == RequirementTypes.average).findFirst();
        if (requirementOptional.isPresent()) {
            callback.apply(Integer.parseInt(requirementOptional.get().value));
        } else {
            callback.apply(-1);
        }
    }

    private RequirementService() {
        database = Arrays.asList(
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
            ),
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
        );
    }

    public static RequirementService getInstance() {
        if (instance == null) {
            instance = new RequirementService();
        }

        return instance;
    }
}
