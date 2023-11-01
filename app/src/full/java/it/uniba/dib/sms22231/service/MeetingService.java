package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class MeetingService {
    private static MeetingService instance;
    private static final String MEETING_COLLECTION = "meetings";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference meetingCollection = db.collection(MEETING_COLLECTION);

    // Ottengo un meeting attraverso l'id
    public Observable<Meeting> getMeetingById(String meetingId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            meetingCollection.document(meetingId).get().addOnCompleteListener(task -> {
                next.apply(mapMeeting(task.getResult()));
            });
        });
    }

    // mappatura dell'Entity di tipo Meeting
    private Meeting mapMeeting(DocumentSnapshot rawMeeting) {
        Meeting meeting = new Meeting(rawMeeting.getData());
        meeting.id = rawMeeting.getId();
        return meeting;
    }

    // Ottengo la lista dei meeting di una specifica Application
    public Observable<List<Meeting>> getMeetingsByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            meetingCollection.whereEqualTo("applicationId", applicationId).get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                if (documents.size() == 0) {
                    next.apply(new ArrayList<>());
                } else {
                    List<Meeting> meetings = documents.stream().map(this::mapMeeting).collect(Collectors.toList());

                    next.apply(meetings);
                }
            });
        });
    }

    // Salvataggio di una nuova Entity
    public void saveNewMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        meetingCollection.add(meeting.toMap()).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    // Aggiornamento di un'Entity esistente
    public void updateMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        meetingCollection.document(meeting.id).set(meeting.toMap()).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    // Elimiazione di un'Entity tramite id
    public void deleteMeeting(String meetingId, CallbackFunction<Boolean> callback) {
        meetingCollection.document(meetingId).delete().addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    private MeetingService() {}

    public static MeetingService getInstance() {
        if (instance == null) {
            instance = new MeetingService();
        }

        return instance;
    }
}
