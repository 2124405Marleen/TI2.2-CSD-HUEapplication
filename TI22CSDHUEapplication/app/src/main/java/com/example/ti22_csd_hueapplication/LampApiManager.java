package com.example.ti22_csd_hueapplication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LampApiManager {
    //TODO: De emulator moet aan staan om lampen te ontvangen
    //TODO: Eigen IP invullen

    private RequestQueue requestQueue;
    private String url = "http://145.49.45.174:80/api/cc553c2fc782c01e25e2b8c03729a80";
    private LampListener lampListener;
    Context context;    //De betreffende activity

    public LampApiManager(Context context, LampListener lampListener){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);
        this.lampListener = lampListener;
    }

    public void getLamps(){

        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LAMP_REQ", response.toString());

                        //TODO: fix thread interrupted foutmelding
                        try {
                            JSONObject lights = response.getJSONObject("lights");
                            for (Iterator<String> it = lights.keys(); it.hasNext(); ) {
                                String key = it.next();
                                JSONObject lamp = response.getJSONObject(key);
                                Log.d("LAMP_REQ", lamp.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Als de Emulator zegt: "Linking has expired" klik op de knop links onderin de emulator
                            //Als er een TimeoutError is, ligt dat aan de firewall. De applicatie toestaan in firewall of netwerkstatus op prive
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
}
