package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

    private void initUi() {
        fullNameField = findViewById(R.id.nameField);
        roleSpinner = findViewById(R.id.roleSpinner);
        regNumberField = findViewById(R.id.regNumberField);
    }

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

    private void goToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteUser(View view) {
        userService.deleteUser(success -> {
            if (success) {
                Toast.makeText(this, R.string.accountDeleted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.loginAgain, Toast.LENGTH_SHORT).show();
            }

            goToLogin();
        });
    }
}