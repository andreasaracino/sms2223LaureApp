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

    // Inizializzo i componenti grafici
    private void initUi() {
        emailField = findViewById(R.id.signUpEmail);
        passwordField = findViewById(R.id.signUpPassword);
    }

    // Chiudo l'activity passato alla SignInActivity
    public void goToSignIn(View view) {
        finish();
    }

    // Effettua la richiesta di registrazione dell'account e, se l'operazione va a buon fine, si visualizza un toast apposito
    // e si viene trasferiti alla pagina di login, mentre se fallisce viene mostrato un toas di errore
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