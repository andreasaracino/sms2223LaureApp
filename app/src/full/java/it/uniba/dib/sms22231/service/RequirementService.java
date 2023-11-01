package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class RequirementService {
    private static final String COLLECTION_NAME = "requirements";
    private static RequirementService instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference requirementsCollection = db.collection(COLLECTION_NAME);

    // Ottengo la lista dei requisiti associati a una tesi
    public Observable<List<Requirement>> getRequirementsByThesis(Thesis thesis) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            requirementsCollection.whereEqualTo("thesisId", thesis.id).get().addOnCompleteListener(task -> {
                List<Requirement> requirements = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                for (QueryDocumentSnapshot requirementDoc : querySnapshot) {
                    Requirement requirement = mapRequirement(requirementDoc);

                    if (requirement != null) {
                        requirements.add(requirement);
                    }
                }

                next.apply(requirements);
            });
        });
    }

    // Mappatura di un singolo requisito
    private static Requirement mapRequirement(DocumentSnapshot requirementDoc) {
        Requirement requirement = null;

        if (requirementDoc.exists() && requirementDoc.getData() != null) {
            requirement = new Requirement(requirementDoc.getData());
            requirement.id = requirementDoc.getId();
        }

        return requirement;
    }

    // Aggiunta di una lista di requisti associati a una tesi
    public void addRequirements(List<Requirement> requirements, String thesisId, CallbackFunction<Boolean> callback) {
        if (requirements.size() == 0) {
            callback.apply(true);
        }

        AtomicReference<Integer> addedRequirements = new AtomicReference<>(0);

        for (Requirement requirement : requirements) {
            requirement.thesisId = thesisId;
            requirementsCollection.add(requirement).addOnCompleteListener(task -> {
                addedRequirements.getAndSet(addedRequirements.get() + 1);
                if (addedRequirements.get() == requirements.size()) {
                    callback.apply(true);
                }
            });
        }
    }

    // Rimozione di una lista di requisiti
    public void removeRequirements(List<String> requirementsIds, CallbackFunction<Boolean> callback) {
        if (requirementsIds.size() == 0) {
            callback.apply(true);
            return;
        };

        for (String requirementId : requirementsIds) {
            requirementsCollection.document(requirementId).delete().addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
        }
    }

    // Ottengo il requisito "media" di una specifica tesi, se esiste
    public void getAverageRequirementByThesis(String thesisId, CallbackFunction<Integer> callback) {
        requirementsCollection.whereEqualTo("thesisId", thesisId).whereEqualTo("description", RequirementTypes.average).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                Requirement requirement = mapRequirement(task.getResult().getDocuments().get(0));
                callback.apply(Integer.parseInt(requirement.value));
            } else {
                callback.apply(-1);
            }
        });
    }

    private RequirementService() {}

    public static RequirementService getInstance() {
        if (instance == null) {
            instance = new RequirementService();
        }

        return instance;
    }
}
