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

public class MyThesesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_theses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_theses);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //TODO fake data - recycler test
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> teacher = new ArrayList<String>();
        title.add("TITOLO: Interazione utente");
        title.add("TITOLO: Programmazione");
        teacher.add("PROFESSORE: Marcello Piteo");
        teacher.add("PROFESSORE: Andrea Saracino");
        RecyclerView rec = findViewById(R.id.thesisRecycler);
        RecyclerAdapter  recad = new RecyclerAdapter(title, teacher, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        rec.setLayoutManager(linearLayoutManager);
        rec.setAdapter(recad);


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
}