package com.example.ti22_csd_hueapplication;

class Lamp {

    private boolean on;
    private int brightness;
    private int hue;
    private int saturation;
    private boolean rechable;
    private String colormode; //null for light without hue options
    private String type;
    private String name;

    public Lamp(boolean on, int brightness, int hue, int saturation, boolean rechable, String colormode, String type, String name) {
        this.on = on;
        this.brightness = brightness;
        this.hue = hue;
        this.saturation = saturation;
        this.rechable = rechable;
        this.colormode = colormode;
        this.type = type;
        this.name = name;
    }

    public Lamp(String name){
        // Contructor met alleen naam voor het testen
        this.name = name;
        System.out.println("Lamp has been add");
    }

    public boolean isOn() {
        return on;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getHue() {
        return hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public boolean isRechable() {
        return rechable;
    }

    public String getColormode() {
        return colormode;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
