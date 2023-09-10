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
import it.uniba.dib.sms22231.fragments.RankingFragment;

public class AllThesesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_theses);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.all_theses);
        actionBar.setDisplayHomeAsUpEnabled(true);

       useViewPagerAdapter();
    }

    private void useViewPagerAdapter(){
        tabLayout = findViewById(R.id.studentTab);
        viewPager2 = findViewById(R.id.allThesesViewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(vpAdapter);
        vpAdapter.addFragment(new AvailableFragment());
        vpAdapter.addFragment(new RankingFragment());

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(getString(R.string.available));
                    tab.setIcon(R.drawable.available);
                    break;
                case 1:
                    tab.setText(getString(R.string.rank));
                    tab.setIcon(R.drawable.rank);
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