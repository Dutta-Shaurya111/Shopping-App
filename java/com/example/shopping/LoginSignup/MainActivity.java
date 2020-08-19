package com.example.shopping.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Adapters.OnBoardSliderAdapter;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Users;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button joinNowbtn,Loginbtn,mBackBtn,mNextBtn;
private ProgressDialog loadingbar;
private ViewPager mSlideViewPager;
private RelativeLayout mdotLayout;
private TextView[] mDots;
    private TextView slogan;
    private ImageView img;
    int mCurrentPage,flag=0;
    private OnBoardSliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // slogan=findViewById(R.id.slogan);
        //img=findViewById(R.id.img);

        loadingbar=new ProgressDialog(this);
        joinNowbtn=findViewById(R.id.main_joinbtn);
        Loginbtn=findViewById(R.id.main_loginbtn);

        mSlideViewPager=findViewById(R.id.onboard_viewpager);
        mdotLayout=findViewById(R.id.linear_dots);
        mBackBtn=findViewById(R.id.onboard_back_btn);
        mNextBtn=findViewById(R.id.onboard_next_btn);

        sliderAdapter = new OnBoardSliderAdapter(getApplicationContext());
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
        Paper.init(this);

        joinNowbtn.setOnClickListener(this);
        Loginbtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        String UserNumberKey=Paper.book().read(Prevalent.UserNumberKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);


       if (UserNumberKey!="" && UserPasswordKey!="")
       {
           if (!TextUtils.isEmpty(UserNumberKey)&& !TextUtils.isEmpty(UserPasswordKey))
           {
               AllowAccess(UserNumberKey,UserPasswordKey);

               loadingbar.setTitle("Already LoggedIn");
               loadingbar.setMessage("Please Wait While We Are Checking The Credentials");
               loadingbar.setCanceledOnTouchOutside(false);
               loadingbar.show();

           }
       }
    }

    public void addDotsIndicator(int position)
    {
        mDots = new TextView[3];

        for (int i=0;i<mDots.length;i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextColor(getResources().getColor(R.color.whitec));
            mDots[i].setTextSize(35);

            mdotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.whitec));
        }
    }

    private void AllowAccess(final String number,final String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(number).exists()) {
                    //Unique Key Used Is Phone Number Of The User
                    Users userData = dataSnapshot.child("Users").child(number).getValue(Users.class);

                    if (userData.getNumber().equals(number)) {
                        if (userData.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Intent intentt = new Intent(MainActivity.this, HomepageActivity.class);
                            // so that app remembers us everytime
                            Prevalent.currentOnlineUser = userData;
                            startActivity(intentt);
                        } else {
                            Toast.makeText(MainActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account With This" + number + "number Does Not Exist...", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "You Need To Create a new Account", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==joinNowbtn){

            Intent in=new Intent(MainActivity.this,Register.class);
            startActivity(in);
        }
        else if (view==Loginbtn){
Intent in=new Intent(MainActivity.this,loginpage.class);
startActivity(in);

        }

        else if (view == mBackBtn)
        {
            mSlideViewPager.setCurrentItem(mCurrentPage-1);
        }

        else if (view == mNextBtn)
        {
            if (flag == 1){
                Loginbtn.requestFocus();
                joinNowbtn.setVisibility(View.VISIBLE);
                Loginbtn.setVisibility(View.VISIBLE);
            }
            mSlideViewPager.setCurrentItem(mCurrentPage+1);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
        addDotsIndicator(position);

        mCurrentPage = position;

        if (position == 0){

            mNextBtn.setEnabled(true);
            mBackBtn.setEnabled(false);
            mBackBtn.setVisibility(View.INVISIBLE);
            flag=0;
            mNextBtn.setText("NEXT");
            mBackBtn.setText("");
        }

        else if (position == mDots.length-1){

            mNextBtn.setEnabled(true);
            mBackBtn.setEnabled(true);
            mBackBtn.setVisibility(View.VISIBLE);

            mNextBtn.setText("FINISH");
            flag=1;
            mBackBtn.setText("BACK");
        }

        else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                flag=0;
                mNextBtn.setText("NEXT");
                mBackBtn.setText("BACK");

        }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
