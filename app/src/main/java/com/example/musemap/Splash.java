package com.example.musemap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class Splash extends AppCompatActivity {

    ImageView image_splash;
    TextView text_app_name, text_dev_name;
    Animation top_animation, bottom_animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#333949"));
        actionBar.setBackgroundDrawable(colorDrawable);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        image_splash = findViewById(R.id.image_splash);
        text_app_name = findViewById(R.id.text_app_name);
        text_dev_name = findViewById(R.id.text_dev_name);

        top_animation = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom);
        image_splash.setAnimation(top_animation);
        text_app_name.setAnimation(bottom_animation);
        text_dev_name.setAnimation(bottom_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }
}