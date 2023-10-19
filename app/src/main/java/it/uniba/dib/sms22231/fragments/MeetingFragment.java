package it.uniba.dib.sms22231.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.AddMeeting;
import it.uniba.dib.sms22231.activities.MeetingDetailActivity;
import it.uniba.dib.sms22231.activities.TaskDetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.MeetingService;
import it.uniba.dib.sms22231.service.TaskService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class MeetingFragment extends Fragment implements RecyclerViewInterface {
    private final MeetingService meetingService = MeetingService.getInstance();
    private ArrayList<CardData> cardDataArrayList;
    private View view;
    private TextView noMeeting;
    private FloatingActionButton addMeetingButton;
    private RecyclerView meetingRecycler;
    private String applicationId;
    private int caller;

    public MeetingFragment(String applicationId, int caller) {
        super();
        this.applicationId = applicationId;
        this.caller = caller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_meeting, container, false);

        noMeeting = view.findViewById(R.id.noMeetingText);
        meetingRecycler = view.findViewById(R.id.meetingRecycler);

        addMeetingButton = view.findViewById(R.id.addMeetingButton);
        if (caller == 4) {
            addMeetingButton.setVisibility(View.VISIBLE);
        } else {
            addMeetingButton.setVisibility(View.GONE);
        }

        addMeeting();
        fillFragment();

        return view;
    }

    private void fillFragment() {
        meetingService.getMeetingsByApplicationId(applicationId).subscribe(meetings -> {
            cardDataArrayList = new ArrayList<>();
            for (Meeting m : meetings){
                String date = TimeUtils.getTimeFromDate(m.date, false);
                CardData cardData = new CardData<>(m.title, date, m.id, null);
                cardDataArrayList.add(cardData);
            }
            if (cardDataArrayList.isEmpty()){
                noMeeting.setVisibility(View.VISIBLE);
            } else {
                noMeeting.setVisibility(View.GONE);
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cardDataArrayList, getContext(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                meetingRecycler.setLayoutManager(linearLayoutManager);
                meetingRecycler.setAdapter(recyclerAdapter);
                SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.meetingRefresh);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(false);
                        fillFragment();
                    }
                });
            }
        });
    }

    private void addMeeting() {
        addMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddMeeting.class);
                intent.putExtra("applicationId", applicationId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MeetingDetailActivity.class);
        String meetingId = cardDataArrayList.get(position).getId();
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("applicationId", applicationId);
        intent.putExtra("caller", caller);
        startActivity(intent);
    }
}