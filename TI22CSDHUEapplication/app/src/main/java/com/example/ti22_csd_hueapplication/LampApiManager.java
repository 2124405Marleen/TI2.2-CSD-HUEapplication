package com.example.ti22_csd_hueapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class LampApiManager {
    //TODO: De emulator moet aan staan om lampen te ontvangen (dus ook op start drukken met poort 80)
    //TODO: Eigen IP invullen & via postman nieuwe gebruikersnaam aanvragen

    private RequestQueue requestQueue;
    private String username;
    private String address;
    //    private String url = "http://145.49.45.174/api/3a75efa57480163a815bc63f9209cef";
    private LampApiListener listener;
//    private Context context;    //De betreffende activity

    public LampApiManager(Context context, LampApiListener lampApiListener) {
//        username = "";
//        address = getIPAddress();
        this.requestQueue = Volley.newRequestQueue(context);
        listener = lampApiListener;
    }

    public LampApiManager(String username, String address, Context context, LampApiListener lampApiListener) {
        this.username = username;
        this.address = address;
//        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        listener = lampApiListener;
    }

    public void getBridge(){
        getIPAddress();
    }

    private void getIPAddress(){
        String GET_URL = "https://discovery.meethue.com/";

        JsonArrayRequest requestIPAddress = new JsonArrayRequest(
                Request.Method.GET,
                GET_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("!!!!!!!IPAddress_REQ", response.toString());

                        //TODO: fix thread interrupted foutmelding
                        try {
                            JSONObject AddressJO = response.getJSONObject(0);

                            String addressS = AddressJO.getString("internalipaddress");
                            getUsername(addressS);
//                            Log.d("______JSONNN ADDRESS", "address: " + address);

                        } catch (JSONException e) {
                            e.printStackTrace();
                             }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        this.requestQueue.add(requestIPAddress);

//        return address;
    }

    private void getUsername(final String IPAddress){
        String POST_URL = "http://" + IPAddress + "/api";

        JSONObject devicetypeJO = new JSONObject();

        try {

            devicetypeJO.put("devicetype", "MijnApp#LRS");

        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        CustomJsonArrayRequest requestIPAddress = new CustomJsonArrayRequest(
                Request.Method.POST,
                POST_URL,
                devicetypeJO,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("!!!!!!!username_REQ", response.toString());

                        try {
                            JSONObject User = response.getJSONObject(0);
                            JSONObject Succes = User.getJSONObject("success");

                            String usernameS = Succes.getString("username");

                            Log.d("______JSONNN USERNAME", "username: " + usernameS);
                            listener.onBridgeAvailable(IPAddress, usernameS);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        this.requestQueue.add(requestIPAddress);

    }

    public void getLamps() {
        String GET_URL = "http://" + address + "/api/" + username;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                GET_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("!!!!!!!LAMP_REQ", response.toString());

                        //TODO: fix thread interrupted foutmelding
                        try {
                            JSONObject lampsJO = response.getJSONObject("lights");

                            for (Iterator<String> it = lampsJO.keys(); it.hasNext(); ) {
                                String key = it.next();
                                JSONObject state = lampsJO.getJSONObject(key).getJSONObject("state");
                                Log.d("L00000000GGG", "onResponse: " + key);

                                Lamp lamp = new Lamp(
                                        Integer.parseInt(key),
                                        state.getBoolean("on"),
                                        state.getInt("hue"),
                                        state.getInt("sat"),
                                        state.getInt("bri"),
                                        state.getBoolean("reachable"),
                                        state.getString("colormode"),
                                        state.getString("effect"),
                                        lampsJO.getJSONObject(key).getString("type"),
                                        lampsJO.getJSONObject(key).getString("name")
                                );
                                Log.d("NEW_LIGHT", lamp.toString());
                                listener.onLampAvailable(lamp);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Als de Emulator zegt: "Linking has expired" klik op de knop links onderin de emulator
                            //Als er een TimeoutError is, ligt dat aan de firewall. De applicatie toestaan in firewall of netwerkstatus op prive OF wifi hotspot!
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LAMP", error.toString());

            }
        });
        this.requestQueue.add(request);
    }

    public void setLamp(Lamp lamp) {
        String PUT_URL_State = "http://" + address + "/api/" + username + "/lights/" + lamp.getId() + "/state/";
        Log.d("!!!!!!!!!", "RU: " + PUT_URL_State);

        JSONObject stateJO = new JSONObject();

        try {
            if (lamp.isOn()) {
                stateJO.put("on", lamp.isOn());
                stateJO.put("hue", lamp.getHue());
                stateJO.put("sat", lamp.getSat());
                stateJO.put("bri", lamp.getBri());
                stateJO.put("effect", lamp.getEffect());
            } else {
                stateJO.put("on", lamp.isOn());
            }

        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        Log.d("setLamp()", "PUT Body:" + stateJO.toString());

        CustomJsonArrayRequest requestState = new CustomJsonArrayRequest(
                Request.Method.PUT,
                PUT_URL_State,
                stateJO,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("setLamp() state", "onResponse: " + response.toString());
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("setLamp() state", "onErrorResponse: " + error.toString());
                    }
                }
        );

        this.requestQueue.add(requestState);
    }

    public void setLampName(Lamp lamp) {

        String PUT_URL_Lamp = "http://" + address + "/api/" + username + "/lights/" + lamp.getId();

        Log.d("!!!!!!!!!", "RU: " + PUT_URL_Lamp);

        JSONObject lampJO = new JSONObject();

        try {

            lampJO.put("name", lamp.getName());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        Log.d("setLamp()", "PUT Body:" + lampJO.toString());

        CustomJsonArrayRequest requestLamp = new CustomJsonArrayRequest(
                Request.Method.PUT,
                PUT_URL_Lamp,
                lampJO,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("setLamp() lamp", "onResponse: " + response.toString());
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("setLamp() lamp", "onErrorResponse: " + error.toString());
                    }
                }
        );

        this.requestQueue.add(requestLamp);
    }


    public void setLampOnOff(Lamp lamp) {
        String PUT_URL_StateOnOFF = "http://" + address + "/api/" + username + "/lights/" + lamp.getId() + "/state/";
        Log.d("!!!!!!!!!", "RU: " + PUT_URL_StateOnOFF);

        JSONObject stateJO = new JSONObject();

        try {
            stateJO.put("on", lamp.isOn());

        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        Log.d("setLamp()", "PUT Body:" + stateJO.toString());

        CustomJsonArrayRequest requestState = new CustomJsonArrayRequest(
                Request.Method.PUT,
                PUT_URL_StateOnOFF,
                stateJO,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("setLamp() state", "onResponse: " + response.toString());
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("setLamp() state", "onErrorResponse: " + error.toString());
//                        listener.onBitcoinTrackerError(new Error(error));
                    }
                }
        );

        this.requestQueue.add(requestState);
    }

}
