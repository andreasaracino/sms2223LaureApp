package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.MessagesAdapter;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.Observable;

public class ChatActivity extends AppCompatActivity {
    private final ChatService chatService = ChatService.getInstance();
    private final UserService userService = UserService.getInstance();
    private RecyclerView messagesView;
    private EditText editText;
    private Chat chat;
    private Observable<List<Message>>.Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat = (Chat) getIntent().getSerializableExtra("chat");
    }

    @Override
    protected void onStart() {
        super.onStart();

        messagesView = findViewById(R.id.messagesView);
        editText = findViewById(R.id.editTextTextMultiLine);

        List<Message> messageList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        MessagesAdapter messagesAdapter = new MessagesAdapter(messageList, this);
        messagesView.setLayoutManager(layoutManager);
        messagesView.setAdapter(messagesAdapter);

        userService.userObservable.subscribe(user -> {
            subscription = chatService.getChatMessages(chat.id).subscribe(messages -> {
                messagesAdapter.setMessages(messages);
                messagesAdapter.notifyDataSetChanged();
            });
        });
    }

    public void sendMessage(View view) {
        String text = editText.getText().toString().trim();

        if (text.length() > 0) {
            Message message = new Message();

            message.text = text;
            message.chatId = chat.id;

            chatService.sendMessage(message);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        subscription.unsubscribe();
    }
}