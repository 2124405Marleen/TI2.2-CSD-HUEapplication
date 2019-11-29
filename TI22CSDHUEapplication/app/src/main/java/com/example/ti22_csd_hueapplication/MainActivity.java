package com.example.ti22_csd_hueapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.preference.PreferenceManager;
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
    //    private View currentLampView;
    private ImageView currentLampImageView;
    private Switch switchOnOff;
    private Switch switchDisco;
    private EditText lampName;
    private Button buttonNameConfirm;
    private int currentPosition;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ImageView imageViewColor;
    private SwipeRefreshLayout swipeContainer;
    private Thread thread;
    Handler handler;
    Runnable runnableCode;
    int hueInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lamps = new ArrayList<>();

        //initialize all
        initPreferences();
        initLampApiManager();
        initRecycleView();
        initPopupView();
        initHandler();

//        logdAllPreferences();

    }

    private void initHandler(){
        handler = new Handler();
         runnableCode = new Runnable() {

            @Override
            public void run() {
                //System.out.println("Disco!");
                currentPopupLamp.setSat(254);
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
    private void initPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        //s = ip s1 = username
        editor.putString("192.168.1.179:80", "OTb8qpzAqKSnMYj8oWM8-nFQd2ZhgfMEEt4GilJ4");
        editor.putString("145.48.205.33", "iYrmsQq1wu5FxF9CPqpJCnm1GpPVylKBWDUsNDhB");
        editor.apply();
    }

    private void logdAllPreferences(){
        Map<String, ?> keyValues = preferences.getAll();
        for (Map.Entry<String, ?> kv : keyValues.entrySet()) {
            Log.d("___KEYS", "ip: " + kv.getKey() + " | username: " + kv.getValue().toString());
        }
    }

    public void showNotification(String message){
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void initLampApiManager(){
        Map.Entry<String, ?> kv = preferences.getAll().entrySet().iterator().next();
        Log.d("___KEY_First", "ip: " + kv.getKey() + " | username: " + kv.getValue().toString());

        LAM = new LampApiManager(kv.getValue().toString(), kv.getKey(), getApplicationContext(), this);
        LAM.getBridge();
        LAM.getLamps();
        showNotification("Getting Lamps");
    }

    private void initRecycleView() {
        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvLamps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, lamps);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
        popupHeight -= popupHeight * 0.3;

        imageViewColor = popupView.findViewById(R.id.POImageViewColor);
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
                        showNotification("Name is equal to current name" + currentPopupLamp.getName() + " - " + newName);

                    } else {
                        buttonNameConfirm.setBackgroundColor(Color.GREEN);
                        showNotification("Name is changed");
                        
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
                    adapter.notifyItemChanged(currentPosition);
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
                int colorHSB = HSBC.getRGBFromHSB(seekBar.getProgress(), currentPopupLamp.getSat(), currentPopupLamp.getBri());
                imageViewColor.setColorFilter(colorHSB);

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
                int colorHSB = HSBC.getRGBFromHSB(currentPopupLamp.getHue(), seekBar.getProgress(), currentPopupLamp.getBri());
                imageViewColor.setColorFilter(colorHSB);
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
                int colorHSB = HSBC.getRGBFromHSB(currentPopupLamp.getHue(), currentPopupLamp.getSat(), seekBar.getProgress());
                imageViewColor.setColorFilter(colorHSB);
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

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lamps.clear();
                adapter.notifyDataSetChanged();
                LAM.getLamps();
            }
        });
    }

    @Override
    public void onLampAvailable(Lamp lamp) {
        lamps.add(lamp);
        Log.d("onLightAvailable", "Added" + lamp.toString());
        adapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onLampError(Error error) {
        Log.d("onLampError", error.toString());
    }

    @Override
    public void onBridgeAvailable(String IPAdress, String Username) {
        editor.putString(IPAdress, Username);
        editor.apply();
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
        int colorHSB = HSBC.getRGBFromHSB(CL.getHue(), CL.getSat(), CL.getBri());
        imageViewColor.setColorFilter(colorHSB);
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
