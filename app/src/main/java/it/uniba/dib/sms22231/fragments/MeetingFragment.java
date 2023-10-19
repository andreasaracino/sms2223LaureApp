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
    private RecyclerView meetingRecycler;
    private ArrayList<CardData> cardDataArrayList;
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

    //riempimento del fragment con la lista dei meeting
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
                swipeRefreshLayout.setOnRefreshListener(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    fillFragment();
                });
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