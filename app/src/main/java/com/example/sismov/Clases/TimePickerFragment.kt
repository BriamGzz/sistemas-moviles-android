package com.example.sismov.Clases

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(val listener: (String) -> Unit):DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar: Calendar = Calendar.getInstance()
        val hour:Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute:Int = calendar.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(activity as Context, this, hour, minute, true)
        return dialog
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {

        var sHour : String
        var sMinute : String

        if (hour < 10) sHour = "0$hour"
        else sHour = "$hour"

        if (minute < 10) sMinute = "0$minute"
        else sMinute = "$minute"

        listener("$sHour:$sMinute:00")
    }
}