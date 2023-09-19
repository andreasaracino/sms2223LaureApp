package it.uniba.dib.sms22231.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ThesisService;

public class AvailableFragment extends Fragment {
    ThesisService thesisService = ThesisService.getInstance();
    ArrayList<CardData> cardData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available, container, false);

        thesisService.getAllTheses().subscribe(theses->{
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                String title = t.title;
                String subtitle = t.teacherFullname;
                CardData thesis = new CardData(title, subtitle);
                cardData.add(thesis);
            }
            RecyclerView rec = view.findViewById(R.id.availableRecycler);
            RecyclerAdapter recad = new RecyclerAdapter(cardData, getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
            rec.setLayoutManager(linearLayoutManager);
            rec.setAdapter(recad);
        });

        return view;
    }
}