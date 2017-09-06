package com.mcafeweb.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Balpreet on 26-Feb-17.
 */
public class Helper {
    public static Helper Instance;
    private Context context;
    final String TAG = "Helper";
    public String path = "android";
    public String site = "www.mcafeweb.com";
    public String baseURL = "http://www.mcafeweb.com/android/";

    public final String REGISTER_RESULT = "register_result";
    public final String LOGIN_RESULT = "login_result";
    public final String INTEREST_SUBMIT_RESULT = "interest_submit_result";
    public final String GET_INTEREST_LIST = "interest_list";


    public final String SUCCESS = "Success";
    public final String FAILURE = "Failed";

    public final String USER_EXISTS = "User Exists";


    public final String ROLE = "role";


    public final String ROLE_ADMIN = "admin";
    public final String ROLE_CONTRIBUTOR = "contributor";
    public final String ROLE_MODERATOR = "moderator";
    public final String ROLE_MEMBER = "member";

    public SimpleDateFormat format;

    public final String[] Categories =
            {
                    "Beauty and Makeup",
                    "Comic Corner",
                    "Digital Marketing",
                    "Happy Parenting",
                    "Health and Fitness",
                    "Language and Communication",
                    "Motivation and Inspiration",
                    "My Lifebook",
                    "Personal Branding",
                    "Social Media",
                    "Spiritual",
                    "Sports",
                    "Travel Diaries"
            };



    public Map<String,String> NewCategories;


    public final String CREATE_GROUP_RESULT = "create_group_result";
    public final String CREATE_BLOG_RESULT = "create_blog_result";

    public final String PERFORM_LIKE_RESULT = "perform_like_result";

    public final String ACTION = "action";


    public Helper(Context context) {
        Instance = this;
  //      NewCategories = new HashMap<String, String>();
        //format = new SimpleDateFormat(exprectedPattern);
        this.context = context;
    }

    public JSONObject getJson(String input) {
        String json = input.substring(input.indexOf("{"), input.indexOf("}") + 1);
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException jse) {
            jse.printStackTrace();
            Log.v(TAG, "Error creating json");
        }
        //Log.v(TAG, result.toString());
        return result;
    }

    public String getStringNumeric(int number)
    {
        if (number < 1000) {
            return number + "";
        } else if (number < 1000000) {
            return String.format("%.2f", number / 1000.0f) + "K";

        } else if (number < 100000000) {
            return String.format("%.2f", number / 100000.0f) + "M";
        } else {
            return String.format("%.2f", number / 1000000000.0f) + "B";
        }
    }

    public void callError(VolleyError error)
    {
        if (error.getClass() == TimeoutError.class) {
            Toast.makeText(context.getApplicationContext(), "Connection is slow", Toast.LENGTH_SHORT);
        }
        if (error.getClass() == NoConnectionError.class) {
            Toast.makeText(context.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT);
        }
        if (error.getClass() == NetworkError.class) {
            Toast.makeText(context.getApplicationContext(), "This connection may not have internet connectivity", Toast.LENGTH_SHORT);
        }
    }

    public static int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = Instance.context.getResources().getIdentifier("mdcolor_" + typeColor, "array", Instance.context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = Instance.context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    public String BuildURL(String site, String[] path) {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme("https").authority(site);
        if (path != null) {
            for (int i = 0; i < path.length; i++) {
                uriBuilder.appendPath(path[i]);
            }
        }
        return uriBuilder.build().toString();

    }


    public String getParameters(String[] keys, String[] values) {
        StringBuilder data = new StringBuilder();
        if(keys.length>0) {


            try {
                for (int i = 0; i < keys.length; i++) {
                    if (i == 0)
                        data.append(URLEncoder.encode(keys[i].toString(), "UTF-8")
                                + "=" + URLEncoder.encode(values[i], "UTF-8"));
                    if (i != 0)
                        data.append("&" + URLEncoder.encode(keys[i].toString(), "UTF-8")
                                + "=" + URLEncoder.encode(values[i], "UTF-8"));
                }
                //Log.v(TAG,"key - value " + data);
            } catch (UnsupportedEncodingException fe) {
                Log.v(TAG, "Format Exception Occured");
            }
            //Log.v(TAG, data.toString());
        }
        return data.toString();
    }



    public String getStringFromList(List list)
    {
        StringBuilder result = new StringBuilder();
        for(int i = 0;i<list.size();i++)
        {
            result.append(list.get(i).toString().toLowerCase());
            result.append(",");
        }
        result.deleteCharAt(result.length()-1);
        Log.v(TAG,"Final String : " + result.toString());
        return result.toString();
    }

    public List<String> getListFromString(String string)
    {
        String[] intermediate = string.split(",");
        List<String> result = new ArrayList<String>();
        for(int i = 0;i<intermediate.length;i++)
        {
            result.add(intermediate[i].toLowerCase());
        }
        Log.v(TAG,"List Result : " + result );
        return result;


    }


    public Intent getImageIntent()
    {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return intent;
    }

    public String getStringFromBitmap(Bitmap image)
    {
        if(image!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String result = Base64.encodeToString(bytes,Base64.DEFAULT);
            return result;
        }
        else
        {
            return null;
        }
    }

    public Bitmap getBitmapFromString(String value)
    {
        byte[] inter = Base64.decode(value,Base64.DEFAULT);
        Log.d(TAG,"Byte Array = " + inter.toString());
        return BitmapFactory.decodeByteArray(inter,0,inter.length);
    }

    /*
    public Bitmap getBitmapFromByteArray(byte[] value)
    {
        if(value!=null)
            return BitmapFactory.decodeByteArray(value,0,value.length);
        else
            return null;
    }
    */

    public Date getDatefromString(String input)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = dateFormat.parse(input);
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }
        return d;
    }

    public String getStringFromDate(String input)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = dateFormat.parse(input);
        }
        catch (ParseException pe)
        {
            pe.printStackTrace();
        }

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        Log.d(TAG,"GMT offset is " + TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS) + "  hours");

        //SimpleDateFormat dest = new SimpleDateFormat("dd-MM-yy hh:mm a", Locale.ENGLISH);
        SimpleDateFormat dest = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        dest.setTimeZone(TimeZone.getTimeZone("GMT+02:04"));
        String result = dest.format(d);
        return result;
    }


    public String getFirstNameFromFullName(String FullName)
    {
        return FullName.substring(0,FullName.indexOf(' '));
    }

    public String getLastNameFromFullName(String FullName)
    {
        return FullName.substring(FullName.indexOf(' ')+1,FullName.length());
    }

    public void printJSON(JSONObject json)
    {
        String a = json.toString();
        a = a.replace("{","");
        a = a.replace(",","");
        String[] split = a.split("\"");
        for (String s :
                split) {
            Log.d(TAG,"JSON : " + s);

        }
    }

}
