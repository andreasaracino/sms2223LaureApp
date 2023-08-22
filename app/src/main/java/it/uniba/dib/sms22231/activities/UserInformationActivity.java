package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.UserService;
import it.uniba.dib.sms22231.utility.Observable;

public class UserInformationActivity extends AppCompatActivity {
    private User user;
    private Observable<User>.Subscription userSubscription;
    private UserService userService;
    private EditText fullNameField;
    private EditText phoneNumberField;
    private EditText regNumberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        initUi();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userService = UserService.getInstance();
        userSubscription = userService.userObservable.subscribe(user -> {
            if (user != null) {
                this.user = user;
                fullNameField.setText(user.fullName);
                phoneNumberField.setText(user.phoneNumber);
                regNumberField.setText(user.registrationNumber);
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
        phoneNumberField = findViewById(R.id.phoneField);
        regNumberField = findViewById(R.id.regNumberField);
    }

    public void doConfirm(View view) {
        user.fullName = fullNameField.getText().toString();
        user.phoneNumber = phoneNumberField.getText().toString();
        user.registrationNumber = regNumberField.getText().toString();
        userService.saveUserData(user, isSuccessful -> finish());
    }
}