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
import it.uniba.dib.sms22231.adapters.ListAdapter;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.ListData;
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
    private List<Requirement> studentRequirements;


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
            studentRequirements = application.requirements;

            thesisService.getThesisById(application.thesisId, thesis -> {
                txtTitle.setText(thesis.title);
                String temp = getString(R.string.student) + ": " + application.studentName;
                txtOwner.setText(temp);
                txtDescription.setText(thesis.description);

                requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                    teacherRequirements = requirements;

                    ArrayList<ListData> listDataArrayList = new ArrayList<>();
                    boolean averageControl = false;
                    int teacherAverage = 0;
                    int studentAverage = 0;

                    for (Requirement r : teacherRequirements){
                        if (r.description.equals(getString(R.string.average))){
                            teacherAverage = Integer.parseInt(r.value);
                            averageControl = true;
                        }
                    }
                    for (Requirement r : studentRequirements){
                        if (r.description.equals(getString(R.string.average)) ){
                            studentAverage = Integer.parseInt(r.value);
                        }
                    }
                    if (averageControl){
                        String averageText = getString(R.string.average) + ": " + studentAverage + "/" + teacherAverage;
                        ListData average = new ListData(studentAverage < teacherAverage ? R.drawable.clear : R. drawable.check, averageText);
                        listDataArrayList.add(average);
                    }

                    boolean givenExam;

                    for (Requirement teacherReq : teacherRequirements){
                        givenExam = false;
                        if (teacherReq.description.equals(getString(R.string.exam))){
                            for (Requirement studentReq : studentRequirements){
                                if (studentReq.description.equals(getString(R.string.exam))){
                                    if (teacherReq.id.equals(studentReq.id)) {
                                        givenExam = true;
                                    }
                                }
                            }
                            String examText = teacherReq.description + ": " + teacherReq.value;
                            ListData exam = new ListData(givenExam ? R.drawable.check : R.drawable.clear, examText);
                            listDataArrayList.add(exam);
                        }
                    }

                    ListAdapter listAdapter = new ListAdapter(this, listDataArrayList);
                    reqListview.setAdapter(listAdapter);
                    reqListview.setVisibility(View.VISIBLE);
                });
            });
        });


    }
}