package com.carloshoil.waaljanal.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class FragmenDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    EditText editText;
    public FragmenDatePicker(EditText editText)
    {
        this.editText=editText;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(requireContext(), this, year, month, day);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        editText.setText(day+"/"+getMonth(month)+"/"+year);
    }

    private String getMonth(int iMonth)
    {
        String cMonth="";
        switch (iMonth) {
            case 0:
                cMonth = "ENE";
                break;
            case 1:
                cMonth = "FEB";
                break;
            case 2:
                cMonth = "MAR";
                break;
            case 3:
                cMonth = "ABR";
                break;
            case 4:
                cMonth="MAY";
                break;
            case 5:
                cMonth="JUN";
                break;
            case 6:
                cMonth="JUL";
                break;
            case 7:
                cMonth="AGO";
                break;
            case 8:
                cMonth="SEP";
                break;
            case 9:
                cMonth="OCT";
                break;
            case 10:
                cMonth="NOV";
                break;
            case 11:
                cMonth="DIC";
                break;

        }
        return cMonth;
    }
}
