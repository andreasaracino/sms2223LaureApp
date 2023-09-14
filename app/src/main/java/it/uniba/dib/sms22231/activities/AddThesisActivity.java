package it.uniba.dib.sms22231.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert, null);
        builder.setView(customLayout);
        builder.setTitle(R.string.requirement);
        builder.setMessage(R.string.addReq);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Requirement req = null;
            Spinner spinner = customLayout.findViewById(R.id.reqSpinner);
            EditText editText = customLayout.findViewById(R.id.reqEdit);
            int position = spinner.getSelectedItemPosition();
            switch (position) {
                case 0:
                    req = new Requirement(null, getString(R.string.average), null, (editText.getText().toString()));
                    break;
                case 1:
                    req = new Requirement(null, getString(R.string.exam), null, (editText.getText().toString()));
                    break;
                case 2:
                    req = new Requirement(null, getString(R.string.skill), null, (editText.getText().toString()));
                    break;
                case 3:
                    req = new Requirement(null, getString(R.string.timelimit), null, (editText.getText().toString()));

            }
            String control =editText.getText().toString();
            if ( control.trim().isEmpty() ) {
                editText.setError(getText(R.string.error));
                editText.requestFocus();
            } else {
                requirements.add(req);
                //fillList(requirements.to, listViewReq);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSave(View view) {
        Thesis thesis = new Thesis();

        thesis.title = thesisTitle.getText().toString();
        thesis.description = thesisDescription.getText().toString();

        thesisService.saveNewThesis(thesis, requirements, filesList, isSuccessful -> {
            Toast.makeText(this, isSuccessful, Toast.LENGTH_SHORT).show();
        });
    }
}