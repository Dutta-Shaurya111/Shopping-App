package com.example.shopping.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Users;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpLogin extends AppCompatActivity {
    private TextView tv;
    private ImageView logo;
    private EditText editText;
    private Button continue_btn;
    private DatabaseReference RootRef;
    private String parentDbName = "Users";
    private Users userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);

        checkInternetConnection();

        logo=findViewById(R.id.logo);
        tv=findViewById(R.id.tv);
        editText=findViewById(R.id.phoneno);
        continue_btn=findViewById(R.id.btn_continue);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code="+91";

                String number=editText.getText().toString().trim();
                if (number.isEmpty() || number.length()<10){
                    editText.setError("Valid Number Is Required");
                    //editText.requestFocus();
                    return;

                }
                else {

                    AllowAccessToAccount(number);
                }
            }
        });


    }


    public void AllowAccessToAccount(final String number)
    {
        final String phoneno = "+91" + number;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(number).exists())
                {
                    //Unique Key Used Is Phone Number Of The User
                    userData = dataSnapshot.child(parentDbName).child(number).getValue(Users.class);

                    if (userData.getNumber().equals(number))
                    {
                            if (parentDbName.equals("Users")){

                                Intent intentt = new Intent(OtpLogin.this, VerifyPhoneActivity.class);
                                intentt.putExtra("PhoneNumber",phoneno);
                                Prevalent.currentOnlineUser=userData;
                                startActivity(intentt);
                            }
                        else {
                            Toast.makeText(OtpLogin.this, "User is not registered , you need " +
                                            "signup first", Toast.LENGTH_SHORT).show();
                           // loadingbar.dismiss();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            opendialog();
        }

    }
    private void opendialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Warning!!!!");
        builder.setMessage("Internet Error!! Please provide an active Active Internet Connection  " +
                "to Continue");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(OtpLogin.this,MainActivity.class);
                startActivity(intent);
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent=new Intent(OtpLogin.this, HomepageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

