package com.example.air_pollution;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class historic_dataFragment extends Fragment {

    private ImageView imageView1;
    private ImageView imageView2;
    private EditText from_date;
    private EditText to_date;
    private LineChart lineChart;
    private Typeface tfLight = Typeface.DEFAULT;
    private Button plot_graph;
    private String TAG = "historic_dataFragment";
    private Spinner sensor_list;
    private ArrayList<String> sensor;
    private String url;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private ArrayList<Entry> avg;
    private ArrayList<Entry> max;
    private ArrayList<Entry> min;
    private RequestQueue requestQueue;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historic_data_analysis, container, false);
        imageView1 = view.findViewById(R.id.image_c1);
        imageView2 = view.findViewById(R.id.image_c2);
        plot_graph = view.findViewById(R.id.plot_graph);
        from_date = view.findViewById(R.id.from_date);
        to_date = view.findViewById(R.id.to_date);
        lineChart = view.findViewById(R.id.historic_chart);
        sensor_list = view.findViewById(R.id.select_sensor);
        sensor = new ArrayList<>();
        avg = new ArrayList<>();
        max = new ArrayList<>();
        min = new ArrayList<>();


        SharedPreferences sh = this.getActivity().getSharedPreferences("login_data", MODE_PRIVATE);

        url = "http://192.168.49.209:3010/dashboard_apis_route/dashboard_apis_get_sensor_list/" + sh.getString("user_email", "empty");

        requestQueue = Volley.newRequestQueue(this.getActivity());

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
                    sensor_list.setAdapter(spinnerArrayAdapter);


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


        lineChart.setViewPortOffsets(0, 0, 0, 0);
        lineChart.setBackgroundColor(getResources().getColor(R.color.backgrey));

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
        y.setTypeface(tfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getLegend().setEnabled(false);

        lineChart.animateXY(2000, 2000);

        lineChart.invalidate();


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

                                from_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDoneText("done")
                        .setCancelText("cancel")
                        .setThemeDark();
                cdp.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);

            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                to_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDoneText("done")
                        .setCancelText("cancel")
                        .setThemeDark();
                cdp.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);

            }
        });

        plot_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                String start_date = from_date.getText().toString() + " 00:00:00";
                String end_date = to_date.getText().toString() + " 00:00:00";
                Date start = null;
                Date end = null;
                try {
                    start = df.parse(start_date);
                    end = df.parse(end_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long epoch1 = start.getTime();
                long epoch2 = end.getTime();

                Log.d(TAG, "onClick: time stamp" + epoch1 + "   " + epoch2);

                RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());

                String selected_sensor = sensor_list.getSelectedItem().toString();

                Log.d(TAG, "onClick: sensors " + selected_sensor);

                String url2 = "http://192.168.49.209:3011/data_lake_apis_route/data_lake_apis_get_custom_range/" + selected_sensor + "/" + epoch1 + "/" + epoch2;
                //String url2="http://18.225.10.79:3011/data_lake_apis_route/data_lake_apis_get_custom_range/cap06/1579545000000/1580409000000";
                JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray content = response.getJSONArray("content");
                            for (int i = 0; i < content.length(); i++) {
                                JSONObject object = content.getJSONObject(i);
                                avg.add(new Entry(i, Float.valueOf((float) object.getDouble("avg"))));
                                max.add(new Entry(i, object.getInt("max")));
                                min.add(new Entry(i, object.getInt("min")));

                            }
                            Log.d(TAG, "onResponse: " + avg + "  " + max + "  " + min);

                            setData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: here in error" + error);

                    }
                });
                jsonObjectRequest1.setRetryPolicy(new RetryPolicy() {
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

                requestQueue1.add(jsonObjectRequest1);
            }
        });


        return view;
    }

    private void setData() {

        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(avg);
            set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set2.setValues(max);
            set3 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set3.setValues(max);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(avg, "DataSet 1");
            set2 = new LineDataSet(max, "data_set 2");
            set3 = new LineDataSet(min, "data_set3");
            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(true);
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

            //for set2

            set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set2.setCubicIntensity(0.2f);
            set2.setDrawFilled(true);
            set2.setDrawCircles(true);
            set2.setLineWidth(1.8f);
            set2.setCircleRadius(4f);
            set2.setCircleColor(getResources().getColor(R.color.colorPrimary));
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            set2.setColor(getResources().getColor(R.color.lightpurple));
            set2.setFillColor(getResources().getColor(R.color.lightpurple));
            set2.setFillAlpha(100);
            set2.setDrawHorizontalHighlightIndicator(false);
            set2.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            //for set 3

            set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set3.setCubicIntensity(0.2f);
            set3.setDrawFilled(true);
            set3.setDrawCircles(true);
            set3.setLineWidth(1.8f);
            set3.setCircleRadius(4f);
            set3.setCircleColor(getResources().getColor(R.color.colorPrimary));
            set3.setHighLightColor(Color.rgb(244, 117, 117));
            set3.setColor(getResources().getColor(R.color.green));
            set3.setFillColor(getResources().getColor(R.color.green));
            set3.setFillAlpha(100);
            set3.setDrawHorizontalHighlightIndicator(false);
            set3.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets


            ArrayList<ILineDataSet> datasets = new ArrayList<>();
            datasets.add(set1);
            datasets.add(set2);
            datasets.add(set3);

            LineData data = new LineData(datasets);

            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setValueTextColor(R.color.white);
            data.setDrawValues(true);


            // set data
            lineChart.setData(data);


            lineChart.setVisibleXRangeMaximum(20);
            lineChart.moveViewToX(0);


        }
    }


}
