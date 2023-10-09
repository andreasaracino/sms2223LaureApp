package it.uniba.dib.sms22231.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.activities.DetailActivity;
import it.uniba.dib.sms22231.adapters.RecyclerAdapter;
import it.uniba.dib.sms22231.model.CardData;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.utility.RecyclerViewInterface;

public class AvailableFragment extends Fragment implements RecyclerViewInterface {
    private final ThesisService thesisService = ThesisService.getInstance();
    private ArrayList<CardData> cardData;
    private View view;
    private boolean paused;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout searchThesisLayout;
    private LinearLayout searchAverageLayout;
    private RecyclerAdapter recad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_available, container, false);

        getTheses();

        bottomNavBar();

        return view;
    }

    private void bottomNavBar() {
        bottomNavigationView = view.findViewById(R.id.availableBottom);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            int caller;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.searchThesis){
                    caller = 1;
                    searchThesis(caller);
                } else if (item.getItemId() == R.id.searchTeacher){
                    caller = 2;
                    searchThesis(caller);
                } else if (item.getItemId() == R.id.filter){
                    filterThesis();
                }
                return true;
            }
        });
    }

    private void filterThesis() {
        searchAverageLayout = view.findViewById(R.id.searchAverageLayout);
        searchAverageLayout.setVisibility(View.VISIBLE);
        EditText editFrom = view.findViewById(R.id.editFrom);
        EditText editTo = view.findViewById(R.id.editTo);
        Button filter = view.findViewById(R.id.filterButton);
        Button close2 = view.findViewById(R.id.closebutton2);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer min = Integer.parseInt(editFrom.getText().toString());
                Integer max = Integer.parseInt(editTo.getText().toString());
                ArrayList<CardData> filteredList = new ArrayList<>();
                for (CardData c : cardData){
                    Integer reqAverage = (Integer) c.getData();
                    if (reqAverage >= min && reqAverage <= max) {
                        filteredList.add(c);
                    }
                    }
                recad.filterList(filteredList);
                }

        });

        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAverageLayout.setVisibility(View.GONE);
                editFrom.setText("");
                editTo.setText("");
            }
        });
    }



    private void searchThesis(int caller) {
        searchThesisLayout = view.findViewById(R.id.searchThesisLayout);
        searchThesisLayout.setVisibility(View.VISIBLE);
        SearchView searchView = view.findViewById(R.id.searchViewThesis);
        searchView.setQuery("",false);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        if (caller == 1) {
            searchView.setQueryHint(getString(R.string.insTitle));
        } else if (caller == 2) {
            searchView.setQueryHint(getString(R.string.insTeacher));;
        }

        Button close = view.findViewById(R.id.closebutton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("",false);
                searchThesisLayout.setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (caller == 1) {
                    filterTitle(s);
                } else if (caller == 2) {
                    filterTeacher(s);
                }

                return false;
            }
        });
    }

    private void filterTeacher(String s) {
        ArrayList<CardData> filteredList = new ArrayList<>();
        for (CardData c : cardData){
            if (c.getSubtitle().toLowerCase().contains(s.toLowerCase())){
                filteredList.add(c);
            }
        }
        recad.filterList(filteredList);
    }

    private void filterTitle(String s) {
        ArrayList<CardData> filteredList = new ArrayList<>();
        for (CardData c : cardData){
            if (c.getTitle().toLowerCase().contains(s.toLowerCase())){
                filteredList.add(c);
            }
        }
        recad.filterList(filteredList);
    }

    //riempimento della Recycler con tutte le tesi disponibili
    private void getTheses() {
        thesisService.getAllTheses().subscribe(theses -> {
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                CardData thesis = new CardData(t.title, t.teacherFullname, t.id, null, t.averageRequirement);
                cardData.add(thesis);
            }
            RecyclerView rec = view.findViewById(R.id.availableRecycler);
            recad = new RecyclerAdapter(cardData, getContext(), this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rec.setLayoutManager(linearLayoutManager);
            rec.setAdapter(recad);
        });
    }

    //click sulla card per visualizzare il dettaglio tesi
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        String id = cardData.get(position).getId();
        intent.putExtra("id",id);
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
            getTheses();
        }
    }
}