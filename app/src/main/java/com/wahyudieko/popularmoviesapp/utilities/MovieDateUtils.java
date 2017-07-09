package com.wahyudieko.popularmoviesapp.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EKO on 09/07/2017.
 */

public class MovieDateUtils {

    public static String simpleDateFormat(String dateStr){

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
        Date inputDate;
        String outputDate;
        try {
            inputDate = input.parse(dateStr);
            outputDate = output.format(inputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
