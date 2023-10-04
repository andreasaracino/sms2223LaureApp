package it.uniba.dib.sms22231.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Method;
import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class MyThesesActivity extends AppCompatActivity implements RecyclerViewInterface {

    private final ThesisService thesisService = ThesisService.getInstance();
    private final UserService userService = UserService.getInstance();
    private User user;
    private ArrayList<CardData> cardData;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_theses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_theses);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fillCard();

    }

    @Override
    protected void onResume() {
        super.onResume();

        thesisService.getUserOwnTheses();
    }

    //riempimento dell RecyclerView con i dati delle tesi del professore
    private void fillCard() {

        user = userService.getUserData();
        thesisService.userOwnTheses.subscribe(theses -> {
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                CardData thesis = new CardData(t.title, user.fullName, t.id, null);
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

    //click sull'item del menu per aggiungere una nuova tesi
    public void goToAddThesis(){
        Intent intent = new Intent(this, AddModifyThesisActivity.class);
        startActivity(intent);
    }

    //click sulla card per visualizzare il dettaglio della tesi
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        String id = cardData.get(position).getId();
        intent.putExtra("id",id);
        intent.putExtra("caller",2);
        startActivity(intent);
    }

    //creazione del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_theses_menu, menu);

        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "onMenuOpened", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    //click sugli items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            goToAddThesis();
        }
        if (id == android.R.id.home){
            this.finish();
        }
        return true;
    }

}