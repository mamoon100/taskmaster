package com.example.taskmaster;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessageCreator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AddTask extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private TaskViewModel taskViewModel;
    AutoCompleteTextView menuView;
    InputStream inputStream;
    String fileName;
    Location location;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        if (inputStream == null) Log.i("MyAmplifyApp", "Ok");
                        try {
                            inputStream = getContentResolver().openInputStream(result.getData().getData());
                            File file = new File(result.getData().getData().toString());
                            fileName = file.getName();
                            Log.i("MyAmplifyApp", fileName);
                            Button attachFile = findViewById(R.id.uploadFileButton);
                            attachFile.setText("Choose Another File");
                            attachFile.setTextSize(10);
                            Toast.makeText(getApplicationContext(), "Added the file Successfully", Toast.LENGTH_LONG).show();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
    );


    @SuppressLint("SetTextI18n")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        try {
            // Add these lines to add the AWSApiPlugin plugins
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("tasks", MODE_PRIVATE);
        TextView textView = findViewById(R.id.totalTask);
        textView.setText("Total Task: " + getIntent().getIntExtra("count", 0));
//        List<String> teams = new ArrayList<>();
        Set<String> teamNames = sharedPreferences.getStringSet("teamNames", new HashSet<>());
        menuView = findViewById(R.id.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<>(teamNames));
        menuView.setAdapter(adapter);
        menuView.setInputType(InputType.TYPE_NULL);
        String defaultTeamName = sharedPreferences.getString("team", "Select Your Team First from setting");
        menuView.setText(defaultTeamName, defaultTeamName.equals("Select Your Team First from setting"));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Uri data = (Uri) bundle.get(Intent.EXTRA_STREAM);

        if (intent.getType() != null) {
//            System.out.println(data);
            try {
                inputStream = getContentResolver().openInputStream(data);
                File file = new File(data.toString());
                fileName = "image -" + file.getName();
                Log.i("MyAmplifyApp", fileName);
                Button attachFile = findViewById(R.id.uploadFileButton);
                attachFile.setText("Choose Another File");
                attachFile.setTextSize(10);
                Toast.makeText(getApplicationContext(), "Added the file Successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        this.location = location;
                        Toast.makeText(this, "Location was added", Toast.LENGTH_LONG).show();
                    }
                });

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

    public void uploadFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        setResult(RESULT_OK, intent);
        activityResultLauncher.launch(intent);
    }


    @SuppressLint("SetTextI18n")
    public void addTask(View view) {
        TextInputEditText title = findViewById(R.id.title);
        TextInputEditText desc = findViewById(R.id.desc);
        TextView textView = findViewById(R.id.totalTask);
        String[] arrayOfText = textView.getText().toString().split(":");
        String firstHalf = arrayOfText[0];
        String textNumber = arrayOfText[1].split(" ")[1];
        int number = Integer.parseInt(textNumber) + 1;
        textView.setText(firstHalf + ": " + number);
//        Intent intent = new Intent();
//        intent.putExtra("title",Objects.requireNonNull(title.getText()).toString());
//        intent.putExtra("desc",Objects.requireNonNull(desc.getText()).toString());
        if (fileName != null) {
            Amplify.Storage.uploadInputStream(
                    fileName,
                    inputStream,
                    results -> Log.i("MyAmplifyApp", "Successfully uploaded: " + results.getKey()),
                    storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
            );
        }
        Task task = Task.builder().teamName(menuView.getText().toString()).title(Objects.requireNonNull(title.getText()).toString()).desc(Objects.requireNonNull(desc.getText()).toString()).state("new").file(fileName).location(this.location.getLatitude()+","+this.location.getLongitude()).build();
        Amplify.API.mutate(
                ModelMutation.create(task),
                response -> {


                    Log.i("MyAmplifyApp", "Added Todo with id: " + response.getData().getId());
                },
                error -> Log.e("MyAmplifyApp", "Create failed", error));
//        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), "submitted!!", Toast.LENGTH_LONG).show();
        finish();
    }
}