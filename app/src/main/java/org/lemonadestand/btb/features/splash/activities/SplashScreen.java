package org.lemonadestand.btb.features.splash.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import org.lemonadestand.btb.R;
import org.lemonadestand.btb.utils.Utils;
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity;
import org.lemonadestand.btb.features.login.activities.LoginActivity;
import org.lemonadestand.btb.singleton.Singleton;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new Handler().postDelayed(() -> {

            if (Utils.getData(this, Utils.TOKEN) == null) {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                if (Utils.getData(this, Utils.TOKEN).length() >= 5) {
                    Log.e("token=>",Utils.getData(this, Utils.TOKEN));
                    Singleton.INSTANCE.setAuthToken(Utils.getData(this, Utils.TOKEN));
                    Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 2000);
    }
}