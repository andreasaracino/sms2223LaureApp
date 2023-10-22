package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.service.UserService;

public class SignInActivity extends AppCompatActivity {
    private UserService userService;
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

        userService = UserService.getInstance();
        checkLogin(false);
    }

    private void initUi() {
        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkLogin(Boolean loggingIn) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                goToDashboard();
            } else {
                Toast.makeText(this, R.string.verify_mail, Toast.LENGTH_SHORT).show();
            }
        } else if (loggingIn) {
            Toast.makeText(this, R.string.sign_in_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
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

        userService.signIn(email, password, isSuccessful -> {
            setLoading(false);

            if (isSuccessful) {
                checkLogin(true);
            } else {
                Toast.makeText(SignInActivity.this, R.string.sign_in_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.resetPassword);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.prompt_email);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> userService.resetPassword(input.getText().toString(), success -> {
            if (success) {
                Toast.makeText(this, R.string.verify_mail, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show();
            }
        }));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}