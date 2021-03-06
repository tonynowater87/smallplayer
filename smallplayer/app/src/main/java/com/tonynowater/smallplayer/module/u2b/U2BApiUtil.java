package com.tonynowater.smallplayer.module.u2b;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/7.
 */

public class U2BApiUtil {
    private U2BApiUtil() {
    }

    public static List<String> getSuggestionStringList(String response) {

        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONArray arraySuggestions = jsonArray.getJSONArray(1);
            for (int i = 0; i < arraySuggestions.length(); i++) {
                list.add(arraySuggestions.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int formateU2BDurationToMilionSecond(String duration) {
        try {
            duration = duration.replace("PT","");
            int hour = 0;
            int hourIndex = duration.indexOf("H");
            if (hourIndex != -1) {
                hour = Integer.parseInt(duration.substring(0, hourIndex));
            }
            int minute = 0;
            int minuteIndex = duration.indexOf("M");
            if (minuteIndex != -1) {
                minute = Integer.parseInt(duration.substring(hourIndex + 1, minuteIndex));
            }
            int second = 0;
            int secondIndex = duration.indexOf("S");
            if (minuteIndex == -1 && secondIndex != -1) {
                //1H10S
                second = Integer.parseInt(duration.substring(hourIndex + 1, secondIndex));
            } else if (minuteIndex == -1 && secondIndex == -1) {
                //6H
            } else if (minuteIndex != -1 && secondIndex != -1) {
                //有HMS正常情況
                second = Integer.parseInt(duration.substring(minuteIndex + 1, secondIndex));
            }
            return (hour * 60 * 60 + minute * 60 + second) * 1000;
        } catch (Exception e) {
            return -1;
        }
    }
}
