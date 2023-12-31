package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.adapters.VPAdapter;
import it.uniba.dib.sms22231.config.MessageReferenceType;
import it.uniba.dib.sms22231.fragments.InProgressFragment;
import it.uniba.dib.sms22231.fragments.MeetingFragment;
import it.uniba.dib.sms22231.fragments.MyThesisFragment;
import it.uniba.dib.sms22231.fragments.TaskFragment;
import it.uniba.dib.sms22231.model.MessageReference;
import it.uniba.dib.sms22231.service.ChatService;

public class MyThesisActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private VPAdapter vpAdapter;
    private String applicationId;
    private ChatService chatService = ChatService.getInstance();
    private int caller;
    private Bundle bundle;
    private MyThesisFragment myThesisFragment;
    private TaskFragment taskFragment;
    private MeetingFragment meetingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_thesis);

        Intent intent = getIntent();
        caller = intent.getIntExtra("caller", 0);
        applicationId = intent.getStringExtra("id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.my_thesis);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupBottomBar();
        useViewPagerAdapter();
    }

    //selezione attraverso TabLayout del fragment da visualizzare
    private void useViewPagerAdapter(){
        tabLayout = findViewById(R.id.myThesisTab);
        viewPager2 = findViewById(R.id.myThesisViewPager);

        vpAdapter = new VPAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(vpAdapter);

        bundle = new Bundle();
        bundle.putString("applicationId", applicationId);
        bundle.putInt("caller", caller);

        myThesisFragment = new MyThesisFragment();
        myThesisFragment.setArguments(bundle);
        vpAdapter.addFragment(myThesisFragment);

        taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);
        vpAdapter.addFragment(taskFragment);

        meetingFragment = new MeetingFragment();
        meetingFragment.setArguments(bundle);
        vpAdapter.addFragment(meetingFragment);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    if (caller == 4){
                        tab.setText(R.string.thesis);
                        tab.setIcon(R.drawable.my_theses);
                    } else {
                        tab.setText(getString(R.string.my_thesis));
                        tab.setIcon(R.drawable.my_thesis);
                    }
                    break;
                case 1:
                    tab.setText(getString(R.string.Task));
                    tab.setIcon(R.drawable.task);
                    break;
                case 2:
                    tab.setText(R.string.meeting);
                    tab.setIcon(R.drawable.meeting);
            }
        }).attach();
    }

    public void setupBottomBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.chat) {
                goToChat();
            }

            return true;
        });
    }

    private void goToChat() {
        chatService.getChatByApplicationId(applicationId).subscribe(chat -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chat", chat);
            startActivity(intent);
        });
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