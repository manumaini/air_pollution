package com.example.air_pollution;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class sensor_configurationFragment extends Fragment {

    private EditText sensor_id;
    private EditText sensor_name;
    private EditText alert;
    private EditText threshold_value;
    private EditText alert_frequency;
    private EditText sensor_desc;
    private Button submit;
    private String url = "http://18.225.10.79:3010/dashboard_apis_route//dashboard_apis_upd";
    private String TAG = "sensor_config";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_configuration, container, false);

        sensor_id = view.findViewById(R.id.sensor_id);
        sensor_name = view.findViewById(R.id.sensor_name);
        alert = view.findViewById(R.id.alert);
        threshold_value = view.findViewById(R.id.Threshold_value);
        alert_frequency = view.findViewById(R.id.alert_frequency);
        sensor_desc = view.findViewById(R.id.sensor_description);
        submit = view.findViewById(R.id.submit_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: hrre in response");

                if (sensor_id.getText().toString().trim().length() > 0 && sensor_name.getText().toString().trim().length() > 0
                        && alert.getText().toString().trim().length() > 0
                        && threshold_value.getText().toString().trim().length() >0
                        && alert_frequency.getText().toString().trim().length() >0
                        && sensor_desc.getText().toString().trim().length() >0
                ){
                    Log.d(TAG, "onClick: hrre in response2");
                    JSONObject params = new JSONObject();
                    try {
                        params.put("sensor_id", sensor_id.getText().toString());
                        params.put("sensor_name", sensor_desc.getText().toString());
                        params.put("alert_y_n", alert.getText().toString());
                        params.put("threshold_value", threshold_value.getText().toString());
                        params.put("alert_frequency", alert_frequency.getText().toString());
                        params.put("sensor_desc", sensor_desc.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());


                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "onResponse: here in response 3");
                            Toast.makeText(getActivity(), "configuration successfully", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "onErrorResponse: here in response 4"+error);
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
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

                }else{
                    Toast.makeText(getActivity(),"all field are mandatory",Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }
}
