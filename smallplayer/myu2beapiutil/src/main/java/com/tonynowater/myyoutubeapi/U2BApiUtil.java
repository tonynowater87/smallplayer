package com.tonynowater.myyoutubeapi;

import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/7.
 */

public class U2BApiUtil {
    private U2BApiUtil() {
    }

    public static List<String> getSuggestionStringList(Response response) {

        List<String> list = new ArrayList<>();
        try {
            String s = new String(response.body().bytes());
            JSONArray jsonArray = new JSONArray(s);
            JSONArray arraySuggestions = jsonArray.getJSONArray(1);
            for (int i = 0; i < arraySuggestions.length(); i++) {
                list.add(arraySuggestions.getString(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

}
