package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;

public class DetailActivity extends AppCompatActivity {
    private final ThesisService thesisService = ThesisService.getInstance();
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtOwner;
    private final RequirementService requirementService = RequirementService.getInstance();
    private ListView reqListview;
    private ArrayList<String> req;
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private ListView fileListView;
    private ArrayList<String> attach;
    BottomAppBar bottomAppBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.detail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        int caller = intent.getIntExtra("caller", 0);

        bottomAppBar = findViewById(R.id.bottomAppBar);
        switch (caller){
            case 1:
                bottomAppBar.replaceMenu(R.menu.detail_available_bottom_menu);
                break;
            case 2:
                bottomAppBar.replaceMenu(R.menu.detail_my_theses_bottom_menu);
        }


        txtTitle = findViewById(R.id.titleText);
        txtDescription = findViewById(R.id.descriptionText);
        txtOwner = findViewById(R.id.ownerText);
        reqListview = findViewById(R.id.reqList);
        fileListView = findViewById(R.id.fileList);

        thesisService.getThesisById(id, thesis -> {
            txtTitle.setText(thesis.title);
            String temp = getString(R.string.teacher) + ": " + thesis.teacherFullname;
            txtOwner.setText(temp);
            txtDescription.setText(thesis.description);

            requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                req = new ArrayList<>();
                for (Requirement requirement : requirements) {
                    String reqtemp = requirement.description + ": " + requirement.value;
                    req.add(reqtemp);
                }
                fillList(req, reqListview);
            });
            attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                attach = new ArrayList<>();
                for (Attachment attachment : attachments) {
                    String attachtemp = attachment.fileName;
                    attach.add(attachtemp);
                }
                fillList(attach, fileListView);
            });
        });
    }

    private void fillList(ArrayList<String> arrayList, ListView listView) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listAdapter);
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