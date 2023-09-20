package it.uniba.dib.sms22231.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.AllThesesActivity;
import it.uniba.dib.sms22231.activities.DetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class AvailableFragment extends Fragment implements RecyclerViewInterface {
    ThesisService thesisService = ThesisService.getInstance();
    ArrayList<CardData> cardData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_available, container, false);

        thesisService.getAllTheses().subscribe(theses -> {
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                CardData thesis = new CardData(t.title, t.teacherFullname, t.id);
                cardData.add(thesis);
            }
            RecyclerView rec = view.findViewById(R.id.availableRecycler);
            RecyclerAdapter recad = new RecyclerAdapter(cardData, getContext(), this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rec.setLayoutManager(linearLayoutManager);
            rec.setAdapter(recad);
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        String id = cardData.get(position).getId();
        intent.putExtra("id",id);
        startActivity(intent);
    }
}