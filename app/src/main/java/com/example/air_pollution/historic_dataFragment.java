package com.example.air_pollution;

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


import java.util.Calendar;

public class historic_dataFragment extends Fragment{

    private ImageView imageView1;
    private ImageView imageView2;
    private EditText from_date;
    private EditText to_date;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historic_data_analysis,container,false);
        imageView1 = view.findViewById(R.id.image_c1);
        imageView2 = view.findViewById(R.id.image_c2);
        from_date = view.findViewById(R.id.from_date);
        to_date = view.findViewById(R.id.to_date);

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


}
