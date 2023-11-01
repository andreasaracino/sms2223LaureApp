package it.uniba.dib.sms22231.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class MeetingService {
    private final List<Meeting> database;
    private static MeetingService instance;

    // Ottengo un meeting attraverso l'id
    public Observable<Meeting> getMeetingById(String meetingId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            Optional<Meeting> meetingOptional = database.stream().filter(meeting -> meeting.id.equals(meetingId)).findAny();
            next.apply(meetingOptional.orElse(null));
        });
    }

    // Ottengo la lista dei meeting di una specifica Application
    public Observable<List<Meeting>> getMeetingsByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Meeting> meetings = database.stream().filter(meeting -> meeting.applicationId.equals(applicationId)).collect(Collectors.toList());
            next.apply(meetings);
        });
    }

    // Salvataggio di una nuova Entity
    public void saveNewMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Aggiornamento di un'Entity esistente
    public void updateMeeting(Meeting meeting, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    // Elimiazione di un'Entity tramite id
    public void deleteMeeting(String meetingId, CallbackFunction<Boolean> callback) {
        callback.apply(true);
    }

    private MeetingService() {
        database = Arrays.asList(
                new Meeting(
                        "XkIJO69b5hcZo3XmK8r8",
                        "X7vND9g2r5o3GSHW5iDx",
                        Arrays.asList("7rX41QstWVixX6iwPuBW"),
                        new Date(1699198832000L),
                        "Meeting 1 - Saracino",
                        "Introduzione della tesi"
                )
        );
    }

    public static MeetingService getInstance() {
        if (instance == null) {
            instance = new MeetingService();
        }

        return instance;
    }
}
