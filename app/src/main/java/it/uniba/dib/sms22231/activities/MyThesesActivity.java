package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.CallbackFunction;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class MyThesesActivity extends AppCompatActivity implements RecyclerViewInterface {

    private final ThesisService thesisService = ThesisService.getInstance();
    private final UserService userService = UserService.getInstance();
    private User user;
    private ArrayList<CardData> cardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_theses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_theses);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fillCard();

    }

    private void fillCard() {

        user = userService.getUserData();
        String subtitle = user.fullName;
        thesisService.userOwnTheses.subscribe(theses -> {
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                String title = t.title;
                CardData thesis = new CardData(title, subtitle);
                cardData.add(thesis);
            }
            RecyclerView rec = findViewById(R.id.thesisRecycler);
            RecyclerAdapter  recad = new RecyclerAdapter(cardData, this, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
            rec.setLayoutManager(linearLayoutManager);
            rec.setAdapter(recad);
        });

        thesisService.getUserOwnTheses();
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

    public void goToAddThesis(View view){
        Intent intent = new Intent(this, AddThesisActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}