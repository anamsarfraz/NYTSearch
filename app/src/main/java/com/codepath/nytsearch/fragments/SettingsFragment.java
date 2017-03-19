package com.codepath.nytsearch.fragments;

import android.app.DatePickerDialog;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.databinding.FragmentSettingsBinding;
import com.codepath.nytsearch.util.Constants;
import com.codepath.nytsearch.util.DateConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SettingsFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static DateFormat yyyyMMddFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
    private static DateFormat MMddyyyyFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private FragmentSettingsBinding binding;

    Map<CheckBox, String> cbMap;
    Calendar cal;
    boolean dateSet;



    SharedPreferences mSettings;


    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        View contentView = binding.getRoot();


        cal = Calendar.getInstance();
        dateSet = false;

        cbMap = new HashMap<CheckBox, String>(){{
            put(binding.cbArts, getString(R.string.arts));
            put(binding.cbFashionStyle, getString(R.string.fashion_plus_style));
            put(binding.cbSports, getString(R.string.sports));
        }};
        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String beginDate = getString(R.string.date_placeholder);
        try {
            if (mSettings.contains(Constants.BEGIN_DATE_STR)) {
                beginDate = DateConverter.convertFrom(
                        yyyyMMddFormat,
                        MMddyyyyFormat,
                        mSettings.getString(Constants.BEGIN_DATE_STR,
                        getString(R.string.date_placeholder_YYYYMMDD)
                        ));
            }

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Unable to set begin date filter", Toast.LENGTH_SHORT).show();
        }

        binding.tvDate.setText(beginDate);

        String sortOrder = mSettings.getString(Constants.SORT_STR, Constants.DEFAULT_SORT_ORDER);
        String [] orderArray = getResources().getStringArray(R.array.order_array);
        for (int position = 0; position < orderArray.length; position++) {
            if (orderArray[position].toLowerCase().equals(sortOrder)) {
                binding.spnSortOrder.setSelection(position);
                break;
            }
        }

        String filteredQuery = mSettings.getString(Constants.FILTERED_QUERY_STR, null);
        if (filteredQuery != null) {
            Set<String> filteredSet = new HashSet<>(Arrays.asList(filteredQuery.split("\"")));
            for (Map.Entry<CheckBox, String> cbEntry: cbMap.entrySet()) {
                cbEntry.getKey().setChecked(filteredSet.contains(cbEntry.getValue()));
            }


        }



        binding.btnSave.setOnClickListener(v -> {
            saveFilters();
            OnFilterSettingsChangedListener listener = (OnFilterSettingsChangedListener) getActivity();
            listener.onFilterSettingsChanged();
            dismiss();

        });

        binding.tvDate.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance();
            // SETS the target fragment for use later when sending results
            datePickerFragment.setTargetFragment(SettingsFragment.this, 300);
            datePickerFragment.show(fm, "fragment_date_picker");

        });


        return contentView;
    }

    private void saveFilters() {
        SharedPreferences.Editor editor = mSettings.edit();

        if (dateSet) {
            editor.putString(Constants.BEGIN_DATE_STR, yyyyMMddFormat.format(cal.getTime()));
        }

        if (binding.spnSortOrder.getSelectedItemPosition() == 0) {
            editor.remove(Constants.SORT_STR);
        } else {
            editor.putString(
                    Constants.SORT_STR,
                    binding.spnSortOrder.getSelectedItem().toString().toLowerCase());
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<CheckBox, String> cbEntry: cbMap.entrySet()) {
            if (cbEntry.getKey().isChecked()) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("news_desk:(");
                }
                stringBuilder.append(String.format("\"%s\"", cbEntry.getValue()));
            }
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.append(")");
            editor.putString(Constants.FILTERED_QUERY_STR, stringBuilder.toString());
        } else {
            editor.remove(Constants.FILTERED_QUERY_STR);
        }

        editor.apply();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        binding.tvDate.setText(MMddyyyyFormat.format(cal.getTime()));
        dateSet = true;


    }

    public interface OnFilterSettingsChangedListener {
        void onFilterSettingsChanged();
    }

}
