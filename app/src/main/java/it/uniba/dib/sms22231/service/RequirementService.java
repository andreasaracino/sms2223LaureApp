package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class RequirementService {
    private static final String COLLECTION_NAME = "requirements";
    private static RequirementService instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference requirementsCollection = db.collection(COLLECTION_NAME);

    public Observable<List<Requirement>> getRequirementsByThesisId(String thesisId) {
        return new Observable<>((next) -> {
            requirementsCollection.whereEqualTo("thesisId", thesisId).get().addOnCompleteListener(task -> {
                List<Requirement> requirements = new ArrayList<>();
                QuerySnapshot querySnapshot = task.getResult();

                for (QueryDocumentSnapshot requirementDoc : querySnapshot) {
                    Requirement requirement = new Requirement(requirementDoc.getData());
                    requirements.add(requirement);
                }

                next.apply(requirements);
            });
        });
    }

    public void addRequirements(List<Requirement> requirements, String thesisId, CallbackFunction<Boolean> callback) {
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

    private RequirementService() {}

    public static RequirementService getInstance() {
        if (instance == null) {
            instance = new RequirementService();
        }

        return instance;
    }
}