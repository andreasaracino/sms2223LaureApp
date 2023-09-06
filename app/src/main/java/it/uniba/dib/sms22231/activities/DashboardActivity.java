package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.UserService;

public class DashboardActivity extends AppCompatActivity {
    private final UserService userService = UserService.getInstance();
    private User user;

    private Button dash1;
    private Button dash2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dash1 = findViewById(R.id.dash1);
        dash2 = findViewById(R.id.dash2);

        userService.userObservable.subscribe(user -> {
            this.user = user;
            initDashboard();
        });
    }

    private void initDashboard() {
        switch (user.userType) {
            case STUDENT:
                dash1.setText(R.string.all_theses);
                dash2.setText(R.string.my_thesis);
                break;
            case TEACHER:
                dash1.setText(R.string.my_theses);
                dash2.setText(R.string.my_students);
        }
    }

    public void doLogout(View view) {
        userService.signOut();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUserInformation() {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.account) {
            goToUserInformation();
        } else if (id == R.id.logout) {
            doLogout(null);
        }
        return true;
    }
}