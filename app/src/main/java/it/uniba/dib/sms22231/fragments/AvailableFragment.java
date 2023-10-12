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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<CardData> filteredList;
    private View view;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout searchThesisLayout;
    private LinearLayout searchAverageLayout;
    private SearchView searchView;
    private TextView noItemText;
    private RecyclerAdapter recad;
    private boolean paused;
    private boolean isOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_available, container, false);

        searchThesisLayout = view.findViewById(R.id.searchThesisLayout);
        searchAverageLayout = view.findViewById(R.id.searchAverageLayout);
        searchView = view.findViewById(R.id.searchViewThesis);
        noItemText = view.findViewById(R.id.noItemText);

        getTheses();

        bottomNavBar();

        return view;
    }

    //creazione della bottomNavigationView e click sugli items del menu
    private void bottomNavBar() {
        bottomNavigationView = view.findViewById(R.id.availableBottom);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            int caller;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.searchThesis) {
                    getTheses();
                    caller = 1;
                    searchThesis(caller);
                } else if (item.getItemId() == R.id.searchTeacher) {
                    getTheses();
                    caller = 2;
                    searchThesis(caller);
                } else if (item.getItemId() == R.id.filter) {
                    filterThesis();
                }
                return true;
            }
        });
    }

    //filtraggio per media
    private void filterThesis() {
        searchThesisLayout.setVisibility(View.GONE);
        searchView.setQuery("", false);
        searchAverageLayout.setVisibility(View.VISIBLE);
        isOpen = true;
        Spinner spinnerFrom = view.findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = view.findViewById(R.id.spinnerTo);
        Button filter = view.findViewById(R.id.filterButton);
        Button close2 = view.findViewById(R.id.closebutton2);

        filter.setOnClickListener(view -> {
            Integer min = Integer.parseInt(spinnerFrom.getSelectedItem().toString());
            Integer max = Integer.parseInt(spinnerTo.getSelectedItem().toString());
            if (min > max) {
                Toast.makeText(getContext(), R.string.maxmin, Toast.LENGTH_SHORT).show();
            } else {
                filteredList = new ArrayList<>();
                for (CardData c : cardData) {
                    Integer reqAverage = (Integer) c.getData();
                    if (reqAverage >= min && reqAverage <= max) {
                        filteredList.add(c);
                    }
                }
                recad.filterList(filteredList);
                if (filteredList.isEmpty()){
                    noItemText.setVisibility(View.VISIBLE);
                } else {
                    noItemText.setVisibility(View.GONE);

                }

            }
        });

        close2.setOnClickListener(view -> {
            searchAverageLayout.setVisibility(View.GONE);
            isOpen = false;
            getTheses();
        });
    }

    //inizializzazione layout e strumenti di ricerca
    private void searchThesis(int caller) {
        searchAverageLayout.setVisibility(View.GONE);
        searchThesisLayout.setVisibility(View.VISIBLE);
        isOpen = true;
        searchView.setQuery("", false);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        if (caller == 1) {
            searchView.setQueryHint(getString(R.string.insTitle));
        } else if (caller == 2) {
            searchView.setQueryHint(getString(R.string.insTeacher));
        }

        Button close = view.findViewById(R.id.closebutton);
        close.setOnClickListener(view -> {
            searchView.setQuery("", false);
            isOpen = false;
            searchThesisLayout.setVisibility(View.GONE);
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

    //ricerca per professore
    private void filterTeacher(String s) {
        filteredList = new ArrayList<>();
        for (CardData c : cardData) {
            if (c.getSubtitle().toLowerCase().contains(s.toLowerCase())) {
                filteredList.add(c);
            }
        }
        recad.filterList(filteredList);

        if (filteredList.isEmpty()){
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
        }
    }

    //ricerca per titolo tesi
    private void filterTitle(String s) {
        filteredList = new ArrayList<>();
        for (CardData c : cardData) {
            if (c.getTitle().toLowerCase().contains(s.toLowerCase())) {
                filteredList.add(c);
            }
        }
        recad.filterList(filteredList);

        if (filteredList.isEmpty()){
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
        }
    }

    //riempimento della Recycler con tutte le tesi disponibili
    private void getTheses() {
        thesisService.getAllTheses().subscribe(theses -> {
            cardData = new ArrayList<>();
            for (Thesis t : theses) {
                CardData thesis = new CardData(t.title, t.teacherFullname, t.id, null, t.averageRequirement);
                cardData.add(thesis);
            }
            if (cardData.isEmpty()){
                noItemText.setVisibility(View.VISIBLE);
            } else {
                noItemText.setVisibility(View.GONE);
                RecyclerView rec = view.findViewById(R.id.availableRecycler);
                recad = new RecyclerAdapter(cardData, getContext(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rec.setLayoutManager(linearLayoutManager);
                rec.setAdapter(recad);
            }
        });
    }

    //click sulla card per visualizzare il dettaglio tesi
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        String id;
        if (isOpen) {
            id = filteredList.get(position).getId();
        } else {
            id = cardData.get(position).getId();
        }
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
            getTheses();
        }
    }
}