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
import it.uniba.dib.sms22231.model.Message;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.service.UserService;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesView;
    private EditText editText;
    private final ChatService chatService = ChatService.getInstance();
    private final UserService userService = UserService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
            chatService.getChatMessages("0").subscribe(messages -> {
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
            message.chatId = "0";

            chatService.sendMessage(message);
        }
    }
}