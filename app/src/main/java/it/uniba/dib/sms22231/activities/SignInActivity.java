package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private ProgressBar progressBar;

    // Inizializzo l'istanza di FirebaseAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUi();
    }

    // Ottengo un'istanza di UserService e controllo se l'utente è loggato
    @Override
    protected void onStart() {
        super.onStart();

        userService = UserService.getInstance();
        checkLogin(false);
    }

    // Inizializzo i componenti grafici
    private void initUi() {
        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    // Controlli sull'autenticazione. Se l'utente è loggato e la mail verificata si viene trasferiti alla dashboard
    // se invece la mail non è verificata o l'utente non si è loggato con successo, viene mostrato un toast apposito
    private void checkLogin(Boolean loggingIn) {
        if (userService.isLoggedIn()) {
            if (userService.isEmailVerified()) {
                goToDashboard();
            } else {
                Toast.makeText(this, R.string.verify_mail, Toast.LENGTH_SHORT).show();
            }
        } else if (loggingIn) {
            Toast.makeText(this, R.string.sign_in_fail, Toast.LENGTH_SHORT).show();
        }
    }

    // Si passa il controllo alla DashboardActivity
    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    // Al click dell'utente si passa alla SignUpActivity
    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // Blocco i campi in sola lettura mentre viene effettuato il login
    private void setLoading(Boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        loginButton.setEnabled(!loading);
    }

    // Viene richiamato il metodo signIn dello UserService e quando si ha un esito positivo si chiama checkLogin, altrimenti
    // si visualizza un messagio di errore
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

    // Al click sul pulsante per il reset della password, viene mostrata una dialog di conferma, successivamente viene inviata una
    // richiesta di reset della password
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
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Si effettua un login come utente ospite
    public void loginAsGuest(View view) {
        userService.signInAsGuest();
        goToDashboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.direct_access_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String directEmail;
        String directPassword;
        if (item.getItemId() == R.id.asTeacher){
            directEmail = "m.piteo@studenti.uniba.it";
            directPassword = "test123";
            directLogIn(directEmail, directPassword);
        } else if (item.getItemId() == R.id.asStudent) {
            directEmail = "a.saracino62@studenti.uniba.it";
            directPassword = "test123";
            directLogIn(directEmail, directPassword);
        }
        return super.onOptionsItemSelected(item);
    }

    private void directLogIn(String directEmail, String directPassword) {
        userService.signIn(directEmail, directPassword, isSuccessful -> {
            setLoading(false);

            if (isSuccessful) {
                checkLogin(true);
            } else {
                Toast.makeText(SignInActivity.this, R.string.sign_in_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }
}