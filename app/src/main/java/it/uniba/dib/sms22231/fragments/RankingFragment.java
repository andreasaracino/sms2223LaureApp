package it.uniba.dib.sms22231.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.DetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.StudentService;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class RankingFragment extends Fragment implements RecyclerViewInterface {
    private final ThesisService thesisService = ThesisService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private ArrayList<CardData> cardData;
    private RecyclerView rec;
    private View view;
    private TextView noItemText;
    private boolean paused;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ranking, container, false);
        noItemText = view.findViewById(R.id.noItemText);

        initRecyclerView();
        getTheses();

        return view;
    }

    //inizializzazione della RecyclerView
    private void initRecyclerView() {
        rec = view.findViewById(R.id.rankRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rec.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rec);
    }

    //riempimento della RecyclerView con le tesi preferite
    private void getTheses() {
        thesisService.getSavedTheses().subscribe(theses -> {
            cardData = new ArrayList<>();
            int rank = 0;
            for (Thesis t : theses) {
                rank++;
                CardData thesis = new CardData(t.title, t.teacherFullname, t.id, String.valueOf(rank) + ".");
                cardData.add(thesis);
            }
            if (cardData.isEmpty()){
                noItemText.setVisibility(View.VISIBLE);
            } else {
                noItemText.setVisibility(View.GONE);
                RecyclerAdapter recad = new RecyclerAdapter(cardData, getContext(), this);
                rec.setAdapter(recad);
            }
        });
    }

    //drag and drop delle tesi per ordinarle in base alla classifica
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(cardData, fromPosition, toPosition);
            for (int i = 0; i < cardData.size(); i++) {
                cardData.get(i).setRank(i + 1 + ".");
            }
            recyclerView.getAdapter().notifyItemChanged(fromPosition);
            recyclerView.getAdapter().notifyItemChanged(toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            studentService.saveNewFavoritesOrder(cardData.stream().map(card -> card.getId()).collect(Collectors.toList()));

            //annullamento dell'animazione del longClick (vedi RecyclerViewAdapter)
            recyclerView.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    viewHolder.itemView.animate().scaleY(1f).setDuration(700).start();
                    viewHolder.itemView.animate().scaleX(1f).setDuration(700).start();
                }
                return false;
            });

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

    };

    //click sulla card per mostrare il dettaglio
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        String id = cardData.get(position).getId();
        intent.putExtra("id", id);
        intent.putExtra("caller", 1);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (paused) {
            paused = false;
            getTheses();
        }
    }
}