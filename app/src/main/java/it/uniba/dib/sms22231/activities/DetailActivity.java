package it.uniba.dib.sms22231.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Arrays;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;

public class DetailActivity extends AppCompatActivity {
    private final ThesisService thesisService = ThesisService.getInstance();
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtOwner;
    private final RequirementService requirementService = RequirementService.getInstance();
    private ListView reqListview;
    private ArrayList<String> req;
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private ListView fileListView;
    private ArrayList<String> attach;
    private BottomNavigationView bottomNavigationView;
    private int caller;
    private String id;
    private ArrayList<String> examArrayList;
    private TextView txtNoRequirement;
    private TextView txtNoFile;
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

    private void fillActivity() {
        txtTitle = findViewById(R.id.titleText);
        txtDescription = findViewById(R.id.descriptionText);
        txtOwner = findViewById(R.id.ownerText);
        reqListview = findViewById(R.id.reqList);
        fileListView = findViewById(R.id.fileList);
        txtNoRequirement = findViewById(R.id.textNoReq);
        txtNoFile = findViewById(R.id.textNoFile);

        thesisService.getThesisById(id, thesis -> {
            txtTitle.setText(thesis.title);
            String temp = getString(R.string.teacher) + ": " + thesis.teacherFullname;
            txtOwner.setText(temp);
            txtDescription.setText(thesis.description);

            requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                req = new ArrayList<>();
                examArrayList = new ArrayList<>();
                for (Requirement requirement : requirements) {
                    String reqtemp = requirement.description + ": " + requirement.value;
                    req.add(reqtemp);
                    if (requirement.description.equals(getString(R.string.exam))) {
                        String examtemp = requirement.description + ": " + requirement.value;
                        examArrayList.add(examtemp);
                    }
                    if (requirement.description.equals(getString(R.string.average))) {
                        averageControl = true;
                    }
                }
                if (req.isEmpty()) {
                    txtNoRequirement.setVisibility(View.VISIBLE);
                    txtNoRequirement.setText(R.string.noReq);
                } else {
                    fillList(req, reqListview);
                }
            });
            attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                attach = new ArrayList<>();
                for (Attachment attachment : attachments) {
                    String attachtemp = attachment.fileName;
                    attach.add(attachtemp);
                }
                if (attach.isEmpty()) {
                    txtNoFile.setVisibility(View.VISIBLE);
                    txtNoFile.setText(R.string.noFile);
                } else {
                    fillList(attach, fileListView);
                }
            });
        });
    }

    private void createBottomAppBar() {

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        switch (caller) {
            case 1:
                bottomNavigationView.inflateMenu(R.menu.detail_available_bottom_menu);
                break;
            case 2:
                bottomNavigationView.inflateMenu(R.menu.detail_my_theses_bottom_menu);
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.req) {
                    doRequest();
                }
                if (item.getItemId() == R.id.favorite) {
                    //TODO
                }
                if (item.getItemId() == R.id.share) {
                    sendMessage();
                }
                if (item.getItemId() == R.id.qr) {
                    generateQr();
                }
                if (item.getItemId() == R.id.chat) {
                    //TODO
                }
                if (item.getItemId() == R.id.modify) {
                    thesisMod();
                }
                return false;
            }
        });
    }

    private void thesisMod() {
        Intent intent = new Intent(this, AddThesisActivity.class);
        intent.putExtra("caller",3);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void doRequest() {
        if (!req.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.reqDialog);
            builder.setPositiveButton("Ok",null);

            if (!examArrayList.isEmpty()) {
                String[] examArray = new String[examArrayList.size()];
                examArray = examArrayList.toArray(examArray);
                final boolean[] checked = new boolean[examArrayList.size()];
                Arrays.fill(checked, false);
                builder.setMultiChoiceItems(examArray, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        checked[i] = b;
                    }
                });
            }
            EditText average = new EditText(this);
            if (averageControl) {

                average.setHint(R.string.average);
                LinearLayout parentla = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(50, 16, 50, 0);
                average.setLayoutParams(layoutParams);
                parentla.addView(average);
                builder.setView(parentla);
            }

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (averageControl){
                        String control = average.getText().toString();
                        if (control.trim().isEmpty()){
                            average.setError(getText(R.string.error));
                            average.requestFocus();
                        }else {
                            dialog.dismiss();
                            callSecondDialog();}
                    } else{
                        dialog.dismiss();
                        callSecondDialog();}
                }
            });
        } else {
            callSecondDialog();
        }

    }

    private void callSecondDialog() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(R.string.sent)
                .setPositiveButton("Ok", null)
                .create().show();
    }

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

    private void fillList(ArrayList<String> arrayList, ListView listView) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listAdapter);
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

    private void sendMessage() {
        String message = getThesisText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }
}