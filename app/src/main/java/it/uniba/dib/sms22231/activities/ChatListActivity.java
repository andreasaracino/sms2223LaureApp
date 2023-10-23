package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.ChatElementAdapter;
import it.uniba.dib.sms22231.model.Chat;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.utility.Observable;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class ChatListActivity extends AppCompatActivity implements RecyclerViewInterface {
    private final ChatService chatService = ChatService.getInstance();
    private RecyclerView chatListRecycler;
    private List<Chat> chatList;
    private Observable.Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.chatList);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initUi();
    }

    // Inizializza la recycler view
    private void initUi() {
        chatListRecycler = findViewById(R.id.chatListRecycler);

        subscribeToChatList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Sottoscrizione alla query che restitusce la lista delle chat associate all'utente e ne riceve i cambiamenti in tempo reale
    private void subscribeToChatList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ChatElementAdapter chatElementAdapter = new ChatElementAdapter(new ArrayList<>(), getApplicationContext(), this);
        chatListRecycler.setLayoutManager(layoutManager);
        chatListRecycler.setAdapter(chatElementAdapter);

        subscription = chatService.getUserChats().subscribe(chats -> {
            chatList = chats;
            chatElementAdapter.setChatList(chatList);
        });
    }

    // Al click su un elemento della recycler apro la ChatActivity passando l'Entity Chat come argomento
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chat", chatList.get(position));
        startActivity(intent);
    }

    // Chiudo la sottoscrizione alla lista delle chat
    @Override
    protected void onDestroy() {
        super.onDestroy();

        subscription.unsubscribe();
    }
}