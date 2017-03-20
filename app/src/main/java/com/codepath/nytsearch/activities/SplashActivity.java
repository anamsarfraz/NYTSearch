package com.codepath.nytsearch.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.util.Constants;
import com.codepath.nytsearch.util.CustomFonts;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        tvAppTitle.setTypeface(
                CustomFonts.getTypeFace(this, Constants.CHELTENHAM_FONT));
        Animation a = AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_center);
        a.reset();
        tvAppTitle.clearAnimation();
        tvAppTitle.startAnimation(a);

        int secondsDelayed = 3;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashActivity.this, SearchActivity.class));
                finish();
            }
        }, secondsDelayed * 1000);
    }
}
