package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

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
        caller = intent.getIntExtra("caller", 0);

        fillActivity();

    }

    private void fillActivity() {
        TextView title = findViewById(R.id.meetingTitle);
        TextView date = findViewById(R.id.meetingDateText);
        RecyclerView recyclerView = findViewById(R.id.taskRecyclerView);
        TextView noTasks = findViewById(R.id.noTasksTextView);

        meetingService.getMeetingById(meetingId).subscribe(meeting -> {
            title.setText(meeting.title);
            String meetingDate = TimeUtils.getTimeFromDate(meeting.date, false);
            date.setText(meetingDate);
            cardDataArrayList = new ArrayList<>();
            for (String id : meeting.taskId){
                taskService.getTaskById(id).subscribe(task -> {
                    String duedate = TimeUtils.getTimeFromDate(task.dueDate, false);
                    CardData cardData = new CardData<>(task.title, duedate, task.id, null);
                    cardDataArrayList.add(cardData);
                    if (cardDataArrayList.isEmpty()){
                        noTasks.setVisibility(View.VISIBLE);
                    }
                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cardDataArrayList, this, this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
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