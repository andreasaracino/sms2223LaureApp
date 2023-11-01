package it.uniba.dib.sms22231.service;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.Observable;

public class ChatService {
    private final List<Chat> chatDatabase;
    private final List<Message> messageDatabase;
    private static ChatService instance;

    private UserService userService;
    private User currentUser;

    private ChatService() {
        chatDatabase = Arrays.asList(new Chat("UN5nGfIAAlazySh9MVTy", "X7vND9g2r5o3GSHW5iDx", "V1hHe9O2Oge3VpVP1cvmyGIlFqR2", "rf1Y5RWeiyg6NItgdtrptH01pCy2", new Date()));
        messageDatabase = Arrays.asList(
                new Message("oScYAWVVeu5wwQeHrTGD", "UN5nGfIAAlazySh9MVTy", "{\"it\":\"La richiesta per la tesi \\\"Intent e comunicazione tra Activity\\\" è stata APPROVATA dal Prof. Marcello Piteo\",\"en\":\"The application for thesis \\\"Intent e comunicazione tra Activity\\\" has been APPROVED by Prof. Marcello Piteo\"}", null, null, new Date(1698594032000L), true, false, null),
                new Message("MJdzvDVMHKETLph9KNqk", "UN5nGfIAAlazySh9MVTy", "Buongiorno Andrea, come hai potuto vedere la richiesta di tesi è stata accettata. Ho aggiunto il primo task di cui puoi vedere i dettagli e stabilito un meeting.", "rf1Y5RWeiyg6NItgdtrptH01pCy2", null, new Date(1698594212000L), true, false, null),
                new Message("A1DzW2wDGe2NAfw88ckN", "UN5nGfIAAlazySh9MVTy", "Fammi sapere se data e orario vanno bene, altrimenti concordiamo per un altro giorno", "rf1Y5RWeiyg6NItgdtrptH01pCy2", null, new Date(1698594272000L), true, false, null),
                new Message("f1hnyrmJw5chmkBF74Wb", "UN5nGfIAAlazySh9MVTy", "Buongiorno professore. Sì, le confermo che la data e l'ora stabilite vanno bene.", "V1hHe9O2Oge3VpVP1cvmyGIlFqR2", null, new Date(1698594392000L), false, false, null)
        );

        initData();
    }

    // Vengono inizializzate le istanze dei servizi di Firebase
    private void initData() {
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            currentUser = user;
        });
    }

    // Ottengo il numero dei messaggi non letti da parte dell'utente destinatario all'interno di una determinata chat
    private Integer getUnreadMessages(String chatId) {
        return (int) messageDatabase.stream().filter(message -> Objects.equals(message.chatId, chatId) && !Objects.equals(message.senderUID, currentUser.uid) && !message.read).count();
    }

    // Ottengo l'ultimo messaggio scambiato all'interno di una chat
    private Message getLastChatMessage(String chatId) {
        Optional<Message> messageOptional = messageDatabase.stream().sorted(Comparator.comparing(m -> m.dateSent)).filter(m -> m.chatId.equals(chatId)).findFirst();

        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.sent = Objects.equals(message.senderUID, userService.getUserData().uid);
            return message;
        } else {
            return null;
        }
    }

    // Ottengo la lista dei messaggi contenuti in una chat e subito dopo effettuo l'ascolto delle modifiche alla lista. Questo permette di mandare aggiornamenti
    // alla ChatActivity attraverso l'Observable restituito
    public Observable<List<Message>> getChatMessages(String chatId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Message> messages = messageDatabase.stream().sorted(Comparator.comparing(m -> m.dateSent)).filter(m -> m.chatId.equals(chatId)).map(message -> {
                message.sent = Objects.equals(message.senderUID, userService.getUserData().uid);
                return message;
            }).collect(Collectors.toList());

            next.apply(messages);
        });
    }

    // Ottengo la lista delle chat associate all'utente e, come nel metodo sopra, ne ascolto le modifiche e aggiorno ChatListActivity
    public Observable<List<Chat>> getUserChats() {
        return new Observable<>((next, setOnUnsubscribe) -> {
            List<Chat> chats = chatDatabase.stream().filter(chat -> chat.studentId.equals(currentUser.uid) || chat.teacherId.equals(currentUser.uid)).map(this::mapChat).collect(Collectors.toList());
            next.apply(chats);
        });
    }

    @NonNull
    private Chat mapChat(Chat chat) {
        chat.lastMessage = getLastChatMessage(chat.id);
        chat.userFullName = userService.getUserById(currentUser.userType == UserTypes.TEACHER ? chat.studentId : chat.teacherId).fullName;
        chat.unreadMessages = getUnreadMessages(chat.id);
        return chat;
    }

    // Ottengo la chat associata allo studente e al professore passati in input
    public Observable<Chat> getChatByStudentIdAndTeacherId(String studentUid, String teacherId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            Optional<Chat> chatOptional = chatDatabase.stream().filter(chat -> chat.teacherId.equals(teacherId) && chat.studentId.equals(studentUid)).findFirst();
            if (chatOptional.isPresent()) {
                next.apply(mapChat(chatOptional.get()));
            } else {
                next.apply(null);
            }
        });
    }

    // Ottengo la chat associata all'Application specificata in input
    public Observable<Chat> getChatByApplicationId(String applicationId) {
        return new Observable<>((next, setOnUnsubscribe) -> {
            Optional<Chat> chatOptional = chatDatabase.stream().filter(chat -> chat.applicationId.equals(applicationId)).findFirst();
            chatOptional.ifPresent(value -> next.apply(mapChat(value)));
        });
    }

    // Invio di un messaggio ad una chat con relativo aggiornamento della stessa (per triggerare l'aggiornamento di ChatListActivity)
    public void sendMessage(Message message) {}

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }

        return instance;
    }
}
