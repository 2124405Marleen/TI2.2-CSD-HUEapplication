package com.example.ti22_csd_hueapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LampListener{

    private LampApiManager lampApiManager;
    private ArrayList<Lamp> lamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lampApiManager = new LampApiManager(getApplicationContext(), this);
        this.lamps = lampApiManager.getLamps();


    }

    @Override
    public void onLampAvailable(Lamp lamp) {

    }

    @Override
    public void onLampError(String error) {

    }
}
