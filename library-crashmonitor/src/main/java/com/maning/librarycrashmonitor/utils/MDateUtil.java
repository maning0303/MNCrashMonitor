package com.maning.librarycrashmonitor.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by maning on 2017/4/20.
 */

public class MDateUtil {

    public static String dataToTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

}
