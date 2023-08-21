package it.uniba.dib.sms22231;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void initUi() {
        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToDashboard();
        }
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLoading(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        loginButton.setEnabled(!loading);
    }

    public void doLogin(View view) {
        setLoading(true);
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                setLoading(false);

                if (task.isSuccessful()) {
                    goToDashboard();
                } else {
                    Toast.makeText(SignInActivity.this, R.string.sign_in_fail, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}