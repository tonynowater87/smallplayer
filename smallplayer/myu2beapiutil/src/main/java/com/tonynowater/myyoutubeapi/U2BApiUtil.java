package com.tonynowater.myyoutubeapi;

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

}
