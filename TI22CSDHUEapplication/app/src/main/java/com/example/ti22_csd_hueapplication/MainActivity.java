package com.example.ti22_csd_hueapplication;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LampApiListener, RecyclerViewAdapter.ItemClickListener {

    private LampApiManager LAM;
    private ArrayList<Lamp> lamps;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private DisplayMetrics displayMetrics;
    private LayoutInflater inflater;
    private View popupView;
    private int popupWidth, popupHeight;
    private SeekBar hue, sat, bri;
    private TextView lampNo, lampOnOff;
    private Lamp currentPopupLamp;
    private Switch switchOnOff;
    private Switch switchDisco;
    private EditText lampName;
    private Button buttonNameConfirm;
    private int currentPosition;
    private Thread thread;
    Handler handler;
    Runnable runnableCode;
    int hueInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lamps = new ArrayList<>();

        LAM = new LampApiManager("OTb8qpzAqKSnMYj8oWM8-nFQd2ZhgfMEEt4GilJ4", "192.168.1.179:80", getApplicationContext(), this);
        LAM.getLamps();
        LAM.getIPAddress();

        Toast.makeText(getBaseContext(), "Getting lamps", Toast.LENGTH_SHORT).show();

        initRecycleView();
        initPopupView();
        initHandler();

    }

    private void initHandler(){
        handler = new Handler();
         runnableCode = new Runnable() {

            @Override
            public void run() {
                //System.out.println("Disco!");
                hueInt += 4000;
                currentPopupLamp.setHue(hueInt);
                LAM.setLamp(currentPopupLamp);
                handler.postDelayed(this, 600);
                if (hueInt > 64000){
                    hueInt = 0;
                }

            }
        };
    }
    private void initRecycleView() {
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvLamps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, lamps);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPopupView() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // inflate the layout of the popup window
        inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.activity_detailed_lamp_popup, null);

        // create the popup window
        popupWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        popupHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        popupWidth -= popupWidth * 0.1;
        popupHeight -= popupHeight * 0.45;

        lampNo = popupView.findViewById(R.id.POLampNo);
        lampOnOff = popupView.findViewById(R.id.POTextViewAvailable);
        lampName = popupView.findViewById(R.id.POEditTextName);
        buttonNameConfirm = popupView.findViewById(R.id.POButtonConfirm);
        
        switchOnOff = popupView.findViewById(R.id.POSwitchOnOff);
        switchDisco = popupView.findViewById(R.id.POSwitchDisco);

        //set seekbars: hue, sat, bri
        hue = popupView.findViewById(R.id.POHue);
        sat = popupView.findViewById(R.id.POSaturation);
        bri = popupView.findViewById(R.id.POBrightness);

        initListeners();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListeners() {

        //button name confirm listener
        buttonNameConfirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String newName = lampName.getText().toString();
                boolean nameIsSame = true;

                if (currentPopupLamp.getName().equals(newName)) {

                } else {
                    currentPopupLamp.setName(newName);
                    LAM.setLampName(currentPopupLamp);
//                    adapter.notifyDataSetChanged();
                    nameIsSame = false;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (nameIsSame) {
                        buttonNameConfirm.setBackgroundColor(Color.RED);
                        Toast.makeText(getBaseContext(), "Name is equal to current name" + currentPopupLamp.getName() + " - " + newName, Toast.LENGTH_SHORT).show();

                    } else {
                        buttonNameConfirm.setBackgroundColor(Color.GREEN);
                        Toast.makeText(getBaseContext(), "Name is changed", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                } else {
                    buttonNameConfirm.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                }
                return false;
            }
        });

        //switch listener
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentPopupLamp.setOn(isChecked);
                lampOnOff.setText((isChecked ? "true" : "false"));
//                adapter.notifyDataSetChanged();
                adapter.notifyItemChanged(currentPosition);
                LAM.setLampOnOff(currentPopupLamp);
            }
        });

        switchDisco.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                currentPopupLamp.setSat(0);
                currentPopupLamp.setBri(254);
                adapter.notifyItemChanged(currentPosition);

                if (isChecked) {
                    handler.post(runnableCode);
                    System.out.println("Disco started");
                } else{
                    handler.removeCallbacks(runnableCode);
                    System.out.println("Disco stopped");
                }
            }
        });

        //set seekbars: hue, sat, bri
        hue = popupView.findViewById(R.id.POHue);
        sat = popupView.findViewById(R.id.POSaturation);
        bri = popupView.findViewById(R.id.POBrightness);

        //set seekbars listeners
        hue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                int colorHue = HSBC.getRGBFromHSB(seekBar.getProgress(), currentPopupLamp.getSat(), currentPopupLamp.getBri());
//                currentLampImageView.setColorFilter(colorHue);
                currentPopupLamp.setHue(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);

//                Log.d("_____HUE Item Position", "Position: " + currentPosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Log.d("onStopTrackingTouch", "hue value: " + seekBar.getProgress());
                currentPopupLamp.setHue(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);
                LAM.setLamp(currentPopupLamp);
            }
        });

        sat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentPopupLamp.setSat(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPopupLamp.setSat(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);
                LAM.setLamp(currentPopupLamp);
            }
        });

        bri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentPopupLamp.setBri(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPopupLamp.setBri(seekBar.getProgress());
                adapter.notifyItemChanged(currentPosition);
                LAM.setLamp(currentPopupLamp);
            }
        });
    }

    @Override
    public void onLampAvailable(Lamp lamp) {
        lamps.add(lamp);
        Log.d("onLightAvailable", "Added" + lamp.toString());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLampError(Error error) {
        Log.d("onLampError", error.toString());
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Log.d("________onItemClick", "onItemClick: " + position);

        //get clicked Light
        Lamp CL = lamps.get(position);
        currentPopupLamp = lamps.get(position);
        currentPosition = position;

        //setting current light values
        lampNo.setText(String.valueOf(CL.getId()));
        lampOnOff.setText((CL.isOn() ? "true" : "false"));
        switchOnOff.setChecked(CL.isOn());
        lampName.setText(CL.getName(), TextView.BufferType.EDITABLE);
        hue.setProgress(CL.getHue());
        sat.setProgress(CL.getSat());
        bri.setProgress(CL.getBri());

        //popup showing
        final PopupWindow popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
