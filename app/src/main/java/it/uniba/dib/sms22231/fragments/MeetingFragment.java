package it.uniba.dib.sms22231.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.AddMeeting;
import it.uniba.dib.sms22231.model.Task;
import it.uniba.dib.sms22231.service.TaskService;

public class MeetingFragment extends Fragment {
     private View view;
     private FloatingActionButton addMeetingButton;
     private String applicationId;
     private int caller;

     public MeetingFragment(String applicationId, int caller){
         super();
         this.applicationId = applicationId;
         this.caller = caller;
     }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_meeting, container, false);

        addMeetingButton = view.findViewById(R.id.addMeetingButton);
        if (caller == 4) {
            addMeetingButton.setVisibility(View.VISIBLE);
        } else {
            addMeetingButton.setVisibility(View.GONE);
        }

        addMeeting();

        return view;
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
}