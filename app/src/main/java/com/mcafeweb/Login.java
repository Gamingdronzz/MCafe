package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    FloatLabeledEditText Username;
    FloatLabeledEditText Password;
    TextView ForgotPasword;
    Button Login;
    Button RegisterHere;
    LinearLayout loginLayout;
    LinearLayout progressLayout;

    InputMethodManager inputMethodManager;
    Helper helper;

    DBHelper dbHelper;
    SQLiteDatabase db;
    private String TAG = "Login";

    VolleyHelper volleyHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        setupDatabase();

        Username = (FloatLabeledEditText) findViewById(R.id.login_Username);
        Password = (FloatLabeledEditText) findViewById(R.id.login_password);

        Login = (Button) findViewById(R.id.login_Button_Login);
        RegisterHere = (Button) findViewById(R.id.Register_From_Login_Screen);
        ForgotPasword = (TextView) findViewById(R.id.login_forgot_password);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        RegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRegisterScreen();
            }
        });

        ForgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordTask();
            }
        });
        loginLayout = (LinearLayout) findViewById(R.id.Login_Layout);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        showProgressLayout(false);

        if (dbHelper.getUserID() != -1) {
            Log.v(TAG, "User id Found");
            Username.getEditText().setText(dbHelper.getUserEmail());
            Password.getEditText().setText(dbHelper.getUserPassword());
            LoginUser();
        }
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
        dbHelper.createFirst();
    }

    void LoginUser() {
        if (checkForInputData()) {
            hideSoftInput();
            showProgressLayout(true);

            String username = Username.getEditText().getText().toString();
            String password = Password.getEditText().getText().toString();
            String url = helper.baseURL + "login.php5";
            Map<String, String> params = new HashMap<String, String>();
            params.put("emailid", username);
            params.put("password", password);
            params.put("userid", dbHelper.getUserID() + "");
            volleyHelper.makeStringRequest(url, "LOGIN", params);
        } else {

        }
    }

    void ForgotPasswordTask() {
        /*String username = Username.getText().toString();
        if(username.equals(""))
        {
            Toast.makeText(this,"Please enter your email id",Toast.LENGTH_LONG).show();
        }
        else {
            String[] path = new String[]{Helper.path, "forgotpassword.php5"};
            String[] keys = new String[]{"emailid"};
            String[] values = new String[]{Username.getText().toString().toLowerCase()};
            Helper helper = new Helper();
            String url = helper.BuildURL(Helper.site, path);
            String data = helper.getParameters(keys, values);
            //String url = "http://www.mcafeweb.com/android/register.php?firstname=" + FirstName.getText()+"&lastname=last&emailid=sdf&password=dsfzs";
            new FetchDataFromServer(this).execute(url, data);
        }
        */
        Toast.makeText(this, "Under Construction", Toast.LENGTH_SHORT).show();
    }

    void ShowRegisterScreen() {
        Intent intent = new Intent(this, SignUp.class);
        startActivityForResult(intent,01);
    }

    boolean checkForInputData() {
        String username = Username.getEditText().getText().toString();
        username = username.replaceAll("\\s+", "");
        Username.getEditText().setText(username);

        String password = Password.getEditText().getText().toString();
        password = password.replaceAll("\\s+", "");
        Password.getEditText().setText(password);

        if (username.length() == 0) {
            Toast.makeText(Login.this, "Enter Email ID", Toast.LENGTH_SHORT).show();
            Username.requestFocus();
            return false;
        }
        if (password.length() == 0) {
            Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
            Password.requestFocus();
            return false;
        }
        return true;
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            loginLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            loginLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(Username.getWindowToken(), 0);
    }

    private void LoadNextActivity() {
        Intent intent;
        //PreferencesManager manager = new PreferencesManager(this);
        if (dbHelper.getInterestCount() != -1) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, InterestSelection.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponse(String result) {
        Log.d(TAG, result);
        Helper helper = new Helper(this);
        JSONObject json = helper.getJson(result);
        try {
            if (json.getString(helper.LOGIN_RESULT).equals(helper.SUCCESS)) {

                String email = Username.getEditText().getText().toString().toLowerCase();
                String password = Password.getEditText().getText().toString();
                if (dbHelper.getUserID() == -1) {
                    int Userid = json.getInt("userid");
                    dbHelper.setUserID(Userid);
                }
                String firstName = json.getString("firstname");
                String lastName = json.getString("lastname");
                int favBlogListID = json.getInt("favbloglistid");
                String favBlogListName = json.getString("favbloglistname");

                if (json.getString("interest_result").equals(helper.SUCCESS)) {
                    int interestCount = json.getInt("row_count_interest");
                    for (int i = 0; i < interestCount; i++) {
                        dbHelper.addUserInterest(json.getInt("interest_id_" + i));
                    }
                } else {

                }


                if (dbHelper.getInterestList() == null) {
                    int interests = json.getInt("row_count_all_interest");
                    for (int i = 0; i < interests; i++) {
                        int int_id = json.getInt("all_interest_id_" + i);
                        String int_name = json.getString("all_interest_name_" + i);
                        dbHelper.addInterestToTable(int_id, int_name);
                    }
                }

                dbHelper.setUserFirstName(firstName);
                dbHelper.setUserLastName(lastName);
                dbHelper.setUserEmail(email);
                dbHelper.setUserPassword(password);
                dbHelper.setFavBlogList(favBlogListID, favBlogListName);
                try {
                    dbHelper.setUserRole(json.getString("role"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                LoadNextActivity();
            } else {
                Toast.makeText(this, "No Account found\nPlease Register before you Log In", Toast.LENGTH_LONG).show();
                showProgressLayout(false);
            }
        } catch (JSONException jse) {
            Toast.makeText(this, "Unable to Login\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            jse.printStackTrace();
            showProgressLayout(false);
        }

    }

    @Override
    public void onResponse(JSONObject result) {

    }

    @Override
    public void onResponse(JSONArray result) {

    }

    @Override
    public void onResponse(NetworkResponse result) {

    }

    @Override
    public void onError(VolleyError error) {
        showProgressLayout(false);
        //Toast.makeText(getApplicationContext(), "Unable to login\nPlease try again after some time", Toast.LENGTH_LONG).show();4
        helper.callError(error);
    }
}
