package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;

public class ApplicationDetailActivity extends AppCompatActivity {

    private String id;
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtOwner;
    private ListView reqListview;
    private TextView txtNoRequirement;
    private final ThesisService thesisService = ThesisService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private Application application;
    private final RequirementService requirementService = RequirementService.getInstance();
    private List<Requirement> teacherRequirements;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        fillActivity();

    }

    private void fillActivity() {
        txtTitle = findViewById(R.id.titleAppText);
        txtDescription = findViewById(R.id.descriptionAppText);
        txtOwner = findViewById(R.id.ownerAppText);
        reqListview = findViewById(R.id.reqAppList);
        txtNoRequirement = findViewById(R.id.textNoReq);

        applicationService.getApplicationById(id).subscribe(application -> {
           this.application = application;
        });

        thesisService.getThesisById(application.thesisId, thesis -> {
            txtTitle.setText(thesis.title);
            String temp = getString(R.string.student) + ": " + application.studentName;
            txtOwner.setText(temp);
            txtDescription.setText(thesis.description);

            requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                teacherRequirements = requirements;
            });

        });
    }
}