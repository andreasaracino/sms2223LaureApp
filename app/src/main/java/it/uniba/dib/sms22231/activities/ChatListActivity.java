package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.ChatElementAdapter;
import it.uniba.dib.sms22231.service.ChatService;

public class ChatListActivity extends AppCompatActivity {
    private final ChatService chatService = ChatService.getInstance();
    private RecyclerView chatListRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        initUi();
    }

    private void initUi() {
        chatListRecycler = findViewById(R.id.chatListRecycler);

        chatService.getUserChats().subscribe(chats -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            ChatElementAdapter chatElementAdapter = new ChatElementAdapter(chats, this);
            chatListRecycler.setLayoutManager(layoutManager);
            chatListRecycler.setAdapter(chatElementAdapter);
        });
    }
}