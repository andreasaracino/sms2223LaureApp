package it.uniba.dib.sms22231.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.AddModifyMeetingActivity;
import it.uniba.dib.sms22231.activities.MeetingDetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Meeting;
import it.uniba.dib.sms22231.service.MeetingService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;
import it.uniba.dib.sms22231.utility.TimeUtils;

public class MeetingFragment extends Fragment implements RecyclerViewInterface {
    private final MeetingService meetingService = MeetingService.getInstance();
    private View view;
    private TextView noMeeting;
    private FloatingActionButton addMeetingButton;
    private ArrayList<CardData> cardDataArrayList;
    private String applicationId;
    private int caller;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_meeting, container, false);

        applicationId = getArguments().getString("applicationId");
        caller = getArguments().getInt("caller", 0);

        noMeeting = view.findViewById(R.id.noMeetingText);

        addMeetingButton = view.findViewById(R.id.addMeetingButton);
        if (caller == 4) {
            addMeetingButton.setVisibility(View.VISIBLE);
        } else {
            addMeetingButton.setVisibility(View.GONE);
        }

        initUI();
        addMeeting();
        fillFragment();

        return view;
    }

    // inizializza lo swipe refresh layout
    private void initUI() {
        recyclerView = view.findViewById(R.id.meetingRecycler);
        recyclerAdapter = new RecyclerAdapter<>(new ArrayList<>(), getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        swipeRefreshLayout = view.findViewById(R.id.meetingRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::fillFragment);
    }

    //riempimento del fragment con la lista dei meeting
    private void fillFragment() {
        meetingService.getMeetingsByApplicationId(applicationId).subscribe(meetings -> {
            cardDataArrayList = new ArrayList<>();
            for (Meeting m : meetings){
                String date = TimeUtils.dateToString(m.date);
                CardData cardData = new CardData<>(m.title, date, m.id, null);
                cardDataArrayList.add(cardData);
            }
            swipeRefreshLayout.setRefreshing(false);
            if (cardDataArrayList.isEmpty()){
                noMeeting.setVisibility(View.VISIBLE);
            } else {
                noMeeting.setVisibility(View.GONE);
                recyclerAdapter.setCardData(cardDataArrayList);
            }
        });
    }

    //apertura dell'activity per aggiungere un meeting
    private void addMeeting() {
        addMeetingButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddModifyMeetingActivity.class);
            intent.putExtra("applicationId", applicationId);
            startActivity(intent);
        });
    }

    //apertura del dettaglio del meeting
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