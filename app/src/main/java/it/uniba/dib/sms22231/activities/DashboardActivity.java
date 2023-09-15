package it.uniba.dib.sms22231.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.button.MaterialButton;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        dash1 = findViewById(R.id.dash1);
        dash2 = findViewById(R.id.dash2);

        userService.userObservable.subscribe(user -> {
            if (user.userType == null) {
                goToUserInformation();
                finish();
            }

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
                setDash(dash1, R.drawable.eletesi, R.string.all_theses);
                setDash(dash2, R.drawable.miatesi, R.string.my_thesis);
                break;
            case TEACHER:
                setDash(dash1, R.drawable.mietesi, R.string.my_theses);
                setDash(dash2, R.drawable.geststud, R.string.my_students);
        }
    }

    public void setDash(MaterialButton dash, int iconId, int textId) {
        dash.setText(textId);
        dash.setIconSize(200);
        dash.setIconPadding(50);
        dash.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
        dash.setIconResource(iconId);

    }

    public void dash1OnClick(View view) {
        Intent intent = null;
        if (user.userType == null) {
            return;
        }
        switch (user.userType) {
            case STUDENT:
                intent = new Intent(this, AllThesesActivity.class);
                break;
            case TEACHER:
                intent = new Intent(this, MyThesesActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    public void dash2OnClick(View view) {
        Intent intent = null;
        if (user.userType == null) {
            return;
        }
        switch (user.userType) {
            case STUDENT:
                intent = new Intent(this, MyThesisActivity.class);
                break;
            case TEACHER:
                intent = new Intent(this, MyStudentsActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
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

        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "onMenuOpened", e);
            } catch (Exception e) {
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