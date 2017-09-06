package com.mcafeweb;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mcafeweb.utils.Helper;

import java.util.List;

/**
 * Created by Balpreet on 17-Feb-17.
 */
public class PreferencesManager {
    private final String TAG = "PreferencesManager";


    public final String MyPREFERENCES = "MyPrefs";

    private Context context;


    private final String User_ID = "user_id";
    private final String First_Name = "first_name_Key";
    private final String Middle_Name = "middle_name_Key";
    private final String Last_Name = "last_name_key";
    private final String Full_Name = "full_name_key";
    private final String Gender = "gender_Key";
    private final String Account_type = "account_type_Key";
    private final String Login_Status = "login_status_Key";
    private final String Sign_Up_Status = "sign_up_status_Key";
    private final String Email = "email_key";
    private final String Password = "password_key";

    private final String Interest_Status = "interest_status_Key";
    private final String Interests = "interest_status";

    private final String ACCOUNT_TYPE_ADMIN = "admin";
    private final String ACCOUNT_TYPE_CONTRIBUTOR = "contributor";
    private final String ACCOUNT_TYPE_MEMBER = "member";

    //private List<String> Interests;


    public void setContext(Context context)
    {
        this.context = context;
    }

    public List<String> getInterests()
    {
        String value = sharedPreferences.getString(Interests,"");
        Helper helper = new Helper(context);
        List<String> result = helper.getListFromString(value);
        return result;

    }

    public void setInterests(String value)
    {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Interests,value);
        editor.commit();
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG,"All : " + sharedPreferences.getAll());
    }

    public void setLoginStatus(boolean value)
    {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putBoolean(Login_Status,value);
        editor.commit();
    }
    public boolean getLoginStatus()
    {
        boolean value =sharedPreferences.getBoolean(Login_Status, false);
        Log.d(TAG, value + "");
        return value;
    }

    public void setUser_ID(int value)
    {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putInt(User_ID,value);
        editor.commit();
    }
    public int getUser_ID()
    {
        int value =sharedPreferences.getInt(User_ID,0);
        Log.d(TAG, value + "");
        return value;
    }

    public void setSignUpStatus(boolean value)
    {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putBoolean(Sign_Up_Status, value);
        editor.commit();
    }
    public boolean getSignUpStatus() {
        boolean value =sharedPreferences.getBoolean(Sign_Up_Status, false);
        Log.d(TAG, value + "");
        return value;
    }



    public void setInterestStatus(boolean value)
    {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putBoolean(Interest_Status, value);
        editor.commit();
    }

    public boolean getInterestStatus()
    {
        boolean value =sharedPreferences.getBoolean(Interest_Status,false);
        Log.d(TAG, value + "");
        return  value;
    }


    public void setFirstName(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(First_Name,Value);
        editor.commit();
    }

    public String getFirstName()
    {
        return sharedPreferences.getString(First_Name,"John");
    }

    public void setLastName(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Last_Name, Value);
        editor.commit();
    }
    public String getLastName()
    {
        return sharedPreferences.getString(Last_Name,"Doe");
    }
    public void setMiddleName(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Middle_Name, Value);
        editor.commit();
    }
    public String getMiddleName()
    {
        return sharedPreferences.getString(Middle_Name,"");
    }
    public void setFullName(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Full_Name, Value);
        editor.commit();
    }
    public String getFullName()
    {
        return sharedPreferences.getString(Full_Name,"John Doe");
    }
    public void setGender(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Gender, Value);
        editor.commit();
    }
    public String getGender()
    {
        return sharedPreferences.getString(Gender,"Male");
    }
    public void setAccountType(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Account_type, Value);
        editor.commit();
    }
    public String getAccountType()
    {
        return sharedPreferences.getString(Account_type,"User");
    }

    public void setEmail(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Email, Value);
        editor.commit();
    }
    public String getEmail()
    {
        return sharedPreferences.getString(Email,"");
    }

    public void setPassword(String Value) {
        editor = sharedPreferences.edit();
        //editor.clear();
        editor.putString(Password, Value);
        editor.commit();
    }
    public String getPassword()
    {
        return sharedPreferences.getString(Password,"");
    }

    public void clearPreferences()
    {
        editor = sharedPreferences.edit();
        editor.remove(First_Name);
        editor.remove(Middle_Name);
        editor.remove(Last_Name);
        editor.remove(Full_Name);
        editor.remove(Gender);
        editor.remove(Account_type);
        editor.remove(Login_Status);
        editor.remove(Sign_Up_Status);
        editor.remove(Email);
        editor.remove(Password);
        editor.remove(Interest_Status);
        editor.remove(Interests);
    }
}
