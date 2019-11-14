package com.example.ti22_csd_hueapplication;

public interface LampListener {

    void onLampAvailable(Lamp lamp);
    void onLampError(String error);
}
