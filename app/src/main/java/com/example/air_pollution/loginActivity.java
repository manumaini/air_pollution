package com.example.air_pollution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class loginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private String url = "http://192.168.49.209:3000/users/authenticate";
    private ProgressBar progressBar;
    private String TAG = "loginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.spin_kit);


    }

    public void onClick_login(View view) {

        if (username.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
            username.setError("enter the user name");
            password.setError("enter the password");

        } else if (username.getText().toString().isEmpty()) {
            username.setError("enter the user name");

        } else if (password.getText().toString().isEmpty()) {
            password.setError("enter the password");

        } else {
            progressBar.setVisibility(View.VISIBLE);

            JSONObject postparams = new JSONObject();
            try {
                postparams.put("username", username.getText().toString());
                postparams.put("password", password.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);

            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);

                            SharedPreferences sharedPreferences = getSharedPreferences("login_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            try {
                                String token = response.getString("token");
                                editor.putString("token", token);
                                Log.d(TAG, "onResponse" + token);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject user = response.getJSONObject("user");
                                String user_id = user.getString("id");
                                String user_name = user.getString("name");
                                String user_email = user.getString("email");
                                Log.d(TAG, "onResponse: Details"+user_id+user_email+user_name);
                                editor.putString("user_id", user_id);
                                editor.putString("user_name", user_name);
                                editor.putString("user_email",user_email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            editor.apply();
                            editor.commit();


                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onErrorResponse: here3"+error);
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

}
