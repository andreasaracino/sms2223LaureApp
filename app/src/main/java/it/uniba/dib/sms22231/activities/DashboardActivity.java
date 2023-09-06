package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms22231.R;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;

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
        user = mAuth.getCurrentUser();
    }

    public void doLogout(View view) {
        mAuth.signOut();
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