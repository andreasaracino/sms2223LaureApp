package it.uniba.dib.sms22231.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.service.ThesisService;

public class AddThesisActivity extends AppCompatActivity {
    private ThesisService thesisService = ThesisService.getInstance();

    private ActivityResultLauncher<Intent> resultLauncher;
    private EditText thesisTitle;
    private EditText thesisDescription;
    private ListView listViewFile;
    private ListView listViewReq;
    private ArrayList<String> fileName;
    private ArrayList<Requirement> requirements;
    private ArrayAdapter<String> listadapter;
    private ArrayList<Uri> filesList;
    private ArrayList<String> reqString;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thesis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.addThesis);
        actionBar.setDisplayHomeAsUpEnabled(true);

        thesisTitle = findViewById(R.id.titleEditText);
        thesisDescription = findViewById(R.id.descriptionEditText);
        listViewFile = findViewById(R.id.fileListView);
        fileName = new ArrayList<>();
        listViewReq = findViewById(R.id.reqListView);
        requirements = new ArrayList<>();
        filesList = new ArrayList<>();
        reqString = new ArrayList<>();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    String name = getNameFromUri(uri);
                    fileName.add(name);
                    filesList.add(uri);
                    fillList(fileName, listViewFile);
                }
            }
        });

        listViewFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(AddThesisActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_item_delete, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    AlertDialog dialog = new AlertDialog.Builder(AddThesisActivity.this)
                            .setMessage(R.string.suredelete)
                            .setPositiveButton("Ok", null)
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(view1 -> {
                        fileName.remove(i);
                        filesList.remove(i);
                        fillList(fileName, listViewFile);
                        dialog.dismiss();
                    });
                    return false;
                });
                popupMenu.show();
            }
        });

        listViewReq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(AddThesisActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_item_delete, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    AlertDialog dialog = new AlertDialog.Builder(AddThesisActivity.this)
                            .setMessage(R.string.suredelete)
                            .setPositiveButton("Ok", null)
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(view1 -> {
                        reqString.remove(i);
                        requirements.remove(i);
                        fillList(reqString, listViewReq);
                        dialog.dismiss();
                    });
                    return false;
                });
                popupMenu.show();
            }
        });
    }

    public void deletePopup(View view) {

    }

    public void deleteAlert() {

    }

    private void fillList(ArrayList<String> arrayList, ListView listView) {
        listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listadapter);
    }

    private String getNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(nameIndex);
    }

    public void addFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        resultLauncher.launch(intent);
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

    public void addRequirement(View view) {

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert, null);
        AlertDialog builder = new AlertDialog.Builder(this)
                .setView(customLayout)
                .setTitle(R.string.requirement)
                .setMessage(R.string.addReq)
                .setPositiveButton("OK", null)
                .setNegativeButton(R.string.cancel, null)
                .show();

        builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            Requirement req = null;
            Spinner spinner = customLayout.findViewById(R.id.reqSpinner);
            EditText editText = customLayout.findViewById(R.id.reqEdit);

            String control = editText.getText().toString();

            String item = null;

            if (control.trim().isEmpty()) {
                editText.setError(getText(R.string.error));
                editText.requestFocus();
            } else {
                int position = spinner.getSelectedItemPosition();
                switch (position) {
                    case 0:
                        req = new Requirement(null, null, getString(R.string.average), control);
                        item = (getString(R.string.average) + ": " + control);
                        break;
                    case 1:
                        req = new Requirement(null, null, getString(R.string.exam), control);
                        item = (getString(R.string.exam) + ": " + control);
                        break;
                    case 2:
                        req = new Requirement(null, null, getString(R.string.skill), control);
                        item = (getString(R.string.skill) + ": " + control);
                        break;
                    case 3:
                        req = new Requirement(null, null, getString(R.string.timelimit), control);
                        item = (getString(R.string.timelimit) + ": " + control);

                }
                requirements.add(req);
                reqString.add(item);
                fillList(reqString, listViewReq);
                builder.dismiss();
            }
        });
    }

    public void onSave(View view) {
        Thesis thesis = new Thesis();

        thesis.title = thesisTitle.getText().toString();
        thesis.description = thesisDescription.getText().toString();

        thesisService.saveNewThesis(thesis, requirements, filesList, isSuccessful -> {
            Toast.makeText(this, isSuccessful.toString(), Toast.LENGTH_SHORT).show();
        });
    }

}