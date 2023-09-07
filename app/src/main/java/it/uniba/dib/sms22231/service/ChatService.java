package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.Observable;

public class ChatService {
    private static final String CHATS_COLLECTION = "chats";
    private static final String MESSAGES_COLLECTION = "messages";
    private static ChatService instance;

    private FirebaseFirestore db;
    private CollectionReference chatsCollection;
    private CollectionReference messagesCollection;
    private final Observable<List<Chat>> chatsObservable = new Observable<>(null);
    private UserService userService;
    private User currentUser;

    private ChatService() {
        initData();
    }

    private void initData() {
        db = FirebaseFirestore.getInstance();
        chatsCollection = db.collection(CHATS_COLLECTION);
        messagesCollection = db.collection(MESSAGES_COLLECTION);
        userService = UserService.getInstance();
        userService.userObservable.subscribe((user) -> {
            currentUser = user;
        });
    }

    public void getCurrentUserChats() {
        chatsCollection.whereEqualTo(currentUser.userType.toString().toLowerCase() + "Id", currentUser.uid).get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                mapChatsResult(task.getResult(), chatsObservable::next);
            }
        });
    }

    private void mapChatsResult(QuerySnapshot querySnapshot, CallbackFunction<List<Chat>> callback) {
        List<Chat> chatList = new ArrayList<>();

        for (QueryDocumentSnapshot rawChat : querySnapshot) {
            Chat chat = new Chat(rawChat.getData());
            chat.id = rawChat.getId();
            getUnreadMessages(chat.id, count -> {
                chat.unreadMessages = count;
                chatList.add(chat);

                if (chatList.size() == querySnapshot.size()) {
                    callback.apply(chatList);
                }
            });
        }
    }

    private void getUnreadMessages(String chatId, CallbackFunction<Integer> callback) {
        messagesCollection.whereEqualTo("chatId", chatId).whereEqualTo("read", false).count().get(AggregateSource.SERVER).addOnCompleteListener(task -> callback.apply((int) task.getResult().getCount()));
    }

    public Observable<List<Message>> getChatMessages(String chatId) {
        Observable<List<Message>> messagesObservable = new Observable<>();

        messagesCollection.whereEqualTo("chatId", chatId).orderBy("dateSent").limit(100).get().addOnCompleteListener(task -> {
            List<Message> messageList = mapMessagesResult(task.getResult());
            messagesObservable.next(messageList);

            messagesCollection.whereEqualTo("chatId", chatId).orderBy("dateSent").addSnapshotListener((snapshot, e) -> {
                if (e != null || snapshot == null) return;

                for (DocumentChange documentChange : snapshot.getDocumentChanges()) {
                    Message message = new Message(documentChange.getDocument().getData());
                    message.id = documentChange.getDocument().getId();

                    if (documentChange.getType() == DocumentChange.Type.ADDED && !messageList.contains(message)) {
                        message.sent = Objects.equals(message.senderUID, userService.getUserData().uid);
                        messageList.add(message);
                    }
                }

                messagesObservable.next(messageList);
            });
        });

        return messagesObservable;
    }

    private List<Message> mapMessagesResult(QuerySnapshot querySnapshot) {
        List<Message> messageList = new ArrayList<>();

        for (QueryDocumentSnapshot rawMessage : querySnapshot) {
            Message message = new Message(rawMessage.getData());
            message.id = rawMessage.getId();
            message.sent = Objects.equals(message.senderUID, currentUser.uid);
            messageList.add(message);
        }

        return messageList;
    }

    public void sendMessage(Message message) {
        message.dateSent = Date.from(Instant.now());
        message.read = false;
        message.senderUID = currentUser.uid;

        messagesCollection.add(message.toMap());
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }

        return instance;
    }
}
