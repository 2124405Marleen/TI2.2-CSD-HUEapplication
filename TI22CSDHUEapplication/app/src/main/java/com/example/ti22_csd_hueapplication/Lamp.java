package com.example.ti22_csd_hueapplication;

class Lamp {

    private int id;
    private boolean on;
    private int hue, sat, bri;
    private boolean reachable;
    private String colormode; //null for light without hue options
    private String effect;
    private String type;
    private String name;

    public Lamp(int id, boolean on, int hue, int saturation, int brightness, boolean reachable, String colormode, String effect, String type, String name) {
        this.id = id;
        this.on = on;
        this.hue = hue;
        sat = saturation;
        bri = brightness;
        this.reachable = reachable;
        this.colormode = colormode;
        this.effect = effect;
        this.type = type;
        this.name = name;
    }

    public Lamp(String name) {
        // Contructor met alleen naam voor het testen
        this.name = name;
        System.out.println("Lamp has been add");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getBri() {
        return bri;
    }

    public void setBri(int bri) {
        this.bri = bri;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public String getColormode() {
        return colormode;
    }

    public void setColormode(String colormode) {
        this.colormode = colormode;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Lamp{" +
                "id=" + id +
                ", on=" + on +
                ", hue=" + hue +
                ", sat=" + sat +
                ", bri=" + bri +
                ", reachable=" + reachable +
                ", colormode='" + colormode + '\'' +
                ", effect='" + effect + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
