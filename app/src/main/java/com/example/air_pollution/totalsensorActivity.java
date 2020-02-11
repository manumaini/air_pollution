package com.example.air_pollution;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class totalsensorActivity extends AppCompatActivity {

    private RecyclerView rView;
    private ArrayList<String> sensor_names;
    private ArrayList<String> sensor_id;
    private ArrayList<String>  sensor_threshold;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totalsensor);

        rView = findViewById(R.id.recycler_view);
        sensor_id = new ArrayList<>();
        sensor_names = new ArrayList<>();
        sensor_threshold = new ArrayList<>();

        SharedPreferences sh = getSharedPreferences("login_data",MODE_PRIVATE);

        url = "http://18.225.10.79:3010/dashboard_apis_route/dashboard_apis_get_sensor_list/"+sh.getString("user_email","empty");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(layoutManager);

        final recyclerView adapter = new recyclerView(sensor_names,sensor_id,sensor_threshold);
        rView.setAdapter(adapter);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray content = response.getJSONArray("content");
                    for(int i=0 ; i< content.length();i++){
                        JSONObject object = content.getJSONObject(i);
                        sensor_id.add(object.getString("sensor_id"));
                        sensor_names.add(object.getString("sensor_name"));
                        sensor_threshold.add(object.getString("sensor_threshold"));

                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        requestQueue.add(jsonObjectRequest);



    }
}
