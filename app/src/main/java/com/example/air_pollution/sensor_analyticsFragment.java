package com.example.air_pollution;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class sensor_analyticsFragment extends Fragment {

    private LineChart lineChart;
    private Typeface typeface = Typeface.DEFAULT;
    private Spinner spinner;
    private String url;

    private ArrayList<String> sensor;
    private Socket socket;
    private ArrayList<Entry> values = new ArrayList<>();
    private String TAG = "sensor_analyticsFragment";
    private int count = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_analytics, container, false);
        spinner = view.findViewById(R.id.sensor_select2);
        sensor = new ArrayList<>();
        SharedPreferences sh = this.getActivity().getSharedPreferences("login_data", MODE_PRIVATE);
        url = "http://192.168.49.209:3010/dashboard_apis_route/dashboard_apis_get_sensor_list/" + sh.getString("user_email", "empty");

        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray content = response.getJSONArray("content");
                    for (int i = 0; i < content.length(); i++) {
                        JSONObject object = content.getJSONObject(i);
                        sensor.add(object.getString("sensor_id"));

                    }

                    // Selection of the spinner
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sensor);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    spinner.setAdapter(spinnerArrayAdapter);


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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sensor_id = spinner.getSelectedItem().toString().trim();
                Log.d(TAG, "onItemSelected: " + sensor_id);

                //setting query for socket
                try {
                    IO.Options opts = new IO.Options();
                    opts.forceNew = true;
                    opts.query = "sensor_id=" + sensor_id;
                    opts.transports = new String[]{Polling.NAME};
                    opts.transports = new String[]{WebSocket.NAME};
                    socket = IO.socket("http://192.168.49.209:3006", opts);

                } catch (URISyntaxException e) {
                    Log.d(TAG, "onItemSelected: error in connection" + e.getMessage());

                }

                //socket events
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {

                    }

                }).on("dataUpdate", new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            values.add(new Entry(count, data.getInt("temp_val")));
                            Log.d(TAG, "call: " + data.getLong("time_val") + "  " + data.getInt("temp_val"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        count++;
                        setData();

                    }

                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                    }

                });
                socket.connect();


                if (socket.connected()) {
                    Toast.makeText(getActivity(), "socket connected successfully!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "socket not connected", Toast.LENGTH_LONG).show();
                }




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        lineChart = view.findViewById(R.id.historic_chart);

        lineChart.setViewPortOffsets(0, 0, 0, 0);
        lineChart.setBackgroundColor(getResources().getColor(R.color.black));

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        lineChart.setDrawGridBackground(false);
        lineChart.setMaxHighlightDistance(300);
        XAxis x = lineChart.getXAxis();
        x.setEnabled(false);

        YAxis y = lineChart.getAxisLeft();
        y.setTypeface(typeface);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getLegend().setEnabled(false);

        lineChart.animateXY(2000, 2000);

        lineChart.invalidate();

        return view;
    }

    private void setData() {

        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(getResources().getColor(R.color.colorPrimary));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(getResources().getColor(R.color.colorPrimary));
            set1.setFillColor(getResources().getColor(R.color.colorPrimary));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTypeface(typeface);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            lineChart.setData(data);

            lineChart.setVisibleXRangeMaximum(20);
            lineChart.moveViewToX(0);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        socket.disconnect();
    }
}
