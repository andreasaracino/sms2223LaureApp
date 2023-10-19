package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.MeetingService;
import it.uniba.dib.sms22231.service.TaskService;

public class AddMeeting extends AppCompatActivity {
    private final TaskService taskService = TaskService.getInstance();
    private final MeetingService meetingService = MeetingService.getInstance();
    private ArrayList<Task> taskArrayList;
    private ArrayList<Task> checkedTasksArrayList = new ArrayList<>();
    private String[] tasksArray;
    private String[] taskIdsArray;
    private String applicationId;
    private boolean[] checked;
    private EditText title;
    private EditText subjects;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button addTask;
    private Button saveButton;
    private ListView taskListView;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.addMeeting);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        applicationId = intent.getStringExtra("applicationId");

        title = findViewById(R.id.meetingTitleText);
        subjects = findViewById(R.id.subjectText);
        datePicker = findViewById(R.id.meetingDate);
        timePicker = findViewById(R.id.meetingTime);
        addTask = findViewById(R.id.addTaskBtn);
        taskListView = findViewById(R.id.taskList);
        saveButton = findViewById(R.id.saveMeetingButton);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        datePicker.init(year, month, day, null);

        timePicker.setIs24HourView(true);

        addTasks();
        saveMeeting();

    }

    private void saveMeeting() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meeting meeting = new Meeting();
                meeting.title = title.getText().toString();
                meeting.subject = subjects.getText().toString();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute(), 0);
                long milliseconds = calendar.getTimeInMillis();
                meeting.date = new Date(milliseconds);
                meeting.taskId = new ArrayList<>();
                for (Task t : checkedTasksArrayList){
                    meeting.taskId.add(t.id);
                }
                meeting.applicationId = applicationId;
                meetingService.saveNewMeeting(meeting, isSuccessfully -> {
                    Toast.makeText(AddMeeting.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                });
                finish();
            }
        });
    }

    private void addTasks() {
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMeeting.this);
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < taskArrayList.size(); i++) {
                                if (checked[i]) {
                                    checkedTasksArrayList.add(taskArrayList.get(i));
                                }
                            }
                            fillTaskList();
                            dialog.dismiss();
                        }
                    });
                });

            }
        });
    }

    private void fillTaskList() {
        ArrayList<String> tasksTitle = new ArrayList<>();
        for (Task t : checkedTasksArrayList) {
            tasksTitle.add(t.title);
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasksTitle);
        taskListView.setAdapter(listAdapter);
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