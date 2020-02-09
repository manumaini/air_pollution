package com.example.air_pollution;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Calendar;

public class historic_dataFragment extends Fragment{

    private ImageView imageView1;
    private ImageView imageView2;
    private EditText from_date;
    private EditText to_date;
    private LineChart lineChart;
    private Typeface tfLight = Typeface.DEFAULT;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historic_data_analysis,container,false);
        imageView1 = view.findViewById(R.id.image_c1);
        imageView2 = view.findViewById(R.id.image_c2);
        from_date = view.findViewById(R.id.from_date);
        to_date = view.findViewById(R.id.to_date);
        lineChart = view.findViewById(R.id.historic_chart);

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
        setData(100,500);


        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                from_date.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
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
                                to_date.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDoneText("done")
                        .setCancelText("cancel")
                        .setThemeDark();
                cdp.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);

            }
        });



        return view;
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range + 1)) + 20;
            values.add(new Entry(i, val));
        }

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
            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            lineChart.setData(data);

            lineChart.setVisibleXRangeMaximum(20);
            lineChart.moveViewToX(0);
        }
    }


}
