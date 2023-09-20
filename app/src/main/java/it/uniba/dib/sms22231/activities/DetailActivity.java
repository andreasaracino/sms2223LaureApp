package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ThesisService;

public class DetailActivity extends AppCompatActivity {

    ThesisService thesisService = ThesisService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.detail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView txtTitle = findViewById(R.id.titleText);
        TextView txtDescription = findViewById(R.id.descriptionText);

        thesisService.getThesisById(id, thesis -> {
            String s = getString(R.string.title) + ": " + thesis.title;
            txtTitle.setText(s);

        });

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
}