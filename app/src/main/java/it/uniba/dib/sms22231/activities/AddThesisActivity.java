package it.uniba.dib.sms22231.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.ArrayList;

import it.uniba.dib.sms22231.R;

public class AddThesisActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> resultLauncher;
    private ListView listView;
    private ArrayList<String> fileName;
    private ArrayAdapter<String> listadapter;


     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thesis);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.addThesis);
        actionBar.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.fileListView);
        fileName = new ArrayList<String>();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null){
                    Uri uri = data.getData();
                    String name = getNameFromUri (uri);
                    fileName.add(name);
                    fillList();
                }
            }
        });
    }

    private void fillList() {
         listadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileName);
         listView.setAdapter(listadapter);
    }

    private String getNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null,null,null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(nameIndex);
    }

    public void addFile (View view){
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

}