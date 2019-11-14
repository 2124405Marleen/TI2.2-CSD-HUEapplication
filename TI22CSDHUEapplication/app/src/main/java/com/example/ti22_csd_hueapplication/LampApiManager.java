package com.example.ti22_csd_hueapplication;

import android.content.Context;
import android.util.Log;

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
    private RequestQueue requestQueue;
    final String url = "http://192.168.2.17/api/newdeveloper";
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
//                        try {
//                            JSONObject lights = response.getJSONObject("lights");
//                            for (Iterator<String> it = lights.keys(); it.hasNext(); ) {
//                                String key = it.next();
//                                JSONObject lamp = response.getJSONObject(key);
//                                Log.d("LAMP_REQ", lamp.toString());
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LAMP", error.toString());

            }
        });
        this.requestQueue.add(request);
        this.requestQueue.start();
    }
}
