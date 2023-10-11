package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.ChatElementAdapter;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class ChatListActivity extends AppCompatActivity implements RecyclerViewInterface {
    private final ChatService chatService = ChatService.getInstance();
    private RecyclerView chatListRecycler;
    private List<Chat> chatList;

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
            ChatElementAdapter chatElementAdapter = new ChatElementAdapter(chats, getApplicationContext(), this);
            chatListRecycler.setLayoutManager(layoutManager);
            chatListRecycler.setAdapter(chatElementAdapter);
            chatList = chats;
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat", chatList.get(position));
        startActivity(intent);
    }
}