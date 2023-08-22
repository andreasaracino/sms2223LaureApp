package it.uniba.dib.sms22231.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.dib.sms22231.R;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUi();
    }

    private void initUi() {
        emailField = findViewById(R.id.signUpEmail);
        passwordField = findViewById(R.id.signUpPassword);
        signUpButton = findViewById(R.id.signUpButton);
    }

    public void goToSignIn(View view) {
        finish();
    }

    public void doSignUp(View view){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();
                    mAuth.signOut();

                    Toast.makeText(this, R.string.verify_mail, Toast.LENGTH_SHORT).show();
                    goToSignIn(null);
                } else {
                    Toast.makeText(this, R.string.sign_up_fail, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}