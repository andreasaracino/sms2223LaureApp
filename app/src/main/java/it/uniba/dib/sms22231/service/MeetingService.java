package it.uniba.dib.sms22231.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class MeetingService {
    private static MeetingService instance;
    private static final String MEETING_COLLECTION = "meetings";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference meetingCollection = db.collection(MEETING_COLLECTION);

    public Observable<Meeting> getMeetingById(String meetingId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            meetingCollection.document(meetingId).get().addOnCompleteListener(task -> {
                next.apply(mapMeeting(task.getResult()));
            });
        });
    }

    private Meeting mapMeeting(DocumentSnapshot rawMeeting) {
        Meeting meeting = new Meeting(rawMeeting.getData());
        meeting.id = rawMeeting.getId();
        return meeting;
    }

    public Observable<List<Meeting>> getMeetingsByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            meetingCollection.whereEqualTo("applicationId", applicationId).get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                List<Meeting> meetings = documents.stream().map(this::mapMeeting).collect(Collectors.toList());

                next.apply(meetings);
            });
        });
    }

    public void saveNewMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        meetingCollection.add(meeting.toMap()).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    public void updateMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        meetingCollection.document(meeting.id).set(meeting).addOnCompleteListener(task -> callback.apply(task.isSuccessful()));
    }

    private MeetingService() {}

    public static MeetingService getInstance() {
        if (instance == null) {
            instance = new MeetingService();
        }

        return instance;
    }
}
