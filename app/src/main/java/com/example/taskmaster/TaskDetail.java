package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TaskDetail extends AppCompatActivity {
    GoogleMap googleMap;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("tasks", Context.MODE_PRIVATE);
        TextView textViewTitle = findViewById(R.id.titleDetail);
        TextView textViewDesc = findViewById(R.id.descDetail);
        TextView locationView = findViewById(R.id.location);
        textViewTitle.setText(sharedPreferences.getString("title", "No title"));
        textViewDesc.setText(sharedPreferences.getString("desc", "No Desc"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String file = sharedPreferences.getString("file", null);
        String location = sharedPreferences.getString("location", null);
        ImageView img = findViewById(R.id.imgTask);
        Amplify.Storage.getUrl(
                file,
                result -> {
                    try {
//                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(result.getUrl().toString()).getContent());
                        if (result.getUrl().toString().contains("image")) {
                            InputStream inputStream = (InputStream) new URL(result.getUrl().toString()).getContent();
                            Drawable drawable = Drawable.createFromStream(inputStream, "src");
                            textViewTitle.setText("AAAA");
                            System.out.println("=================" + result.getUrl());
                            Log.i("MyAmplifyApp", result.getUrl().toString());
                            img.setImageDrawable(drawable);
                        } else {
                            img.setVisibility(View.GONE);
                            TextView textUrl = findViewById(R.id.urlText);
                            textUrl.setVisibility(View.VISIBLE);
                            textUrl.setText("Download Your File");
                            textUrl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri url = Uri.parse(result.getUrl().toString());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                },
                error -> Log.e("MyAmplifyApp", "URL generation failure", error)
        );
        if (location != null) {
//            double lon = Double.parseDouble(location.split(",")[0]);
//            double lat = Double.parseDouble(location.split(",")[1]);
            locationView.setText(location);
//            LatLng longLat = new LatLng(lon, lat);
//            Marker marker = googleMap.addMarker(new MarkerOptions().position(longLat).title("Home").snippet("Task"));
        }



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