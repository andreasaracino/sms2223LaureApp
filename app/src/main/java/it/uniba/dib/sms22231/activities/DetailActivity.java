package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Arrays;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.ApplicationStatus;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Application;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ApplicationService;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.ChatService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.StudentService;
import it.uniba.dib.sms22231.service.ThesisService;

public class DetailActivity extends AppCompatActivity {
    private final ThesisService thesisService = ThesisService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtOwner;
    private TextView txtNoRequirement;
    private TextView txtNoFile;
    private ListView reqListview;
    private ListView fileListView;
    private ArrayList<String> req;
    private ArrayList<String> attach;
    private ArrayList<String> examArrayList;
    private ArrayList<String> examsIds;
    private BottomNavigationView bottomNavigationView;
    private String id;
    private String averageId;
    private Thesis thesis;
    private int caller;
    private boolean averageControl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.detail);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        caller = intent.getIntExtra("caller", 0);

        createBottomAppBar();

        fillActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillActivity();
    }

    //l'activity viene riempita con tutti i dati della tesi
    private void fillActivity() {
        txtTitle = findViewById(R.id.titleText);
        txtDescription = findViewById(R.id.descriptionText);
        txtOwner = findViewById(R.id.ownerText);
        reqListview = findViewById(R.id.reqList);
        fileListView = findViewById(R.id.fileList);
        txtNoRequirement = findViewById(R.id.textNoReq);
        txtNoFile = findViewById(R.id.textNoFile);

        thesisService.getThesisById(id, thesis -> {
            this.thesis = thesis;
            checkFavorite();
            txtTitle.setText(thesis.title);
            String temp = getString(R.string.teacher) + ": " + thesis.teacherFullname;
            txtOwner.setText(temp);
            txtDescription.setText(thesis.description);

            requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                req = new ArrayList<>();
                examArrayList = new ArrayList<>();
                examsIds = new ArrayList<>();

                for (Requirement requirement : requirements) {
                    String reqtemp = getResources().getStringArray(R.array.requirements)[requirement.description.ordinal()] + ": " + requirement.value;
                    req.add(reqtemp);
                    //arraylist degli esami da utilizzare per la creazione dell'application
                    if (requirement.description == RequirementTypes.exam) {
                        examArrayList.add(requirement.value);
                        examsIds.add(requirement.id);
                    }
                    //controllo della presenza della media e id da utilizzare per la creazione dell'application
                    if (requirement.description == RequirementTypes.average) {
                        averageControl = true;
                        averageId = requirement.id;
                    }
                }
                if (req.isEmpty()) {
                    reqListview.setVisibility(View.GONE);
                    txtNoRequirement.setVisibility(View.VISIBLE);
                } else {
                    fillList(req, reqListview);
                }
            });
            attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                attach = new ArrayList<>();
                for (Attachment attachment : attachments) {
                    String attachtemp = attachment.getFileName();
                    attach.add(attachtemp);
                }
                if (attach.isEmpty()) {
                    fileListView.setVisibility(View.GONE);
                    txtNoFile.setVisibility(View.VISIBLE);
                } else {
                    fillList(attach, fileListView);
                }
            });
        });
    }

    //creazione della BottomAppBar e inserimento dei menu in base all'utente
    private void createBottomAppBar() {

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        switch (caller) {
            case 1:
                bottomNavigationView.inflateMenu(R.menu.detail_available_bottom_menu);
                break;
            case 2:
                bottomNavigationView.inflateMenu(R.menu.detail_my_theses_bottom_menu);
        }
        //click sugli item del menu
        bottomNavigationView.setOnItemSelectedListener(item -> {
             if (item.getItemId() == R.id.req) {
                 doRequest();
             } else if (item.getItemId() == R.id.favorite) {
                 addFavorite();
             } else if (item.getItemId() == R.id.share) {
                 sendMessage();
             } else if (item.getItemId() == R.id.qr) {
                 generateQr();
             } else if (item.getItemId() == R.id.chat) {
                 goToChat();
             } else if (item.getItemId() == R.id.modify) {
                 thesisMod();
             } else if (item.getItemId() == R.id.deleteThesis) {
                 deleteThesis();
             }
             return false;
         });
    }

    //eliminazione della tesi
    private void deleteThesis() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sureDeleteThesis)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    thesisService.removeThesis(thesis.id, (success) -> {
                        if (success) {
                            finish();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null).show();
    }

    //apertura della chat
    private void goToChat() {
//        Intent intent = new Intent(this, ChatActivity.class);
//        ChatService.getInstance().getChatByTeacherId(thesis.teacherId).subscribe(chat -> {
//            intent.putExtra("chat", chat);
//            startActivity(intent);
//        });
    }

    //aggiunta o rimozione della della tesi dalla classifica dei preferiti
    private void addFavorite() {
        studentService.addThesisToFavourites(thesis, isFavorite -> {
            Toast.makeText(this, isFavorite ? R.string.favThesis : R.string.remFavorite, Toast.LENGTH_SHORT).show();
            checkFavorite();
        });
    }

    //modifica dell'icona dei preferiti
    private void checkFavorite() {
        if (caller == 1) {
            MenuItem item = bottomNavigationView.getMenu().findItem(R.id.favorite);
            if (studentService.isThesisFavorite(thesis)) {
                item.setIcon(R.drawable.favorite_full);
            } else {
                item.setIcon(R.drawable.outline_favorite_border_24);
            }
        }
    }

    //apertura della modifica della tesi
    private void thesisMod() {
        Intent intent = new Intent(this, AddModifyThesisActivity.class);
        intent.putExtra("caller", 3);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    //richiesta della tesi
    private void doRequest() {

        ArrayList<Requirement> studentRequirements = new ArrayList<>();
        if (!examArrayList.isEmpty()) {
            callExamDialog(studentRequirements);
        } else if (averageControl) {
            callAverageDialog(studentRequirements);
        }
        if (req.isEmpty()) {
            callConfirmDialog(studentRequirements);
        }
    }

    //inserimento della media dello studente per richiedere la tesi
    private void callAverageDialog(ArrayList<Requirement> studentRequirements) {
        EditText average = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.youraverage)
                .setPositiveButton(R.string.next, null)
                .setNegativeButton(R.string.cancel, null);
        average.setHint(R.string.average);
        LinearLayout parentla = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(50, 16, 50, 0);
        average.setLayoutParams(layoutParams);
        parentla.addView(average);
        builder.setView(parentla);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String control = average.getText().toString();
            if (control.trim().isEmpty()) {
                average.setError(getText(R.string.error));
                average.requestFocus();
            } else {
                Requirement requirement = new Requirement();
                requirement.thesisId = thesis.id;
                requirement.description = RequirementTypes.average;
                requirement.value = average.getText().toString();
                requirement.id = averageId;
                studentRequirements.add(requirement);
                dialog.dismiss();
                callConfirmDialog(studentRequirements);
            }
        });

    }

    //scelta degli esami dati per richiedere la tesi
    private void callExamDialog(ArrayList<Requirement> studentRequirements) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.yourexams)
                .setPositiveButton(R.string.next, null)
                .setNegativeButton(R.string.cancel, null);
        String[] examArray = new String[examArrayList.size()];
        examArray = examArrayList.toArray(examArray);
        final boolean[] checked = new boolean[examArrayList.size()];
        Arrays.fill(checked, false);
        builder.setMultiChoiceItems(examArray, checked, (dialogInterface, i, b) -> checked[i] = b);
        AlertDialog dialog = builder.create();
        dialog.show();
        String[] finalExamArray = examArray;
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            for (int i = 0; i < checked.length; i++) {
                if (checked[i]) {
                    Requirement requirement = new Requirement();
                    requirement.thesisId = thesis.id;
                    requirement.description = RequirementTypes.exam;
                    requirement.value = finalExamArray[i];
                    requirement.id = examsIds.get(i);
                    studentRequirements.add(requirement);
                }
            }
            dialog.dismiss();
            if (averageControl) {
                callAverageDialog(studentRequirements);
            } else {
                callConfirmDialog(studentRequirements);
            }
        });
    }

    //conferma della richiesta e creazione dell'application
    private void callConfirmDialog(ArrayList<Requirement> studentRequirements) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.requestConfirm)
                .setPositiveButton("Ok", null)
                .setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            Application application = new Application();
            application.status = ApplicationStatus.pending;
            application.thesisId = thesis.id;
            application.studentUid = studentService.getStudentData().uid;
            application.requirements = studentRequirements;

            applicationService.createApplication(application, isSuccessful -> {
                Toast.makeText(getApplicationContext(), R.string.sent, Toast.LENGTH_SHORT).show();
            });
            dialog.dismiss();
        });
    }

    //creazione di un codice QR per condividere i dati della tesi
    private void generateQr() {
        ImageView a = new ImageView(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String text = getThesisText();

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            a.setImageBitmap(bmp);
            builder.setTitle(R.string.thesisQr)
                    .setPositiveButton("OK", null)
                    .setView(a)
                    .create().show();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //riempimento delle liste
    private void fillList(ArrayList<String> arrayList, ListView listView) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listAdapter);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //creazione del testo da utilizzare per la creazione del QR o per la condivisione
    public String getThesisText() {
        String s = getString(R.string.title) + " " + txtTitle.getText().toString() + "\n"
                + txtOwner.getText().toString() + "\n"
                + getString(R.string.description) + " " + txtDescription.getText().toString() + "\n"
                + getString(R.string.req) + "\n";
        String s1;
        for (int i = 0; i < reqListview.getCount(); i++) {
            s1 = reqListview.getItemAtPosition(i).toString() + "\n";
            s = s + s1;
        }
        return s;
    }

    //condivisione della tesi tramite App esterne
    private void sendMessage() {
        String message = getThesisText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }
}