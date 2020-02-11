package com.example.air_pollution;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class dashboard_fragment extends Fragment {

    private TextView total_sensor;
    private TextView sensor_online;
    private TextView sensor_offline;
    private TextView alert;
    private String url;
    private String TAG = "dashboard";
    private RelativeLayout black_layout;
    private ProgressBar loading;
    private CardView ctotal_sensor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard,container,false);

        total_sensor = view.findViewById(R.id.total_sensor);
        sensor_offline = view.findViewById(R.id.sensor_offline);
        sensor_online = view.findViewById(R.id.sensor_online);
        alert = view.findViewById(R.id.alert);
        black_layout = view.findViewById(R.id.black_layout);
        loading = view.findViewById(R.id.spin_kit2);

        ctotal_sensor = view.findViewById(R.id.cardview_washfold);
        ctotal_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),totalsensorActivity.class);
                startActivity(intent);
            }
        });



        SharedPreferences sh = this.getActivity().getSharedPreferences("login_data",Context.MODE_PRIVATE);
        String user_email = sh.getString("user_email","empty");

        Log.d(TAG, "onCreateView: user_email "+user_email);

        url = "http://18.225.10.79:3010/dashboard_apis_route/dashboard_apis_get_sensor_meta/"+user_email;

        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: inresponse");
                //Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_LONG).show();

                try {
                    JSONObject content = response.getJSONObject("content");
                    total_sensor.setText(content.getString("total_sensors"));
                    sensor_online.setText(content.getString("sensors_online"));
                    sensor_offline.setText(content.getString("sensors_offline"));
                    alert.setText(content.getString("amc_expired"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                black_layout.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: in error"+error);
                loading.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"check your internet connection",Toast.LENGTH_LONG).show();
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
        return view;
    }
}
