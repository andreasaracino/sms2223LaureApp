package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.VPAdapter;
import it.uniba.dib.sms22231.fragments.AvailableFragment;
import it.uniba.dib.sms22231.fragments.InProgressFragment;
import it.uniba.dib.sms22231.fragments.RankingFragment;
import it.uniba.dib.sms22231.fragments.RequestsFragment;

public class MyStudentsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_students);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_students);
        actionBar.setDisplayHomeAsUpEnabled(true);

        useViewPagerAdapter();

    }

    //selezione attraverso TabLayout del fragment da visualizzare
    private void useViewPagerAdapter(){
        tabLayout = findViewById(R.id.myStudentsTab);
        viewPager2 = findViewById(R.id.myStudentsViewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(vpAdapter);
        vpAdapter.addFragment(new RequestsFragment());
        vpAdapter.addFragment(new InProgressFragment());

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(getString(R.string.requests));
                    tab.setIcon(R.drawable.request);
                    break;
                case 1:
                    tab.setText(getString(R.string.progress));
                    tab.setIcon(R.drawable.progress);
            }
        }).attach();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}