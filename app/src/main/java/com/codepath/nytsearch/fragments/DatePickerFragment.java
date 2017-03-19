package com.codepath.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.codepath.nytsearch.R;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    public DatePickerFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment DatePickerFragment.
     */
    public static DatePickerFragment newInstance() {
        return new DatePickerFragment();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getTargetFragment();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getContext(), R.style.SettingDatePickerStyle, listener, year, month, day);
    }

}
