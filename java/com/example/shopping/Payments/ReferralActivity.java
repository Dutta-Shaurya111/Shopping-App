package com.example.shopping.Payments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.HashMap;

public class ReferralActivity extends AppCompatActivity {
private TextView createLink,shareLink,referCode_tv,referLink_tv;
private String referLink;
private String code;
private DatabaseReference referralRef;

    // Base Link: refershopping.page.link
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        referralRef = FirebaseDatabase.getInstance().getReference();

initialise();
        FirebaseApp.initializeApp(this);

createLink.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      //  createLinkMeth(Prevalent.currentOnlineUser.getNumber(),"prod456");
        createInviteCode();
    }
});

shareLink.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
shareInviteCode();
    }
});


    }

    @Override
    protected void onStart() {
        super.onStart();
try {

    referralRef.child("Referrals").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot referShot : dataSnapshot.getChildren()) {
                if (referShot.child("senderno").getValue(String.class).equals(
                        Prevalent.currentOnlineUser.getNumber()
                )) {
                    referCode_tv.setText("Your Referral Code is : " + referShot.child("code").getValue(String.class));

                    createLink.setVisibility(View.INVISIBLE);
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
catch (Exception e){

}
    }

    private void shareInviteCode() {
        Intent appChooserintent = new Intent();
        appChooserintent.setAction(Intent.ACTION_SEND);
        appChooserintent.putExtra(Intent.EXTRA_TEXT,code);
        appChooserintent.setType("text/plain");
        startActivity(appChooserintent);
    }

    private void createInviteCode()
    {
        String num = Prevalent.currentOnlineUser.getNumber();
        String number = Prevalent.currentOnlineUser.getNumber();
        String user = Prevalent.currentOnlineUser.getName();

        int len = user.length();
        user = user.substring(0,5);
        num = num.substring(5,10);

         code = user+num;
/*
        StringBuilder sb = new StringBuilder(code.length());

        for (int i = 0; i < code.length(); i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(code.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(code
                    .charAt(index));
        }
        */

        Log.d("Number is : ",number);
        Log.d("User is : ",user);
        Log.d("Invite Code is : ",code);

        HashMap<String,Object> referMap=new HashMap<>();
        referMap.put("code",code);
        referMap.put("senderno",number);
        referMap.put("status","Unsuccess");

        referralRef.child("Referrals").child(code).updateChildren(referMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    referCode_tv.setVisibility(View.VISIBLE);
                    referCode_tv.setText("Your Refer Code : "+code);
                    toast("Referral Created Successfully");
                }
            }
        });

    }

    private void createLinkMeth(String custId,String prodId)
    {
        Log.d("Main","IN Method");
        /*
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.shoppingapp.com/"))
                .setDynamicLinkDomain("refershopping.page.link")
                //Open links with this App on Android
        .setAndroidParameters(new DynamicLink.AndroidParameters
                .Builder("com.example.shopping").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.d("TAG","Long refer : "+dynamicLinkUri.toString());

*/

        String dynamicLink = "https://refershopping.page.link/?"+
                "link=https://www.shoppingapp.com/myrefer.php?custId="+custId+"-"+prodId+
                "&apn="+getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Reward Cash 100"+
                "&si="+"https://shauryaarts.com/pics/logo.webp";

        // there are 2 parameters in this link:- apn and link

        Log.e("TAG","share Link: "+dynamicLink);
        referLink = dynamicLink.substring(dynamicLink.indexOf("custId"));
        //referLink = dynamicLink.substring(dynamicLink.lastIndexOf("=")+1);
        Log.d("TAG substring",referLink);

         custId = referLink.substring(7,referLink.indexOf("-"));
         prodId = referLink.substring(referLink.indexOf("-")+1,referLink.indexOf("-")+8);

        Log.d("TAG","custId: "+custId);
        Log.d("TAG","productId: "+prodId);



        // shorten dynamic link as it is difficult to display long link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(dynamicLink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.d("Main",shortLink.toString());

                            // flow of link is 1) link 2) playstore and check if app is installed or not
                            // if yes then 3)move to App oterwise install app first

                            Intent appChooserintent = new Intent();
                            appChooserintent.setAction(Intent.ACTION_SEND);
                            appChooserintent.putExtra(Intent.EXTRA_TEXT,shortLink.toString());
                            appChooserintent.setType("text/plain");
                            startActivity(appChooserintent);
                        }
                        else {
                            // Error
                            // ...
                            Toast.makeText(getApplicationContext(),"Error Occured"+task.getException()
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void initialise()
    {
        createLink = findViewById(R.id.create_link);
        shareLink = findViewById(R.id.share_link);
        referCode_tv = findViewById(R.id.refer_code);
        referLink_tv = findViewById(R.id.refer_link);
    }

    private void createReferLink(String custId,String productId){

    }

    private void toast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
