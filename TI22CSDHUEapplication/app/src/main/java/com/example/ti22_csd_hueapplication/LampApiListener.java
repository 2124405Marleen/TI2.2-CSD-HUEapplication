package com.example.ti22_csd_hueapplication;

public interface LampApiListener {

    void onLampAvailable(Lamp lamp);
    void onLampError(Error error);
}
