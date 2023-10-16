package it.uniba.dib.sms22231.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.MyThesisActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class InProgressFragment extends Fragment implements RecyclerViewInterface {
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private View view;
    private ArrayList<CardData<Application>> cardDataArrayList;
    private TextView noItemText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_in_progress, container, false);
        noItemText = view.findViewById(R.id.noThesisFoundText);

        getApplications();

        return view;
    }

    //riempimento della recycler con tutte le richieste di tesi accettate
    private void getApplications() {
        applicationService.getAllApplicationsByStatus(ApplicationStatus.approved).subscribe(applications -> {
            cardDataArrayList = new ArrayList<>();
            applications.forEach(application -> {
                CardData<Application> cardData = new CardData<>(application.studentName, application.thesisTitle, application.id, null, application);
                cardDataArrayList.add(cardData);
            });
            if (cardDataArrayList.isEmpty()){
                noItemText.setVisibility(View.VISIBLE);
            } else {
                noItemText.setVisibility(View.GONE);
                RecyclerView rec = view.findViewById(R.id.acceptedApplicationsRecycler);
                RecyclerAdapter<Application> recad = new RecyclerAdapter<>(cardDataArrayList, getContext(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rec.setLayoutManager(linearLayoutManager);
                rec.setAdapter(recad);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MyThesisActivity.class);
        String id = cardDataArrayList.get(position).getData().id;
        intent.putExtra("id",id);
        int caller = 4;
        intent.putExtra("caller", caller);
        startActivity(intent);
    }
}