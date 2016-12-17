package net.rodrigobrito.firetodolist.util;

import android.content.Context;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rodrigo on 10/09/16.
 */
public class DateUtil {
    private SimpleDateFormat sdf;

    public DateUtil(Context context) {
        this.sdf = new SimpleDateFormat();
    }

    public String parse(Date date){
        if(date == null){
            return "";
        }
        return new SimpleDateFormat("EEE").format(date) + ", " + sdf.format(date);
    }
}
