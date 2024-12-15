package com.example.sportscompiler;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firestore.bundle.BundleElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SortDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortDialogFragment extends DialogFragment {

    private Button cancelButton;
    private Button submitButton;
    private RadioGroup sortingRadioGroup;
    private String sortingResult;


    public SortDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SortDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SortDialogFragment newInstance(String param1, String param2) {
        SortDialogFragment fragment = new SortDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sort_dialog, container, false);
        cancelButton = rootView.findViewById(R.id.cancel_button);
        submitButton = rootView.findViewById(R.id.submitButton);
        sortingRadioGroup = rootView.findViewById(R.id.sortingRadioGroup);

        if(getDialog() != null && getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int selectedID = sortingRadioGroup.getCheckedRadioButtonId();
                if(selectedID != -1)
                {
                    RadioButton radio = rootView.findViewById(selectedID);
                    sortingResult = radio.getText().toString();
                }

                if(getParentFragmentManager() != null)
                {
                    Bundle result = new Bundle();
                    result.putString("sortingOption", sortingResult);
                    getParentFragmentManager().setFragmentResult("sortRequest", result);
                }
                dismiss();
            }
        });
        return rootView;
    }
}