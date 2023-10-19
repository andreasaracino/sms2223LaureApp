package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.service.MeetingService;
import it.uniba.dib.sms22231.service.TaskService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class MeetingDetailActivity extends AppCompatActivity implements RecyclerViewInterface {
    private final MeetingService meetingService = MeetingService.getInstance();
    private final TaskService taskService = TaskService.getInstance();
    private ArrayList<CardData> cardDataArrayList;
    private String meetingId;
    private String applicationId;
    private int caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.meetingDetail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        meetingId = intent.getStringExtra("meetingId");
        applicationId = intent.getStringExtra("applicationId");
        caller = intent.getIntExtra("caller", 0);

        fillActivity();
        initBottom();

    }

    private void initBottom() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.meetingDetailBottom);

        if (caller == 4) {
            bottomNavigationView.getMenu().findItem(R.id.addDate).setVisible(false);
        } else {
            bottomNavigationView.getMenu().findItem(R.id.modifyMeeting).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.deleteMeeting).setVisible(false);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.addDate) {
                    addToCalendar();
                } else if (item.getItemId() == R.id.modifyMeeting) {
                    modifyMeeting();
                } else if (item.getItemId() == R.id.deleteMeeting) {
                    deleteMeeting();
                }
                return false;
            }
        });
    }

    private void deleteMeeting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton("Ok", null)
                .setMessage(getString(R.string.suredelete));
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetingService.deleteMeeting(meetingId, isSuccessfully -> {
                    Toast.makeText(MeetingDetailActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                });
            }
        });

    }

    private void modifyMeeting() {
        Intent intent = new Intent(this, AddMeeting.class);
        intent.putExtra("onModify", true);
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("applicationId", applicationId);
        startActivity(intent);
        finish();
    }

    private void addToCalendar() {
        meetingService.getMeetingById(meetingId).subscribe(meeting -> {
            Calendar dateToAdd = Calendar.getInstance();
            dateToAdd.setTimeInMillis(meeting.date.getTime());
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateToAdd.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, meeting.title);
            startActivity(intent);
        });
    }

    private void fillActivity() {
        TextView title = findViewById(R.id.meetingTitle);
        TextView date = findViewById(R.id.meetingDateText);
        RecyclerView recyclerView = findViewById(R.id.taskRecyclerView);
        TextView noTasks = findViewById(R.id.noTasksTextView);
        TextView subjects = findViewById(R.id.textSubject);

        meetingService.getMeetingById(meetingId).subscribe(meeting -> {
            title.setText(meeting.title);
            String meetingDate = TimeUtils.getTimeFromDate(meeting.date, false);
            date.setText(meetingDate);
            subjects.setText(meeting.subject);
            cardDataArrayList = new ArrayList<>();
            for (String id : meeting.taskId) {
                taskService.getTaskById(id).subscribe(task -> {
                    String duedate = TimeUtils.getTimeFromDate(task.dueDate, false);
                    CardData cardData = new CardData<>(task.title, duedate, task.id, null);
                    cardDataArrayList.add(cardData);
                    if (cardDataArrayList.isEmpty()) {
                        noTasks.setVisibility(View.VISIBLE);
                    }
                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cardDataArrayList, this, this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(recyclerAdapter);
                });
            }

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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        String taskId = cardDataArrayList.get(position).getId();
        intent.putExtra("taskId", taskId);
        intent.putExtra("caller", caller);
        startActivity(intent);
    }
}