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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms22231.R;
import it.uniba.dib.sms22231.config.ChangeTypes;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Change;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.service.UserService;

public class AddThesisActivity extends AppCompatActivity {
    private final ThesisService thesisService = ThesisService.getInstance();

    private ActivityResultLauncher<Intent> resultLauncher;
    private EditText thesisTitle;
    private EditText thesisDescription;
    private ListView listViewFile;
    private ListView listViewReq;
    private ArrayList<String> fileNames;
    private ArrayList<Requirement> currentRequirements;
    private ArrayAdapter<String> listadapter;
    private ArrayList<Uri> filesList;
    private ArrayList<String> reqString;

    private final UserService userService = UserService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private Thesis currentThesis;
    private List<Attachment> currentAttachments;
    private final List<Change<Requirement>> changedRequirements = new ArrayList<>();
    private final List<Change<Attachment>> changedAttachments = new ArrayList<>();

    private Boolean isEditing = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thesis);

        Intent intent = getIntent();
        int caller = intent.getIntExtra("caller", 0);

        Button saveModifyButton = findViewById(R.id.saveThesisButton);

        ActionBar actionBar = getSupportActionBar();
        if (caller == 3) {
            actionBar.setTitle(R.string.modify);
            saveModifyButton.setText(R.string.modify);
            saveModifyButton.setOnClickListener(this::onModify);
        } else {
            actionBar.setTitle(R.string.addThesis);
            saveModifyButton.setText(R.string.saveThesis);
            saveModifyButton.setOnClickListener(this::onSave);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        thesisTitle = findViewById(R.id.titleEditText);
        thesisDescription = findViewById(R.id.descriptionEditText);
        listViewFile = findViewById(R.id.fileListView);
        fileNames = new ArrayList<>();
        listViewReq = findViewById(R.id.reqListView);
        currentRequirements = new ArrayList<>();
        filesList = new ArrayList<>();
        reqString = new ArrayList<>();

        if (caller == 3){
            isEditing = true;
            String id = intent.getStringExtra("id");
            thesisService.getThesisById(id, thesis -> {
                currentThesis = thesis;
                thesisTitle.setText(thesis.title);
                thesisDescription.setText(thesis.description);

                requirementService.getRequirementsByThesis(thesis).subscribe(requirements -> {
                    currentRequirements = (ArrayList<Requirement>) requirements;
                    reqString = new ArrayList<>();
                    for (Requirement requirement : requirements) {
                        String reqtemp = requirement.description + ": " + requirement.value;
                        reqString.add(reqtemp);
                    }

                    fillList(reqString, listViewReq);
                });
                attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                    currentAttachments = attachments;
                    fileNames = new ArrayList<>();
                    for (Attachment attachment : attachments) {
                        fileNames.add(attachment.fileName);
                    }

                    fillList(fileNames, listViewFile);
                });
            });
        }
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    String name = getNameFromUri(uri);
                    fileNames.add(name);
                    filesList.add(uri);
                    if (isEditing) {
                        Attachment attachment = new Attachment();
                        attachment.path = uri;
                        attachment.fileName = name;
                        changedAttachments.add(new Change<>(attachment, ChangeTypes.added));
                    }
                    fillList(fileNames, listViewFile);
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
                        if (isEditing) {
                            changedAttachments.add(new Change<>(currentAttachments.get(i), ChangeTypes.removed));
                        } else {
                            filesList.remove(i);
                        }
                        fileNames.remove(i);
                        fillList(fileNames, listViewFile);
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
                        if (isEditing) {
                            changedRequirements.add(new Change<>(currentRequirements.get(i), ChangeTypes.removed));
                        }

                        reqString.remove(i);
                        currentRequirements.remove(i);
                        fillList(reqString, listViewReq);
                        dialog.dismiss();
                    });
                    return false;
                });
                popupMenu.show();
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

    @Override
    public void onBackPressed() {
        if (!isChanged()) {
            super.onBackPressed();
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(AddThesisActivity.this)
                .setMessage(R.string.sureGoBack)
                .setPositiveButton(R.string.yes, null)
                .setNegativeButton("No", null)
                .show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view1 -> {
            dialog.dismiss();
            super.onBackPressed();
        });
    }

    private boolean isChanged() {
        return isEditing && (
                changedAttachments.size() > 0 ||
                changedRequirements.size() > 0 ||
                !currentThesis.title.equals(thesisTitle.getText().toString()) ||
                !currentThesis.description.equals(thesisDescription.getText().toString())
        ) ||
                !isEditing && (
                fileNames.size() > 0 ||
                reqString.size() > 0 ||
                thesisTitle.getText().toString().length() > 0 ||
                thesisDescription.getText().toString().length() > 0
        );
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
                if (isEditing) {
                    changedRequirements.add(new Change<>(req, ChangeTypes.added));
                }
                currentRequirements.add(req);
                reqString.add(item);
                fillList(reqString, listViewReq);
                builder.dismiss();
            }
        });
    }

    public void onSave(View view) {
        Thesis thesis = new Thesis();
        User user = userService.getUserData();
        String userId = user.uid;

        thesis.teacherId = userId;
        thesis.title = thesisTitle.getText().toString();
        thesis.description = thesisDescription.getText().toString();

        AtomicReference<Boolean> success = new AtomicReference<>(false);

        thesisService.saveNewThesis(thesis, currentRequirements, filesList, fileNames, isSuccessful -> {
            if (success.get()) {
                finish();
            }

            success.set(isSuccessful);
        });
    }

    public void onModify(View view) {
        currentThesis.title = thesisTitle.getText().toString();
        currentThesis.description = thesisDescription.getText().toString();

        thesisService.updateThesis(currentThesis, changedAttachments, changedRequirements, success -> {
            if (success) {
                finish();
            }
        });
    }

}