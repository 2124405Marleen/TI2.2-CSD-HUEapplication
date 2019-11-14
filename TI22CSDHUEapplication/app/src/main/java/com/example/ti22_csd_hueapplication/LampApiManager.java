package com.example.ti22_csd_hueapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class LampApiManager {
//TODO: De emulator moet aan staan om lampen te ontvangen
    private RequestQueue requestQueue;
    final String url = "http://localhost/api/newdeveloper";
    private LampListener lampListener;
    Context context;    //De betreffende activity

    public LampApiManager(Context context, LampListener lampListener){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);
        this.lampListener = lampListener;
    }

    public void getLamps(){
        final JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("LAMP_REQ", response.toString());
                        //TODO: De JSON uitlezen en lamp-objecten maken
//                        try{
//                            for (int idx = 0; idx < response.length(); idx++){
//                                boolean on = response.getJSONObject(idx).getJSONObject("state").getBoolean("on");
//                                String name = response.getJSONObject(idx).getString("name");
//                                Lamp lamp = new Lamp(name);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        this.requestQueue.add(request);
    }
}
