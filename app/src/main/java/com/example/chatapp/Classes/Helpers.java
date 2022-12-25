package com.example.chatapp.Classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Helpers {
    public static String parseDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat;
        Date date;
        try {
            date = inputFormat.parse(inputDate);
            assert date != null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar currentCalendar = Calendar.getInstance();
            if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
                // Date is today
                outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } else if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH) - 1) {
                // Date is yesterday
                outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String output = outputFormat.format(date);
                return "Yesterday";
            } else if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                // Date is within the same month
                int daysAgo = currentCalendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
                return daysAgo + " days ago";
            } else if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
                // Date is within the same year
                outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } else {
                // Date is more than 1 week ago
                outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}