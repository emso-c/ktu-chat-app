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

    public static int compareDates(String date1, String date2){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(date1);
            d2 = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d1.compareTo(d2) < 0) {
            return 1;
        } else if (d1.compareTo(d2) > 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public static String parseLastSeen(String inputDate){
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = inputFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar inputCalendar = Calendar.getInstance();
        inputCalendar.setTime(date);

        Calendar currentCalendar = Calendar.getInstance();

        long timeDifference = currentCalendar.getTimeInMillis() - inputCalendar.getTimeInMillis();
        long minutes = timeDifference / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;

        if(days == 0){
            // today
            if(hours == 0){
                // this hour
                if(minutes == 0){
                    // just now
                    return "just now";
                }else{
                    // x minutes ago
                    return minutes + " minutes ago";
                }
            }else{
                // x hours ago
                return hours + " hours ago";
            }
        }else if(days == 1){
            // yesterday
            return "yesterday";
        }else{
            // dd.mm.yyyy
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return outputFormat.format(date);
        }
    }
}