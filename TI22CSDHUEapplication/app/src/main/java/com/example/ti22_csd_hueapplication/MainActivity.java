package com.example.ti22_csd_hueapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LampListener{

    private LampApiManager lampApiManager;
    private ArrayList<Lamp> lamps;
    private RecyclerView recyclerView;
    private LampAdapter lampAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lampApiManager = new LampApiManager(getApplicationContext(), this);
        lamps = lampApiManager.getLamps();
        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(
                this,
                1,
                GridLayoutManager.VERTICAL,
                false
        ));
        lampAdapter = new LampAdapter(this, lamps);
        recyclerView.setAdapter(lampAdapter);

    }

    @Override
    public void onLampAvailable(Lamp lamp) {

    }

    @Override
    public void onLampError(String error) {

    }
}
