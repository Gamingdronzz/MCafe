package com.mcafeweb;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
public class SignUp extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    LoginButton facebookLoginButton;
    String TAG = "SignUp";
    CallbackManager callbackManager;
    EditText EmailID, FirstName, LastName, Password;
    NetworkManager networkManager;

    TextView InfoText;

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    Button RegisterUser;
    Button RegisterViaGoogle;
    //Button Login;

    LinearLayout registerLayout;
    LinearLayout progressLayout;
    InputMethodManager inputMethodManager;

    Helper helper;
    DBHelper dbHelper;
    VolleyHelper volleyHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().hide();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this, this);
        setupDatabase();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EmailID = (EditText) findViewById(R.id.EditText_Email);
        FirstName = (EditText) findViewById(R.id.EditText_FirstName);
        LastName = (EditText) findViewById(R.id.EditText_LastName);
        Password = (EditText) findViewById(R.id.EditText_Password);

        InfoText = (TextView) findViewById(R.id.InfoTextSignUp);

        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        progressLayout = (LinearLayout) findViewById(R.id.registerProgressLayout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.v(TAG, "Connection failed");
            }
        };

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        EmailID.addTextChangedListener(watcher);
        FirstName.addTextChangedListener(watcher);
        LastName.addTextChangedListener(watcher);
        Password.addTextChangedListener(watcher);

        //facebookId = Profile_Image = "";

        networkManager = new NetworkManager(getApplicationContext());
        Log.d(TAG, "Setting up Facebook");
        setUpFacebook();

        RegisterUser = (Button) findViewById(R.id.button_Register);
        RegisterViaGoogle = (Button) findViewById(R.id.button_Google);
        //Login = (Button) findViewById(R.id.Login_From_Register);

        RegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        RegisterViaGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDetailsFromGoogle();
            }
        });

        showProgressLayout(false);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.mcafeweb",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("Key", "KeyHash : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException nnfe) {
            nnfe.printStackTrace();
        }


    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
        dbHelper.createFirst();
    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (s.equals("\\s")) {
                Toast.makeText(SignUp.this, "Field Cannot contain spaces", Toast.LENGTH_SHORT);

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if you don't add following block,
        // your registered `FacebookCallback` won't be called

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                dbHelper.setUserEmail(acct.getEmail());
                dbHelper.setUserFirstName(Helper.Instance.getFirstNameFromFullName(acct.getDisplayName()));
                dbHelper.setUserLastName(Helper.Instance.getLastNameFromFullName(acct.getDisplayName()));
                setInputFields();
                showProgressLayout(false);

            }
        }
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            showProgressLayout(false);
        }
        showProgressLayout(false);
        return;
    }


    protected void GetDetailsFromGoogle() {
        InfoText.setText("Connecting via Google\nPlease be patient");
        showProgressLayout(true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void setUpFacebook() {
        InfoText.setText("Connecting via Facebook\nHold on");
        callbackManager = CallbackManager.Factory.create();
        showProgressLayout(true);
        facebookLoginButton = (LoginButton) findViewById(R.id.Facebook_LoginButton);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook Login Succesful");
                Toast.makeText(SignUp.this, "Facebook Login Succesful", Toast.LENGTH_SHORT).show();
                Profile profile = Profile.getCurrentProfile();
                if (profile == null) {
                    Log.d(TAG, "Facebook profile Null");
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            Profile.setCurrentProfile(profile2);
                            mProfileTracker.stopTracking();
                            SetDetails(profile2);
                        }
                    };
                } else {
                    Log.d(TAG, "Facebook profile Found");
                    SetDetails(profile);
                }

                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    String email = object.optString("email");
                                    dbHelper.setUserEmail(email);
                                    String profile_name = object.getString("name");
                                    long fb_id = object.getLong("id"); //use this for logout
                                    Log.d(TAG, "First Name : " + dbHelper.getUserFirstName() +
                                            "Last Name : " + dbHelper.getUserLastName());
                                    EmailID.setText(email);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook Login Cancelled");
                Toast.makeText(SignUp.this, "Facebook Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook Login Error");
                error.printStackTrace();
                Toast.makeText(SignUp.this, "Facebook Login Error! Please try after some time", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SetDetails(Profile profile) {
        Log.d(TAG, "Setting Facebook profile Details");
        //facebookId = profile.getId();
        dbHelper.setUserFirstName(profile.getFirstName());
        dbHelper.setUserLastName(profile.getLastName());
        //Profile_Image = profile.getProfilePictureUri(256, 256).toString();
        setInputFields();
        showProgressLayout(false);
    }

    void setInputFields() {

        EmailID.setText(dbHelper.getUserEmail());
        FirstName.setText(dbHelper.getUserFirstName());
        LastName.setText(dbHelper.getUserLastName());
        Password.setText("");
        Password.requestFocus();
    }

    public void RegisterUser() {
        hideSoftInput();
        InfoText.setText("Signing you up for some\ncool things");
        showProgressLayout(true);
        Log.d(TAG, "Trying to Register User on Server");


        if (checkForInputData()) {


            String firstName = FirstName.getText().toString();
            String lastName = LastName.getText().toString();
            String emailid = EmailID.getText().toString();
            String password = Password.getText().toString();
            String url = helper.baseURL + "register.php5";
            Map<String, String> params = new HashMap<String, String>();
            params.put("firstname", firstName);
            params.put("lastname", lastName);
            params.put("email", emailid);
            params.put("password", password);
            volleyHelper.makeStringRequest(url, "Register", params);
        }
    }

    void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(EmailID.getWindowToken(), 0);
    }

    boolean checkForInputData() {
        String email = EmailID.getText().toString();
        email = email.replaceAll("\\s+", "");
        EmailID.setText(email);


        String fname = FirstName.getText().toString();
        fname = fname.replaceAll("\\s+", "");
        FirstName.setText(fname);

        String lname = LastName.getText().toString();
        lname = lname.replaceAll("\\s+", "");
        LastName.setText(lname);

        String pword = Password.getText().toString();
        pword = pword.replaceAll("\\s+", "");
        Password.setText(pword);
        if (EmailID.getText().length() == 0) {
            Toast.makeText(SignUp.this, "Enter Email ID", Toast.LENGTH_SHORT).show();
            EmailID.requestFocus();
            showProgressLayout(false);
            return false;
        } else if (email.length() > 0) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUp.this, "Enter proper email ID", Toast.LENGTH_SHORT).show();
                EmailID.requestFocus();
                showProgressLayout(false);
                return false;
            }
        }
        if (fname.length() == 0) {
            Toast.makeText(SignUp.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            FirstName.requestFocus();
            showProgressLayout(false);
            return false;
        }
        if (lname.length() == 0) {
            Toast.makeText(SignUp.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            LastName.requestFocus();
            showProgressLayout(false);
            return false;
        }
        if (pword.length() < 6) {
            Toast.makeText(SignUp.this, "Password cannot be less than 6 characters", Toast.LENGTH_SHORT).show();
            Password.requestFocus();
            showProgressLayout(false);
            return false;
        }
        return true;
    }

    private void LoadLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            registerLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            registerLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onResponse(String result) {
        Log.d(TAG, result);
        JSONObject json = helper.getJson(result);
        try {
            if (json.getString(helper.REGISTER_RESULT).equals(helper.SUCCESS)) {
                int userid = json.getInt("userid");
                InfoText.setText("Succesfully Created things");
                dbHelper.setUserEmail(EmailID.getText().toString());
                dbHelper.setUserFirstName(FirstName.getText().toString());
                dbHelper.setUserLastName(LastName.getText().toString());
                dbHelper.setUserPassword(Password.getText().toString());
                dbHelper.setUserRole(helper.ROLE_MEMBER);
                dbHelper.setUserID(userid);


                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show();
                //User.Instance().setEmail(EmailID.getText().toString());
                LoadLoginActivity();
            } else if (json.getString(helper.REGISTER_RESULT).equals(helper.USER_EXISTS)) {
                showProgressLayout(false);
                Toast.makeText(this, "There is already a user with that Email address", Toast.LENGTH_LONG).show();
            } else {
                showProgressLayout(false);
                Toast.makeText(this, "Unable to register\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException jse) {
            showProgressLayout(false);
            jse.printStackTrace();
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

    }
}
