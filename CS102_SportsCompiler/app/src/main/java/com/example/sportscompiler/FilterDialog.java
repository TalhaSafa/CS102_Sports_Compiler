package com.example.sportscompiler;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private Calendar selectedDate;
    private Calendar selectedTime;

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

    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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


        List<String> quotaCount = new ArrayList<>();

        for(int i = 0; i < 15; i++)
        {
            quotaCount.add(String.valueOf(i));
        }

        List<String> matchFields = new ArrayList<>();

        matchFields.add("Select");
        matchFields.add("Main Campus 1");
        matchFields.add("Main Campus 2");
        matchFields.add("East Campus");

        ArrayAdapter<String> quotaCountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, quotaCount);
        quotaCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quotaCountSpinner.setAdapter(quotaCountAdapter);

        ArrayAdapter<String> matchFieldAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, matchFields);
        matchFieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchFieldSpinner.setAdapter(matchFieldAdapter);



        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                selectedDate = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth)
                    {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                        selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectedTime = Calendar.getInstance();

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
                    {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

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
                        selectedTime.get(Calendar.HOUR_OF_DAY),
                        selectedTime.get(Calendar.MINUTE),
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

                matchField = matchFieldSpinner.getSelectedItem().toString();
                quotaS = quotaCountSpinner.getSelectedItem().toString();
                quota = Integer.parseInt(quotaS);

                if(matchField.equalsIgnoreCase("Select"))
                {
                    matchField = null;
                }

                if(getParentFragment() != null)
                {
                    Bundle result = new Bundle();

                    result.putString("matchField", matchField);
                    result.putInt("quota", quota);

                    if(selectedDate != null)
                    {
                        result.putInt("year", selectedDate.get(Calendar.YEAR));
                        result.putInt("month", selectedDate.get(Calendar.MONTH) + 1);
                        result.putInt("day", selectedDate.get(Calendar.DAY_OF_MONTH));
                    }

                    if(selectedTime != null)
                    {
                        result.putInt("hour", selectedTime.get(Calendar.HOUR_OF_DAY));
                        result.putInt("minute", selectedTime.get(Calendar.MINUTE));
                    }

                    getParentFragmentManager().setFragmentResult("filterDialogData", result);
                    dismiss();
                }
                else
                {
                    Log.e("FilterDialog", "Parent fragment is null");
                }
            }
        });
        return rootView;
    }
}