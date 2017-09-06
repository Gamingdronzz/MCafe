package com.mcafeweb;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcafeweb.utils.DBHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    PreferencesManager preferencesManager;
    NetworkManager networkManager;
    private final String TAG = "Splash Screen";
    ImageView logo;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = (ImageView) findViewById(R.id.SplashScreen_Logo);
        preferencesManager = new PreferencesManager(getApplicationContext());
        networkManager = new NetworkManager(getApplicationContext());
        dbHelper = new DBHelper(this);
        ShowNextActivity();
    }

    private void ShowNextActivity() {
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LoadNextActivity();
            }
        }, 1000);
        */

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadNextActivity();
                    }
                },
                1000
        );
    }

    private void LoadNextActivity() {
        if (networkManager.isNetworkConnected()) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


}
