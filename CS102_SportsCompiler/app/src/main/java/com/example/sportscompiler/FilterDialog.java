package com.example.sportscompiler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterDialog extends DialogFragment {

    private String matchField, quotaS;
    private int quota;
    private Spinner matchFieldSpinner, quotaCountSpinner;
    private Button submitButton, dateButton, timeButton, cancelButton;

    private Calendar selectedDateTime;

    public FilterDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterDialog newInstance(String param1, String param2) {
        FilterDialog fragment = new FilterDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        submitButton = rootView.findViewById(R.id.submit_button);
        matchFieldSpinner = rootView.findViewById(R.id.match_field_spinner);
        quotaCountSpinner = rootView.findViewById(R.id.quota_Count_spinner);
        dateButton = rootView.findViewById(R.id.date_button);
        timeButton = rootView.findViewById(R.id.time_button);
        cancelButton = rootView.findViewById(R.id.cancel_button);
        submitButton = rootView.findViewById(R.id.submit_button);

        matchField = matchFieldSpinner.getSelectedItem().toString();
        quotaS = quotaCountSpinner.getSelectedItem().toString();
        quota = Integer.parseInt(quotaS);

        selectedDateTime = Calendar.getInstance();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
                    {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                        selectedDateTime.get(Calendar.YEAR),
                        selectedDateTime.get(Calendar.MONTH),
                        selectedDateTime.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
                    {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);

                        String minuteText;

                        if(minute < 10)
                        {
                            minuteText = "0" + minute;
                        }
                        else
                        {
                            minuteText = String.valueOf(minute);
                        }

                        timeButton.setText(hourOfDay + ":" + minuteText);
                    }
                },
                        selectedDateTime.get(Calendar.HOUR_OF_DAY),
                        selectedDateTime.get(Calendar.MINUTE),
                        true
                );
                timePickerDialog.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(getParentFragment() != null)
                {
                    Bundle result = new Bundle();
                    result.putString("matchField", matchField);
                    result.putInt("quota", quota);
                }
            }
        });




        return rootView;

    }
}