package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.TaskStatus;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.TaskService;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class TaskDetailActivity extends AppCompatActivity {
    private final TaskService taskService = TaskService.getInstance();
    private TextView titleText;
    private TextView descriptionText;
    private TextView statusText;
    private TextView dueDateText;
    private BottomNavigationView bottomNavigationView;
    private Task task;
    private String taskId;
    private int caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.taskDetail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        caller = intent.getIntExtra("caller", 0);

        titleText = findViewById(R.id.taskTitleTextView);
        descriptionText = findViewById(R.id.taskDescriptionTextView);
        statusText = findViewById(R.id.taskStatusTextView);
        dueDateText = findViewById(R.id.taskDueDateTextView);

        setBottomNavigationView();


        fillActivity();

    }

    private void setBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomTask);

        if (caller != 4) {
            bottomNavigationView.getMenu().findItem(R.id.deleteTask).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.modifyTask).setVisible(false);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.modifyStatus) {
                    modifyStatus();
                } else if (item.getItemId() == R.id.modifyTask) {
                    modifyTask();
                } else if (item.getItemId() == R.id.deleteTask) {
                    deleteTask();
                }
                return false;
            }
        });
    }

    private void deleteTask() {
        //TODO
    }

    private void modifyTask() {
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_add_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.modTask)
                .setView(customLayout)
                .setNegativeButton(getString(R.string.Cancel), null)
                .setPositiveButton(getString(R.string.modTask), null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView title = alertDialog.findViewById(R.id.taskTitleText);
        title.setText(titleText.getText().toString());
        TextView description = alertDialog.findViewById(R.id.taskDescriptionText);
        description.setText(descriptionText.getText().toString());
        Spinner statusSpinner = alertDialog.findViewById(R.id.taskStatusSpinner);
        statusSpinner.setSelection(task.status.ordinal());
        DatePicker datePicker = alertDialog.findViewById(R.id.taskDatePicker);
        int day = Integer.parseInt((String) DateFormat.format("dd", task.dueDate));
        int month = Integer.parseInt((String) DateFormat.format("MM", task.dueDate));
        int year = Integer.parseInt((String) DateFormat.format("yyyy", task.dueDate));
        datePicker.init(year, month - 1, day, null);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.title = title.getText().toString();
                task.description = description.getText().toString();
//                if (statusSpinner.getSelectedItem().equals(getString(R.string.openStatus))) {
//                    task.status = TaskStatus.open;
//                } else if (statusSpinner.getSelectedItem().equals(getString(R.string.closedStatus))) {
//                    task.status = TaskStatus.closed;
//                } else if (statusSpinner.getSelectedItem().equals(getString(R.string.inProgressStatus))) {
//                    task.status = TaskStatus.inProgress;
//                }
                task.status = TaskStatus.values()[statusSpinner.getSelectedItemPosition()];
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.YEAR, datePicker.getYear());
                long time = calendar.getTimeInMillis();
                task.dueDate = new Date(time);

                taskService.updateTask(task, isSuccessfully -> {
                    Toast.makeText(TaskDetailActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                });
                alertDialog.dismiss();
                fillActivity();
            }
        });
    }

    private void modifyStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.modStatus))
                .setPositiveButton("Ok", null)
                .setNegativeButton(getString(R.string.cancel), null);

        Spinner statusSpinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.taskStatus, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        builder.setView(statusSpinner);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (statusSpinner.getSelectedItem().equals(getString(R.string.openStatus))) {
//                    task.status = TaskStatus.open;
//                } else if (statusSpinner.getSelectedItem().equals(getString(R.string.closedStatus))) {
//                    task.status = TaskStatus.closed;
//                } else if (statusSpinner.getSelectedItem().equals(getString(R.string.inProgressStatus))) {
//                    task.status = TaskStatus.inProgress;
//                }
                task.status = TaskStatus.values()[statusSpinner.getSelectedItemPosition()];
                taskService.updateTask(task, isSuccessfully -> {
                    Toast.makeText(TaskDetailActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                });
               dialog.dismiss();
               fillActivity();
            }
        });
    }

    private void fillActivity() {
        taskService.getTaskById(taskId).subscribe(task -> {
            this.task = task;
            titleText.setText(task.title);
            descriptionText.setText(task.description);
            statusText.setText(getResources().getStringArray(R.array.taskStatus)[task.status.ordinal()]);
            String date = TimeUtils.getTimeFromDate(task.dueDate, false);
            dueDateText.setText(date);
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

