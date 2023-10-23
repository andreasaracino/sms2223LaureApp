package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.MessagesAdapter;
import it.uniba.dib.sms22231.config.MessageReferenceType;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.MessageReference;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.Observable;
import it.uniba.dib.sms22231.utility.ResUtils;

public class ChatActivity extends AppCompatActivity {
    private final ResUtils resUtils = ResUtils.getInstance();
    private final ChatService chatService = ChatService.getInstance();
    private final UserService userService = UserService.getInstance();
    private RecyclerView messagesView;
    private MessagesAdapter messagesAdapter;
    private LinearLayout messageReferenceContainer;
    private TextView chatReferenceMessage;
    private EditText editText;
    private Chat chat;
    private MessageReference messageReference;
    private Observable<List<Message>>.Subscription subscription;
    private boolean paused;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chat = (Chat) getIntent().getSerializableExtra("chat");
        messageReference = (MessageReference) getIntent().getSerializableExtra("messageReference");

        initAppBar();
    }

    // Inizializzo l'app bar customizzata con l'icona dell'utente interlocutore e le sue generalità
    private void initAppBar() {
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        View customView = getLayoutInflater().inflate(R.layout.activity_chat_action_bar, null);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);

        ImageButton backButton = customView.findViewById(R.id.backButton);
        TextView chatUserFullName = customView.findViewById(R.id.chatUserFullName);
        TextView chatUserInitials = customView.findViewById(R.id.chatUserInitials);

        backButton.setOnClickListener(this::onBack);
        chatUserFullName.setText(chat.userFullName);
        String[] splitName = chat.userFullName.split(" ");
        String initials = String.valueOf(splitName[0].charAt(0)) + splitName[splitName.length - 1].charAt(0);
        chatUserInitials.setText(initials);
        chatUserInitials.setBackgroundTintList(ColorStateList.valueOf(resUtils.getColorByNumber(chat.userFullName.length())));
    }

    public void onBack(View view) {
        this.onBackPressed();
    }

    // Inizializzo gli elementi grafici e l'adapter da assegnare alla recycler view contenente la lista dei messaggi in chat
    @Override
    protected void onStart() {
        super.onStart();

        messageReferenceContainer = findViewById(R.id.messageReferenceContainer);
        chatReferenceMessage = findViewById(R.id.chatReferenceMessage);
        messagesView = findViewById(R.id.messagesView);
        editText = findViewById(R.id.editTextTextMultiLine);

        initMessageReference();

        List<Message> messageList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        messagesAdapter = new MessagesAdapter(messageList, this, this::goToReference);
        messagesView.setLayoutManager(layoutManager);
        messagesView.setAdapter(messagesAdapter);

        userService.userObservable.subscribe(user -> {
            subscribeToChat();
        });
    }

    // Se è stato passato un riferimento a una tesi o a un task visualizzo l'apposito messaggio di servizio
    private void initMessageReference() {
        if (messageReference != null) {
            messageReferenceContainer.setVisibility(View.VISIBLE);
            chatReferenceMessage.setText(resUtils.getStringWithParams(messageReference.messageReferenceType.getStringRes(), messageReference.value));
        }
    }

    // Al click su un messaggio con riferimento (tesi o task) vado alla rispettiva activity
    private void goToReference(MessageReference messageReference) {
        Intent intent;
        if (Objects.requireNonNull(messageReference.messageReferenceType) == MessageReferenceType.task) {
            intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra("taskId", messageReference.referenceId);
        } else {
            intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", messageReference.referenceId);
        }
        startActivity(intent);
    }

    // Sottoscrivo l'activity all'observable che riceve i messaggi per visualizzarli nella recycler view
    private void subscribeToChat() {
        subscription = chatService.getChatMessages(chat.id).subscribe(messages -> {
            messagesAdapter.setMessages(messages);
            messagesAdapter.notifyDataSetChanged();
            messagesView.scrollToPosition(messages.size() - 1);
        });
    }

    // Creo un'entity di tipo Message e la passo al metodo apposito del ChatService che si occuperà di inviarlo
    public void sendMessage(View view) {
        String text = editText.getText().toString().trim();

        if (text.length() > 0) {
            editText.setText("");
            Message message = new Message();

            message.text = text;
            message.chatId = chat.id;
            message.messageReference = messageReference;

            removeReference(null);
            chatService.sendMessage(message);
        }
    }

    // Al click sul pulsante di chiusura del riferimento, lo stesso viene rimosso dall'interfaccia e dall'activity
    public void removeReference(View view) {
        messageReference = null;
        messageReferenceContainer
                .animate()
                .scaleX(0.5f)
                .scaleY(0.5f)
                .translationY(64)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        messageReferenceContainer.setVisibility(View.GONE);
                    }
                });
    }

    // sospendo la sottoscrizione
    @Override
    protected void onPause() {
        super.onPause();

        subscription.unsubscribe();
        paused = true;
    }

    // riavvio la sottoscrizione
    @Override
    protected void onResume() {
        super.onResume();

        if (paused) {
            subscribeToChat();
            paused = false;
        }
    }
}