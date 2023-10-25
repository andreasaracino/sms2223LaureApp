package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.MeetingService;
import it.uniba.dib.sms22231.service.TaskService;

public class AddModifyMeetingActivity extends AppCompatActivity {
    private final TaskService taskService = TaskService.getInstance();
    private final MeetingService meetingService = MeetingService.getInstance();
    private EditText title;
    private EditText subjects;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button addTask;
    private Button saveButton;
    private ListView taskListView;
    private final Calendar calendar = Calendar.getInstance();
    private ArrayList<Task> taskArrayList;
    private ArrayList<Task> checkedTasksArrayList = new ArrayList<>();
    private String[] tasksArray;
    private String[] taskIdsArray;
    private boolean[] checked;
    private String applicationId;
    private String meetingId;
    private boolean onModify;
    private Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_meeting);

        Intent intent = getIntent();
        applicationId = intent.getStringExtra("applicationId");
        onModify = intent.getBooleanExtra("onModify", false);
        meetingId = intent.getStringExtra("meetingId");

        title = findViewById(R.id.meetingTitleText);
        subjects = findViewById(R.id.subjectText);
        datePicker = findViewById(R.id.meetingDate);
        timePicker = findViewById(R.id.meetingTime);
        addTask = findViewById(R.id.addTaskBtn);
        taskListView = findViewById(R.id.taskList);
        saveButton = findViewById(R.id.saveMeetingButton);

        ActionBar actionBar = getSupportActionBar();
        if (onModify) {
            actionBar.setTitle(R.string.modMeet);
            saveButton.setText(R.string.modMeet);
            fillActivity();
        } else {
            actionBar.setTitle(R.string.addMeeting);
            saveButton.setText(R.string.addMeeting);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        timePicker.setIs24HourView(true);

        addTasks();
        saveOrModify();

    }

    //se l'activity è aperta in modifica, i campi vengono riempiti con i dati del meeting
    private void fillActivity() {
        meetingService.getMeetingById(meetingId).subscribe(meeting -> {
            this.meeting = meeting;
            title.setText(meeting.title);
            subjects.setText(meeting.subject);
            int day = Integer.parseInt((String) DateFormat.format("dd", meeting.date));
            int month = Integer.parseInt((String) DateFormat.format("MM", meeting.date));
            int year = Integer.parseInt((String) DateFormat.format("yyyy", meeting.date));
            int hour = Integer.parseInt((String) DateFormat.format("HH", meeting.date));
            int minutes = Integer.parseInt((String) DateFormat.format("mm", meeting.date));
            datePicker.init(year, month - 1, day, null);
            timePicker.setHour(hour);
            timePicker.setMinute(minutes);
        });
    }

    //il metodo specifica quale azione il Button deve svolgere. Dopo aver creato un oggetto di tipo Meeting, se l'activity è stata aperta per l'aggiunta,
    //il Button effettuerà il salvataggio, altrimenti modificherà il meeting esistente.
    private void saveOrModify() {
        saveButton.setOnClickListener(view -> {
            Meeting meeting = new Meeting();
            meeting.title = title.getText().toString();
            meeting.subject = subjects.getText().toString();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
            long milliseconds = calendar.getTimeInMillis();
            meeting.date = new Date(milliseconds);
            meeting.taskIds = new ArrayList<>();
            for (Task t : checkedTasksArrayList) {
                meeting.taskIds.add(t.id);
            }
            meeting.applicationId = applicationId;
            if (onModify) {
                meeting.id = meetingId;
                meetingService.updateMeeting(meeting, isSuccessfully -> {
                    Toast.makeText(AddModifyMeetingActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                });
            } else {
                meetingService.saveNewMeeting(meeting, isSuccessfully -> {
                    Toast.makeText(AddModifyMeetingActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                });
            }
            finish();
        });
    }

    // cliccando sul pulsante appare una Dialog che permette di scegliere i task correlati al meeting
    private void addTasks() {
        addTask.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddModifyMeetingActivity.this);
            builder.setTitle(getString(R.string.addTask))
                    .setNegativeButton(getString(R.string.Cancel), null)
                    .setPositiveButton("Ok", null);
            taskArrayList = new ArrayList<>();
            taskService.getTasksByApplicationId(applicationId).subscribe(tasks -> {
                taskArrayList.addAll(tasks);
                tasksArray = new String[taskArrayList.size()];
                taskIdsArray = new String[taskArrayList.size()];
                for (int i = 0; i < taskArrayList.size(); i++) {
                    tasksArray[i] = taskArrayList.get(i).title;
                    taskIdsArray[i] = taskArrayList.get(i).id;
                }
                checked = new boolean[taskArrayList.size()];
                Arrays.fill(checked, false);
                builder.setMultiChoiceItems(tasksArray, checked, (dialogInterface, i, b) -> checked[i] = b);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                    for (int i = 0; i < taskArrayList.size(); i++) {
                        if (checked[i]) {
                            checkedTasksArrayList.add(taskArrayList.get(i));
                        }
                    }
                    fillTaskList();
                    dialog.dismiss();
                });
            });
        });
    }

    //riempimento della ListView dei task
    private void fillTaskList() {
        ArrayList<String> tasksTitle = new ArrayList<>();
        for (Task t : checkedTasksArrayList) {
            tasksTitle.add(t.title);
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasksTitle);
        taskListView.setAdapter(listAdapter);
    }

    //se ci sono delle modifiche non salvate e si chiude l'activity, viene chiesta conferma della chiusura
    @Override
    public void onBackPressed() {
        if (!isChanged()) {
            super.onBackPressed();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.sureGoBack)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton("No", null)
                .show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            super.onBackPressed();
        });
    }

    //controlla se ci sono modifiche da comunicare a onBackPressed()
    private boolean isChanged() {
        return onModify && (
                !meeting.title.equals(title.getText().toString()) ||
                        !meeting.subject.equals(subjects.getText().toString())
        ) ||
                !onModify && (
                        title.getText().toString().length() > 0 ||
                                subjects.getText().toString().length() > 0
                );
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