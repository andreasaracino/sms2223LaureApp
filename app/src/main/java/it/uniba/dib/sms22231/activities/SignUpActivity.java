package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.service.UserService;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private final UserService userService = UserService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.signUp);

        initUi();
    }

    private void initUi() {
        emailField = findViewById(R.id.signUpEmail);
        passwordField = findViewById(R.id.signUpPassword);
    }

    public void goToSignIn(View view) {
        finish();
    }

    public void doSignUp(View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        userService.signUp(email, password, success -> {
            if (success) {
                Toast.makeText(this, R.string.verify_mail, Toast.LENGTH_SHORT).show();
                goToSignIn(null);
            } else {
                Toast.makeText(this, R.string.sign_up_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }
}