package it.uniba.dib.sms22231.service;

import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.Thesis;
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
            getUnreadMessages(chat.id).subscribe(count -> {
                chat.unreadMessages = count;
                chatList.add(chat);

                if (chatList.size() == querySnapshot.size()) {
                    callback.apply(chatList);
                }
            });
        }
    }

    private Observable<Integer> getUnreadMessages(String chatId) {
        return new Observable<>(next -> {
            messagesCollection.whereEqualTo("chatId", chatId).whereEqualTo("read", false).count().get(AggregateSource.SERVER).addOnCompleteListener(task -> next.apply((int) task.getResult().getCount()));
        });
    }

    private Observable<Message> getLastChatMessage(String chatId) {
        return new Observable<>(next -> {
            messagesCollection.whereEqualTo("chatId", chatId).orderBy("dateSent", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    Message message = new Message(task.getResult().getDocuments().get(0).getData());
                    message.sent = Objects.equals(message.senderUID, userService.getUserData().uid);
                    next.apply(message);
                } else {
                    next.apply(null);
                }
            });
        });
    }

    public Observable<List<Message>> getChatMessages(String chatId) {
        return new Observable<>(next -> messagesCollection.whereEqualTo("chatId", chatId).orderBy("dateSent").get().addOnCompleteListener(task -> {
            List<Message> messageList = mapMessagesResult(task.getResult());
            next.apply(messageList);

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

                next.apply(messageList);
            });
        }));
    }

    public Observable<List<Chat>> getUserChats() {
        return new Observable<>(next -> {
            chatsCollection.whereEqualTo(getFieldKeyByUserType(), currentUser.uid).get().addOnCompleteListener(task -> {
                List<Chat> chats = new ArrayList<>();
                List<DocumentSnapshot> chatsRaw = task.getResult().getDocuments();

                chatsRaw.forEach(rawChat -> {
                    mapChat(rawChat, chat -> {
                        chats.add(chat);

                        if (chats.size() == chatsRaw.size()) {
                            next.apply(chats.stream().filter(c -> c.lastMessage != null).sorted(Comparator.comparing(a -> a.lastMessage.dateSent, Comparator.reverseOrder())).collect(Collectors.toList()));
                        }
                    });
                });
            });
        });
    }

    public Observable<Chat> getChatByTeacherId(String teacherId) {
        return new Observable<>(next -> {
            chatsCollection.whereEqualTo("studentId", currentUser.uid).whereEqualTo("teacherId", teacherId).get().addOnCompleteListener(task -> {
                if (task.getResult().isEmpty()) {
                    Chat chat = new Chat(null, currentUser.uid, teacherId);
                    chatsCollection.add(chat).addOnCompleteListener(task1 -> {
                        task1.getResult().get().addOnCompleteListener(task2 -> mapChat(task2.getResult(), next));
                    });
                } else {
                    mapChat(task.getResult().getDocuments().get(0), next);
                }
            });
        });
    }

    private void mapChat(DocumentSnapshot rawChat, CallbackFunction<Chat> callback) {
        Chat chat = new Chat(Objects.requireNonNull(rawChat.getData()));
        chat.id = rawChat.getId();

        final int[] completed = {0};

        userService.getUserByUid((String) rawChat.getData().get(currentUser.userType == UserTypes.STUDENT ? "teacherId" : "studentId"), user -> {
            chat.userFullName = user.fullName;

            if (++completed[0] == 3) {
                callback.apply(chat);
            }
        });

        getLastChatMessage(chat.id).subscribe(message -> {
            chat.lastMessage = message;

            if (++completed[0] == 3) {
                callback.apply(chat);
            }
        });

        getUnreadMessages(chat.id).subscribe(unreadMessages -> {
            chat.unreadMessages = unreadMessages;

            if (++completed[0] == 3) {
                callback.apply(chat);
            }
        });
    }

    private String getFieldKeyByUserType() {
        String fieldKey = "";

        switch (currentUser.userType) {
            case STUDENT:
                fieldKey = "studentId";
                break;
            case TEACHER:
                fieldKey = "teacherId";
        }

        return fieldKey;
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

    private void createChat() {
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
