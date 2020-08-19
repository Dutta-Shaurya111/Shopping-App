package com.example.shopping.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shopping.Fragments.ProductDetailsFragments.FragmentDetails;
import com.example.shopping.Fragments.ProductDetailsFragments.FragmentOverview;
import com.example.shopping.Fragments.ProductDetailsFragments.FragmentReview;
import com.example.shopping.Fragments.ProductDetailsFragments.ViewPagerAdapter;
import com.example.shopping.Fragments.ProductDetailsFragments.helpFragments.FragmentAbout;
import com.example.shopping.Fragments.ProductDetailsFragments.helpFragments.FragmentFaqs;
import com.example.shopping.R;
import com.google.android.material.tabs.TabLayout;

public class HelpActivity extends AppCompatActivity {

    private Button call_btn,email_btn;
    private TextView fraud_tv,info_tv;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initialise();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // adding fragments to adapter

        adapter.AddFragment(new FragmentAbout(),"About");

        adapter.AddFragment(new FragmentFaqs(),"FAQS");

        // adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setAction(Intent.EXTRA_EMAIL);
                startActivity(intent);
            }
        });
    }

    private void initialise()
    {
        call_btn = findViewById(R.id.help_contact_number);
        email_btn = findViewById(R.id.help_email_add);
        fraud_tv = findViewById(R.id.help_fraud_heading);
        info_tv = findViewById(R.id.help_call_heading);
        viewPager = findViewById(R.id.help_viewpager);
        tabLayout = findViewById(R.id.help_tab_layout);
    }
}
