package com.example.shopping.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopping.R;

public class ActivitySplash extends AppCompatActivity
{
    private TextView text1,text2;
    private ImageView imgDeliveryMan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        text1 = findViewById(R.id.tv1);
        text2 = findViewById(R.id.tv2);
        imgDeliveryMan = findViewById(R.id.delivery_img);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEnterAnim();
            }
        },3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startExitAnim();
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActivitySplash.this,MainActivity.class));
                finish();
            }
        },4000);
    }

    private void startExitAnim()
    {
        imgDeliveryMan.startAnimation(AnimationUtils.loadAnimation(this,R.anim.image_out));
        text1.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_out));
        text2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_out));
        imgDeliveryMan.setVisibility(View.INVISIBLE);
        text1.setVisibility(View.INVISIBLE);
        text2.setVisibility(View.INVISIBLE);
    }

    private void startEnterAnim()
    {
        imgDeliveryMan.startAnimation(AnimationUtils.loadAnimation(this,R.anim.image_in));
        text1.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_in));
        text2.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_in));

        imgDeliveryMan.setVisibility(View.VISIBLE);
        text1.setVisibility(View.VISIBLE);
        text2.setVisibility(View.VISIBLE);
    }
}

