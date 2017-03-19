package com.codepath.nytsearch.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by usarfraz on 3/18/17.
 */

public class DateConverter {
    public static String convertFrom(DateFormat fromFormat, DateFormat toFormat, String inputDate) throws ParseException {
        return toFormat.format(fromFormat.parse(inputDate));
    }
}
