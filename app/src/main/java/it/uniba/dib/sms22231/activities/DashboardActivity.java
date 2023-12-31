package it.uniba.dib.sms22231.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.config.UserTypes;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.service.StudentService;
import it.uniba.dib.sms22231.service.UserService;

public class DashboardActivity extends AppCompatActivity {
    private final UserService userService = UserService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();
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

    //inizializzazione della dashboard in base all'utente
    private void initDashboard() {
        if (user.userType == null) {
            return;
        }
        switch (user.userType) {
            case STUDENT:
                setDash(dash2, R.drawable.my_thesis, R.string.my_thesis);
            case GUEST:
                setDash(dash1, R.drawable.all_theses, R.string.all_theses);
                break;
            case TEACHER:
                setDash(dash1, R.drawable.my_theses, R.string.my_theses);
                setDash(dash2, R.drawable.my_students, R.string.my_students);
        }
    }

    //inizializzazione dei MateriaButton
    public void setDash(MaterialButton dash, int iconId, int textId) {
        dash.setText(textId);
        dash.setIconSize(200);
        dash.setIconPadding(50);
        dash.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
        dash.setIconResource(iconId);
        dash.setVisibility(View.VISIBLE);
    }

    //onClick del primo MaterialButton in base all'utente
    public void dash1OnClick(View view) {
        Intent intent = null;
        if (user.userType == null) {
            return;
        }
        switch (user.userType) {
            case GUEST:
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

    //onClick del secondo MaterialButton in base all'utente
    public void dash2OnClick(View view) {
        if (user.userType == null) {
            return;
        }

        switch (user.userType) {
            case STUDENT:
                String applicationId = studentService.getStudentData().currentApplicationId;
                if (applicationId != null) {
                    applicationService.getApplicationById(applicationId).subscribe(application -> {
                        ApplicationStatus status = application.status;
                        if (status == ApplicationStatus.approved) {
                            Intent intent = new Intent(this, MyThesisActivity.class);
                            intent.putExtra("id", applicationId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, getString(R.string.noAccepted), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, getString(R.string.noAccepted), Toast.LENGTH_SHORT).show();
                }
                break;
            case TEACHER:
                Intent intent = new Intent(this, MyStudentsActivity.class);
                startActivity(intent);
        }

    }

    //click sull'item logout del menu
    public void doLogout(View view) {
        userService.signOut();
        goToLogin();
    }

    //ritorno all'activity di login dopo il logout
    private void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    //click sull'item account del menu
    private void goToUserInformation() {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }

    //creazione del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null && user.userType == UserTypes.GUEST) {
            FloatingActionButton fab = findViewById(R.id.chatFab);
            fab.setVisibility(View.GONE);
            getMenuInflater().inflate(R.menu.guest_dashboard_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        }

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

    //scelta degli item del menu
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

    public void openChat(View view) {
        Intent intent = new Intent(this, ChatListActivity.class);
        startActivity(intent);
    }
}