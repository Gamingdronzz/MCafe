package com.mcafeweb;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Balpreet on 12/3/2015.
 */
public class ServiceHandler
{
    //input stream deals with bytes
    HttpsURLConnection connection = null;
    BufferedReader reader = null;
    final String TAG = "ServiceHandler";




    //this method returns json object.
    public String getResultFromServer(String myURL, String params) throws FileNotFoundException,IOException,MalformedURLException {
        URL url;
        try
        {
            url = new URL(myURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(params);
            outputStreamWriter.flush();
            Log.v(TAG, "Connected");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            // Read Server Response
            String line = "";

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            //Log.v(TAG,sb.toString());
            String result = sb.toString();
            String[] a = result.split("\",\"");
            for (String s:a
                 ) {
                Log.v(TAG,s);
            }
            Log.v(TAG,"Done");
            return result;
        }
        catch (FileNotFoundException fnfe)
        {
            throw fnfe;
        }
        catch (MalformedURLException mURL)
        {
            throw mURL;
        }
        catch (IOException ioe)
        {
            throw ioe;
        }
    }
}

