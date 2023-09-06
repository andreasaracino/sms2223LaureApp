package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.UserService;

public class DashboardActivity extends AppCompatActivity {
    private final UserService userService = UserService.getInstance();
    private User user;
    private TextView label;

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
        label = findViewById(R.id.textView);

        userService.userObservable.subscribe(user -> {
            this.user = user;
            label.setText(user.fullName);
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }
}