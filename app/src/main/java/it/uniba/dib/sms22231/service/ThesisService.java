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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.ChangeTypes;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Change;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Student;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ThesisService {
    private static final String COLLECTION_NAME = "theses";
    private static ThesisService instance;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference thesesCollection = db.collection(COLLECTION_NAME);
    private final UserService userService = UserService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    public final Observable<List<Thesis>> userOwnTheses = new Observable<>(null);

    private ThesisService() {}

    public Observable<List<Thesis>> getAllTheses() {
        return new Observable<>((next, setOnUnsubscribe) -> {
            thesesCollection.orderBy("title", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                mapThesesResult(documents.stream().collect(Collectors.toMap(documents::indexOf, Function.identity())), next);
            });
        });
    }

    public void getUserOwnTheses() {
        String uid = userService.getUserData().uid;

        thesesCollection.whereEqualTo("teacherId", uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                mapThesesResult(documents.stream().collect(Collectors.toMap(documents::indexOf, Function.identity())), userOwnTheses::next);
            }
        });
    }

    public Observable<List<Thesis>> getSavedTheses() {
        Observable<List<Thesis>> savedTheses = new Observable<>();

        studentService.updateStudent().subscribe(student -> {
            if (student.savedThesesIds.size() == 0) {
                savedTheses.next(new ArrayList<>());
                return;
            }

            thesesCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<Integer, DocumentSnapshot> rawTheses = new HashMap<>();
                    QuerySnapshot result = task.getResult();

                    student.savedThesesIds.keySet().forEach(thesisIndex -> {
                        Optional<DocumentSnapshot> thesisDoc = result.getDocuments().stream().filter(doc -> doc.getId().equals(student.savedThesesIds.get(thesisIndex))).findFirst();
                        thesisDoc.ifPresent(doc -> rawTheses.put(Integer.parseInt(thesisIndex), doc));
                    });

                    mapThesesResult(rawTheses, savedTheses::next);
                }
            });
        });

        return savedTheses;
    }

    public void getThesisById(String id, CallbackFunction<Thesis> callback) {
        thesesCollection.document(id).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            mapThesis(documentSnapshot.getId(), documentSnapshot.getData(), callback);
        });
    }

    public void saveNewThesis(Thesis thesis, List<Requirement> requirements, List<Uri> attachments, List<String> fileNames, CallbackFunction<Boolean> callback) {
        thesesCollection.add(thesis.toMap()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentReference savedThesis = task.getResult();
                attachmentService.saveAttachments(attachments, fileNames, savedFiles -> {
                    savedThesis.update("attachmentIds", savedFiles);
                    callback.apply(true);
                });

                requirementService.addRequirements(requirements, savedThesis.getId(), callback);
            }
        });
    }

    public void updateThesis(Thesis thesis, List<Change<Attachment>> changedAttachments, List<Change<Requirement>> changedRequirements, CallbackFunction<Boolean> callback) {
        DocumentReference thesisDocument = thesesCollection.document(thesis.id);
        thesesCollection.document(thesis.id).set(thesis.toMap()).addOnCompleteListener(task -> {
            final boolean[] success = {false};

            updateAttachments(changedAttachments, thesis, thesisDocument, isSuccessful -> {
                if (success[0] && isSuccessful) {
                    callback.apply(true);
                } else {
                    success[0] = true;
                }
            });

            updateRequirements(changedRequirements, thesis, isSuccessful -> {
                if (success[0] && isSuccessful) {
                    callback.apply(true);
                } else {
                    success[0] = true;
                }
            });
        });
    }

    private void updateAttachments(List<Change<Attachment>> changedAttachments, Thesis thesis, DocumentReference thesisDocument, CallbackFunction<Boolean> callbackFunction) {
        List<Uri> newAttachments = changedAttachments.stream().filter(attachmentChange -> attachmentChange.changeType == ChangeTypes.added).map(attachmentChange -> attachmentChange.value.path).collect(Collectors.toList());
        List<String> fileNames = changedAttachments.stream().filter(attachmentChange -> attachmentChange.changeType == ChangeTypes.added).map(attachmentChange -> attachmentChange.value.getFileName()).collect(Collectors.toList());
        List<String> removedAttachments = changedAttachments.stream().filter(attachmentChange -> attachmentChange.changeType == ChangeTypes.removed).map(attachmentChange -> attachmentChange.value.id).collect(Collectors.toList());

        attachmentService.saveAttachments(newAttachments, fileNames, savedFiles -> {
            thesis.attachments.removeAll(removedAttachments);
            thesis.attachments.addAll(savedFiles);
            thesisDocument.update("attachmentIds", thesis.attachments).addOnCompleteListener(task -> {
                callbackFunction.apply(task.isSuccessful());
            });
        });
    }

    private void updateRequirements(List<Change<Requirement>> changedRequirements, Thesis thesis, CallbackFunction<Boolean> callbackFunction) {
        List<Requirement> newRequirements = changedRequirements.stream().filter(requirementChange -> requirementChange.changeType == ChangeTypes.added).map(requirementChange -> requirementChange.value).collect(Collectors.toList());
        List<String> removedRequirements = changedRequirements.stream().filter(requirementChange -> requirementChange.changeType == ChangeTypes.removed).map(requirementChange -> requirementChange.value.id).collect(Collectors.toList());

        final boolean[] success = {false};

        requirementService.removeRequirements(removedRequirements, isSuccessful -> {
            if (success[0] && isSuccessful) {
                callbackFunction.apply(true);
            } else {
                success[0] = true;
            }
        });

        requirementService.addRequirements(newRequirements, thesis.id, isSuccessful -> {
            if (success[0] && isSuccessful) {
                callbackFunction.apply(true);
            } else {
                success[0] = true;
            }
        });
    }

    private void mapThesesResult(Map<Integer, DocumentSnapshot> documentSnapshots, CallbackFunction<List<Thesis>> callback) {
        Map<Integer, Thesis> theses = new HashMap<>();

        if (documentSnapshots.size() == 0) {
            callback.apply(new ArrayList<>());
            return;
        }

        for (Map.Entry<Integer, DocumentSnapshot> thesisRaw : documentSnapshots.entrySet()) {
            Integer index = thesisRaw.getKey();
            DocumentSnapshot data = thesisRaw.getValue();
            mapThesis(data.getId(), data.getData(), thesis -> {
                theses.put(index, thesis);

                if (theses.size() == documentSnapshots.size()) {
                    callback.apply(theses.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.toList()));
                }
            });
        }

    }

    private void mapThesis(String id, Map<String, Object> data, CallbackFunction<Thesis> callback) {
        Thesis thesis = new Thesis(data);
        thesis.id = id;

        final boolean[] successful = {false};

        userService.getUserByUid(thesis.teacherId, user -> {
            thesis.teacherFullname = user.fullName;

            if (successful[0]) {
                callback.apply(thesis);
            } else {
                successful[0] = true;
            }
        });

        requirementService.getAverageRequirementByThesis(id, averageRequirement -> {
            thesis.averageRequirement = averageRequirement;

            if (successful[0]) {
                callback.apply(thesis);
            } else {
                successful[0] = true;
            }
        });
    }

    public static ThesisService getInstance() {
        if (instance == null) {
            instance = new ThesisService();
        }

        return instance;
    }

    public void removeThesis(String thesisId, CallbackFunction<Boolean> callback) {
        thesesCollection.document(thesisId).delete().addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }
}
