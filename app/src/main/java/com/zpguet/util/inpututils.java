package com.zpguet.util;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class inpututils {

//    private static String APIKEY = "e73ca560fd9f48a982ce8ef2b87b36bd";

    public static String getString(String question) {
        String out = null;
        try {
            String info = URLEncoder.encode(question, "utf-8");
            URL url = new URL("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+ info);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            connection.setConnectTimeout(10 * 1000);
            connection.setRequestMethod("GET");
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream inputStream = connection.getInputStream();
                String resutl = streamutils.streamToString(inputStream);
                JSONObject object = new JSONObject(resutl);
                out = object.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;

    }
}
