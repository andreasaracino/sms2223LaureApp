package it.uniba.dib.sms22231.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Method;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.UserService;

public class DashboardActivity extends AppCompatActivity {
    private final UserService userService = UserService.getInstance();
    private User user;

    private MaterialButton dash1;
    private MaterialButton dash2;

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

        dash1 = findViewById(R.id.dash1);
        dash2 = findViewById(R.id.dash2);

        userService.userObservable.subscribe(user -> {
            this.user = user;
            initDashboard();
        });
    }

    private void initDashboard() {
        if (user.userType == null) {
            return;
        }
        switch (user.userType) {
            case STUDENT:
                dash1.setText(R.string.all_theses);
                dash1.setIconResource(R.drawable.eletesi);
                dash1.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
                dash1.setIconSize(200);
                dash1.setIconPadding(50);
                dash2.setText(R.string.my_thesis);
                dash2.setIconResource(R.drawable.miatesi);
                dash2.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
                dash2.setIconSize(200);
                dash2.setIconPadding(50);
                break;
            case TEACHER:
                dash1.setText(R.string.my_theses);
                dash1.setIconResource(R.drawable.mietesi);
                dash1.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
                dash1.setIconSize(200);
                dash1.setIconPadding(50);
                dash2.setText(R.string.my_students);
                dash2.setIconResource(R.drawable.geststud);
                dash2.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
                dash2.setIconSize(200);
                dash2.setIconPadding(50);
        }
    }

    public void doLogout(View view) {
        userService.signOut();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUserInformation() {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);

        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e(TAG, "onMenuOpened", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.account) {
            goToUserInformation();
        } else if (id == R.id.logout) {
            doLogout(null);
        }
        return true;
    }
}