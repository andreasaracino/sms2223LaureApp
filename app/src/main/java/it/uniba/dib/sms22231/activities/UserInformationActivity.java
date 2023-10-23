package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.Observable;

public class UserInformationActivity extends AppCompatActivity {
    private User user;
    private Observable<User>.Subscription userSubscription;
    private UserService userService;
    private EditText fullNameField;
    private Spinner roleSpinner;
    private EditText regNumberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.account);

        initUi();
    }

    // Ottengo i dati dell'utente e compilo tutti i campi corrispondenti ai dati stessi
    // se l'utente era già registrato lo spinner di selezione del ruolo viene bloccato in sola lettura
    @Override
    protected void onStart() {
        super.onStart();

        userService = UserService.getInstance();

        userSubscription = userService.userObservable.subscribe(user -> {
            this.user = user;
            fullNameField.setText(user.fullName);
            regNumberField.setText(user.registrationNumber);
            if (user.userType != null) {
                roleSpinner.setSelection(user.userType.ordinal());
                roleSpinner.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        userSubscription.unsubscribe();
    }

    // inizializzo i componenti grafici
    private void initUi() {
        fullNameField = findViewById(R.id.nameField);
        roleSpinner = findViewById(R.id.roleSpinner);
        regNumberField = findViewById(R.id.regNumberField);
    }

    // Ottengo i dati inseriti nei vari campi e li salvo attraverso l'uso del UserService
    public void doConfirm(View view) {
        user.fullName = fullNameField.getText().toString();
        user.userType = UserTypes.values()[roleSpinner.getSelectedItemPosition()];
        user.registrationNumber = regNumberField.getText().toString();

        if (TextUtils.isEmpty(fullNameField.getText())){
            fullNameField.setError(getText(R.string.error));
            fullNameField.requestFocus();
        } else {
            userService.saveUserData(user, isSuccessful -> {
                if (isSuccessful) {
                    goToDashboard();
                } else {
                    Toast.makeText(this, "Error, try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Torno alla DashboardActivity
    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    // Vado alla SignInActivity
    private void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    // Al click del pulsante "Elimina Account" viene mostrata una dialog di conferma e se l'utente clicca "Sì" l'account viene eliminato
    public void deleteUser(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.deleteAccountPrompt)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton("No", null)
                .show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            dialog.dismiss();

            userService.deleteUser(success -> {
                if (success) {
                    Toast.makeText(this, R.string.accountDeleted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.loginAgain, Toast.LENGTH_SHORT).show();
                }

                goToLogin();
            });
        });
    }
}