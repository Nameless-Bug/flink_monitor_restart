package com.nameless.monitor_restart.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RestUtil {

    private RestUtil(){}

    private static Logger logger = LoggerFactory.getLogger(RestUtil.class);

    public static String getResponse(String urlAddress){
        String str;
        StringBuilder response = new StringBuilder();

        try{
            URL url = new URL(urlAddress);
            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) URLconnection;
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                while ((str = br.readLine())!=null){
                    response.append(str);
                }
                br.close();
            }else{
                logger.error("failed to get response from "+urlAddress);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
