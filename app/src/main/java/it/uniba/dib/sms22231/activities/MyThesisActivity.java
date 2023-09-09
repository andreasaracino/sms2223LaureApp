package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.VPAdapter;
import it.uniba.dib.sms22231.fragments.MyThesisFragment;
import it.uniba.dib.sms22231.fragments.TaskFragment;

public class MyThesisActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_thesis);

        useViewPagerAdapter();
    }

    private void useViewPagerAdapter(){
        tabLayout = findViewById(R.id.myThesisTab);
        viewPager2 = findViewById(R.id.myThesisViewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(vpAdapter);
        vpAdapter.addFragment(new MyThesisFragment());
        vpAdapter.addFragment(new TaskFragment());

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText(getString(R.string.my_thesis));
                    tab.setIcon(R.drawable.miatesi);
                    break;
                case 1:
                    tab.setText(getString(R.string.Task));
                    tab.setIcon(R.drawable.task);
            }
        }).attach();
    }

}