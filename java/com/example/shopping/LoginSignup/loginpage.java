package com.example.shopping.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Users;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.example.shopping.Admin.AdminProduct;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class loginpage extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
TextView forgot_password,adminlink,not_adminlink,skiplogin;
EditText Inputphonenumber,Inputpassword;
Button loginbtn,loginOtpbtn;
ImageView img;
private ProgressDialog loadingbar;
private String parentDbName="Users";
String number,password;
private CheckBox remember_me,checkBox;
private LinearLayout linearLayout;
private DatabaseReference RootRef;
private Users userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        checkInternetConnection();

       initialize();

checkBox.setOnCheckedChangeListener(this);

// Paper dependency is included for remembering the user
        Paper.init(this);


        loginbtn.setOnClickListener(this);

       adminlink.setOnClickListener(this);
        not_adminlink.setOnClickListener(this);
        skiplogin.setOnClickListener(this);
        loginOtpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginpage.this,OtpLogin.class);
                startActivity(intent);
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginpage.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
    }

    private void initialize()
    {
        //img=findViewById(R.id.img);
        linearLayout=findViewById(R.id.linear1);
        Inputphonenumber=findViewById(R.id.phonenumber);
        Inputpassword=findViewById(R.id.password);
        forgot_password=findViewById(R.id.forgot_password);
        checkBox=(com.rey.material.widget.CheckBox)findViewById(R.id.checkbox);

        remember_me=(com.rey.material.widget.CheckBox)findViewById(R.id.checkbox1);

        skiplogin= this.<TextView>findViewById(R.id.skip_login);
        adminlink=findViewById(R.id.text_admin);
        not_adminlink=findViewById(R.id.text_notadmin);//TextView
        loginbtn=findViewById(R.id.login_btn);
        loginOtpbtn = findViewById(R.id.login_otp_btn);
        loadingbar=new ProgressDialog(loginpage.this);
    }

    @Override
    public void onClick(View view) {
if (view==loginbtn){
        LoginUser();
    }
    else if (view==adminlink){

        loginbtn.setText("Login As Admin");
        not_adminlink.setVisibility(View.VISIBLE);
        adminlink.setVisibility(View.INVISIBLE);
        parentDbName="Admins";
}
    else if (view==not_adminlink){
        loginbtn.setText("Login");
        adminlink.setVisibility(View.VISIBLE);
        not_adminlink.setVisibility(View.INVISIBLE);
        parentDbName="Users";

}
    else if (view==skiplogin)
    {
        Intent intent=new Intent(loginpage.this, HomepageActivity.class);
        startActivity(intent);
    }}

    public void LoginUser()
    {
         number=Inputphonenumber.getText().toString();
         password=Inputpassword.getText().toString();

         if (TextUtils.isEmpty(number))
         {
             if (number.length()!=10){
                 Inputphonenumber.setError("Enter a valid number");
                 Inputphonenumber.requestFocus();
             }
            Toast.makeText(loginpage.this,"Please Write Your Phone-Number...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Inputpassword.setError("Required Field");
            Inputpassword.requestFocus();
            Toast.makeText(loginpage.this,"Please Write Password...",Toast.LENGTH_SHORT).show();
        }
         else {
             loadingbar.setTitle("Logging In Account");
             loadingbar.setMessage("Please Wait....");
             loadingbar.setCanceledOnTouchOutside(false);
             loadingbar.show();

             AllowAccessToAccount(number,password);
         }
    }


    public void AllowAccessToAccount(final String number, final String password)
    {
        // code for remember me
        if (remember_me.isChecked())
        {
            Paper.book().write(Prevalent.UserNumberKey,number);
            Paper.book().write(Prevalent.UserPasswordKey,password);

        }

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
                        if (userData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(loginpage.this, "Welcome Admin,You Are Logged In Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intentt = new Intent(loginpage.this, AdminProduct.class);
                                startActivity(intentt);
                            }
                            else if (parentDbName.equals("Users")){
                                Toast.makeText(loginpage.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intentt = new Intent(loginpage.this, HomepageActivity.class);
                                Prevalent.currentOnlineUser=userData;
                                startActivity(intentt);
                            }
                        }
                        else {
                            Toast.makeText(loginpage.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                        }
                    }
                } else {
                   // Toast.makeText(loginpage.this, "Account With This" + number + "number Does Not Exist...", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(loginpage.this, "You Need To Create a new Account", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(loginpage.this,Register.class);
                startActivity(intent);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(loginpage.this);
        builder.setTitle("Warning!!!!");
        builder.setIcon(R.drawable.error);
        builder.setMessage("Internet Error!! Please provide an active Active Internet Connection  " +
                "to Continue");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(loginpage.this,MainActivity.class);
                startActivity(intent);
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    // for password visisbility
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b)
        {
            Inputpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
            {
                Inputpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
    }
}