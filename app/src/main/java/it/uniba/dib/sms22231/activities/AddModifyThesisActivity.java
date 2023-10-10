package it.uniba.dib.sms22231.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
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
import it.uniba.dib.sms22231.adapters.CustomListAdapter;
import it.uniba.dib.sms22231.config.ChangeTypes;
import it.uniba.dib.sms22231.config.RequirementTypes;
import it.uniba.dib.sms22231.model.Attachment;
import it.uniba.dib.sms22231.model.Change;
import it.uniba.dib.sms22231.model.CustomListData;
import it.uniba.dib.sms22231.model.Requirement;
import it.uniba.dib.sms22231.model.Thesis;
import it.uniba.dib.sms22231.model.User;
import it.uniba.dib.sms22231.service.AttachmentService;
import it.uniba.dib.sms22231.service.RequirementService;
import it.uniba.dib.sms22231.service.ThesisService;
import it.uniba.dib.sms22231.service.UserService;

public class AddModifyThesisActivity extends AppCompatActivity {
    private final ThesisService thesisService = ThesisService.getInstance();
    private final RequirementService requirementService = RequirementService.getInstance();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private final UserService userService = UserService.getInstance();
    private ActivityResultLauncher<Intent> resultLauncher;
    private EditText thesisTitle;
    private EditText thesisDescription;
    private ListView listViewFile;
    private ListView listViewReq;
    private ArrayList<String> fileNames;
    private ArrayList<Requirement> currentRequirements;
    private ArrayList<Uri> filesList;
    private List<Attachment> attachments = new ArrayList<>();
    private ArrayList<String> reqString;
    private Thesis currentThesis;
    private final List<Change<Requirement>> changedRequirements = new ArrayList<>();
    private final List<Change<Attachment>> changedAttachments = new ArrayList<>();
    private Boolean isEditing = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_thesis);

        Intent intent = getIntent();
        int caller = intent.getIntExtra("caller", 0);

        Button saveModifyButton = findViewById(R.id.saveThesisButton);

        ActionBar actionBar = getSupportActionBar();
        setSaveOrModify(caller, saveModifyButton, actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        thesisTitle = findViewById(R.id.titleEditText);
        thesisDescription = findViewById(R.id.descriptionEditText);
        listViewFile = findViewById(R.id.fileListView);
        fileNames = new ArrayList<>();
        listViewReq = findViewById(R.id.reqListView);
        currentRequirements = new ArrayList<>();
        filesList = new ArrayList<>();
        reqString = new ArrayList<>();

        fillActivityForModify(intent, caller);

        filePicker();

        deleteAttachment();
        deleteRequirement();
    }

    //eliminazione di un requisito: cliccando su di esso appare un popup col comando elimina
    //l'eliminazione avviene dopo aver dato conferma in una dialog
    private void deleteRequirement() {
        listViewReq.setOnItemClickListener((adapterView, view, i, l) -> {
            PopupMenu popupMenu = new PopupMenu(AddModifyThesisActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_item_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog dialog = new AlertDialog.Builder(AddModifyThesisActivity.this)
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
                    fillRequirementsList(reqString, listViewReq);
                    dialog.dismiss();
                });
                return false;
            });
            popupMenu.show();
        });
    }

    //eliminazione di un file allegato: cliccando su di esso appare un popup col comando elimina
    //l'eliminazione avviene dopo aver dato conferma in una dialog
    private void deleteAttachment() {
        listViewFile.setOnItemClickListener((adapterView, view, i, l) -> {
            PopupMenu popupMenu = new PopupMenu(AddModifyThesisActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_item_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog dialog = new AlertDialog.Builder(AddModifyThesisActivity.this)
                        .setMessage(R.string.suredelete)
                        .setPositiveButton("Ok", null)
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(view1 -> {
                    if (isEditing) {
                        changedAttachments.add(new Change<>(attachments.get(i), ChangeTypes.removed));
                    } else {
                        filesList.remove(i);
                    }
                    fileNames.remove(i);
                    attachments.remove(i);
                    fillAttachmentsList();
                    dialog.dismiss();
                });
                return false;
            });
            popupMenu.show();
        });
    }

    //aggiunta dei file alla tesi
    private void filePicker() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data != null) {
                Uri uri = data.getData();
                String name = getNameFromUri(uri);
                fileNames.add(name);
                filesList.add(uri);
                Attachment attachment = new Attachment();
                attachment.path = uri;
                attachment.setFileName(name);
                attachments.add(attachment);
                if (isEditing) {
                    changedAttachments.add(new Change<>(attachment, ChangeTypes.added));
                }
                fillAttachmentsList();
            }
        });
    }

    //se l'activity è aperta in modifica, i campi vengono riempiti con i dati della tesi
    private void fillActivityForModify(Intent intent, int caller) {
        if (caller == 3) {
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
                        String reqTemp = getResources().getStringArray(R.array.requirements)[requirement.description.ordinal()] + ": " + requirement.value;
                        reqString.add(reqTemp);
                    }
                    fillRequirementsList(reqString, listViewReq);
                });
                attachmentService.getAttachmentsByThesis(thesis).subscribe(attachments -> {
                    this.attachments = attachments;
                    fileNames = new ArrayList<>();
                    for (Attachment attachment : attachments) {
                        fileNames.add(attachment.getFileName());
                    }
                    fillAttachmentsList();
                });
            });
        }
    }

    //adattamento dell'activity in base all'apertura per salvataggio o modifica
    private void setSaveOrModify(int caller, Button saveModifyButton, ActionBar actionBar) {
        if (caller == 3) {
            actionBar.setTitle(R.string.modify);
            saveModifyButton.setText(R.string.modify);
            saveModifyButton.setOnClickListener(this::onModify);
        } else {
            actionBar.setTitle(R.string.addThesis);
            saveModifyButton.setText(R.string.saveThesis);
            saveModifyButton.setOnClickListener(this::onSave);
        }
    }

    //riempimento della Listview dei requisiti
    private void fillRequirementsList(ArrayList<String> arrayList, ListView listView) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listAdapter);
    }

    //riempimento della listview degli attachments con icone
    private void fillAttachmentsList() {
        ArrayList<CustomListData> attachmentsCustomListData = new ArrayList<>();
        attachments.forEach(attachment -> {
            CustomListData customListData = new CustomListData();
            customListData.setText(attachment.getFileName());
            switch (attachment.fileType) {
                case image:
                    customListData.setImageId(R.drawable.image);
                    break;
                case video:
                    customListData.setImageId(R.drawable.outline_video_file_24);
                    break;
                case archive:
                    customListData.setImageId(R.drawable.zip);
                    break;
                case generic:
                    customListData.setImageId(R.drawable.file);
                    break;
                case document:
                    customListData.setImageId(R.drawable.pdf);
            }
            attachmentsCustomListData.add(customListData);
        });
        CustomListAdapter customListAdapter = new CustomListAdapter(this, attachmentsCustomListData);
        listViewFile.setAdapter(customListAdapter);
        listViewFile.setVisibility(View.VISIBLE);
    }

    //ricava dall'uri il nome del file da mostrare poi nella lista
    private String getNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(nameIndex);
    }

    //richiesta dei permessi per accedere ai file in memoria
    public void addFilePermission(View view) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(AddModifyThesisActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(AddModifyThesisActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (
                ActivityCompat.checkSelfPermission(AddModifyThesisActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(AddModifyThesisActivity.this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(AddModifyThesisActivity.this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddModifyThesisActivity.this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
            }, 1);
        } else {
            addFile();
        }
    }

    //aggiunta e selezione dei tipi di file
    public void addFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        resultLauncher.launch(intent);
    }

    //controllo dei permessi per accedere alla memoria
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addFile();
        } else {
            Toast.makeText(getApplicationContext(), R.string.permission, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //se ci sono delle modifiche non salvate e si chiude l'activity, viene chiesta conferma della chiusura
    @Override
    public void onBackPressed() {
        if (!isChanged()) {
            super.onBackPressed();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(AddModifyThesisActivity.this)
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

    //controlla se ci sono modifiche da comunicare a onBackPressed()
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

    //aggiunta di requisiti alla tesi
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
                req = new Requirement(null, null, RequirementTypes.values()[position], control);
                item = getResources().getStringArray(R.array.requirements)[position] + ": " + control;

                if (isEditing) {
                    changedRequirements.add(new Change<>(req, ChangeTypes.added));
                }
                currentRequirements.add(req);
                reqString.add(item);
                fillRequirementsList(reqString, listViewReq);
                builder.dismiss();
            }
        });
    }

    //salvataggio della nuova tesi
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

    //salvataggio delle modifiche della tesi
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