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
import it.uniba.dib.sms22231.fragments.MeetingFragment;
import it.uniba.dib.sms22231.fragments.MyThesisFragment;
import it.uniba.dib.sms22231.fragments.TaskFragment;

public class MyThesisActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_thesis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_thesis);
        actionBar.setDisplayHomeAsUpEnabled(true);

        useViewPagerAdapter();
    }

    //selezione attraverso TabLayout del fragment da visualizzare
    private void useViewPagerAdapter(){
        tabLayout = findViewById(R.id.myThesisTab);
        viewPager2 = findViewById(R.id.myThesisViewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(vpAdapter);
        vpAdapter.addFragment(new MyThesisFragment());
        vpAdapter.addFragment(new TaskFragment());
        vpAdapter.addFragment(new MeetingFragment());

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(getString(R.string.my_thesis));
                    tab.setIcon(R.drawable.miatesi);
                    break;
                case 1:
                    tab.setText(getString(R.string.Task));
                    tab.setIcon(R.drawable.task);
                    break;
                case 2:
                    tab.setText(R.string.meeting);
                    tab.setIcon(R.drawable.meet);
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