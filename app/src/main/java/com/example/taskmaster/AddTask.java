package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AddTask extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    AutoCompleteTextView menuView;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("tasks",MODE_PRIVATE);
        TextView textView = findViewById(R.id.totalTask);
        textView.setText("Total Task: " + getIntent().getIntExtra("count", 0));
//        List<String> teams = new ArrayList<>();
        Set<String> teamNames =  sharedPreferences.getStringSet("teamNames",new HashSet<>());
        menuView = findViewById(R.id.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<>(teamNames));
        menuView.setAdapter(adapter);
        menuView.setInputType(InputType.TYPE_NULL);
        String defaultTeamName = sharedPreferences.getString("team","Select Your Team First from setting");
        menuView.setText(defaultTeamName, defaultTeamName.equals("Select Your Team First from setting"));
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
        Task task = Task.builder().teamName(menuView.getText().toString()).title(Objects.requireNonNull(title.getText()).toString()).desc(Objects.requireNonNull(desc.getText()).toString()).state("new").build();
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