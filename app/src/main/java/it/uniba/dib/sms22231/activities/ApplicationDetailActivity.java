package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.CustomListAdapter;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.CustomListData;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;

public class ApplicationDetailActivity extends AppCompatActivity {

    private final ThesisService thesisService = ThesisService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private Application application;
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtOwner;
    private ListView reqListview;
    private String id;
    private List<Requirement> teacherRequirements;
    private List<Requirement> studentRequirements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.appdetail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        fillActivity();

    }

    //riempimento dei campi dell'activity
    private void fillActivity() {
        txtTitle = findViewById(R.id.titleAppText);
        txtDescription = findViewById(R.id.descriptionAppText);
        txtOwner = findViewById(R.id.ownerAppText);
        reqListview = findViewById(R.id.reqAppList);

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

                    ArrayList<CustomListData> listDataArrayCustomList = new ArrayList<>();
                    boolean averageControl = false;
                    int teacherAverage = 0;
                    int studentAverage = 0;

                    for (Requirement r : teacherRequirements){
                        if (r.description == RequirementTypes.average){
                            teacherAverage = Integer.parseInt(r.value);
                            averageControl = true;
                        }
                    }
                    for (Requirement r : studentRequirements){
                        if (r.description == RequirementTypes.average){
                            studentAverage = Integer.parseInt(r.value);
                        }
                    }
                    if (averageControl){
                        String averageText = getString(R.string.average) + ": " + studentAverage + "/" + teacherAverage;
                        CustomListData average = new CustomListData(studentAverage < teacherAverage ? R.drawable.clear : R. drawable.check, averageText);
                        listDataArrayCustomList.add(average);
                    }

                    boolean givenExam;

                    for (Requirement teacherReq : teacherRequirements){
                        givenExam = false;
                        if (teacherReq.description == RequirementTypes.exam){
                            for (Requirement studentReq : studentRequirements){
                                if (studentReq.description == RequirementTypes.exam){
                                    if (teacherReq.id.equals(studentReq.id)) {
                                        givenExam = true;
                                    }
                                }
                            }
                            String examText = getResources().getStringArray(R.array.requirements)[teacherReq.description.ordinal()] + ": " + teacherReq.value;
                            CustomListData exam = new CustomListData(givenExam ? R.drawable.check : R.drawable.clear, examText);
                            listDataArrayCustomList.add(exam);
                        }
                    }

                    CustomListAdapter customListAdapter = new CustomListAdapter(this, listDataArrayCustomList);
                    reqListview.setAdapter(customListAdapter);
                    reqListview.setVisibility(View.VISIBLE);
                });
            });
        });
    }

    //rifiuto della richiesta di tesi
    public void rejectApplication(View view){
        ApplicationStatus newStatus = ApplicationStatus.rejected;
        setStatus(newStatus);
    }

    //modifica dello stato della tesi
    private void setStatus(ApplicationStatus newStatus) {
        applicationService.setNewApplicationStatus(this, application, newStatus, success -> {
            if (success){
                Toast.makeText(this,R.string.success,Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }

    //accetazione della richiesta di tesi
    public void acceptApplication(View view){
        ApplicationStatus newStatus = ApplicationStatus.approved;
        setStatus(newStatus);
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