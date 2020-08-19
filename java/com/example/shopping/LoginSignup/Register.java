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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ImageView img;
    private EditText register_phone,register_password,register_name,register_invite_code;
    private Button createacc_btn,login_btn,check_invite_code;
    private TextView already_done,have_invite_code;
    private CheckBox register_checkbox,password_checkbox;
    private ProgressDialog loadingbar;
    private int flag=0;
    private String invite="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();

        checkInternetConnection();

        createacc_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        have_invite_code.setOnClickListener(this);
        already_done.setOnClickListener(this);
        password_checkbox.setOnCheckedChangeListener(this);
        check_invite_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String invitecode = register_invite_code.getText().toString();
                if (invitecode.isEmpty()){
                    register_invite_code.setError("Code Required");
                    register_invite_code.requestFocus();
                }

            }
        });

    }


    private void initialize()
    {
        register_phone=findViewById(R.id.register_phonenumber);
        register_password=findViewById(R.id.register_password);
        register_name=findViewById(R.id.name);
        already_done=findViewById(R.id.alreadydone);
        img=findViewById(R.id.register_app_logo);
        have_invite_code = findViewById(R.id.have_invite);
        register_invite_code = findViewById(R.id.invite_code);
        check_invite_code = findViewById(R.id.register_check_invite_code);

        register_checkbox=findViewById(R.id.register_checkbox);
        password_checkbox=findViewById(R.id.password_checkbox);

        createacc_btn=findViewById(R.id.register_register_btn);
        login_btn=findViewById(R.id.register_login_btn);

        loadingbar=new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        if (view==createacc_btn){
            createAccount();

        }
        else if (view==login_btn){
startActivity(new Intent(Register.this,loginpage.class));
        }
        else if (view==already_done){
            createacc_btn.setText("Register for admin");


        }
        else if (view == have_invite_code){
            register_invite_code.setVisibility(View.VISIBLE);
            if (register_invite_code.getText().toString().isEmpty()){
                register_invite_code.setError("Empty Code");
                register_invite_code.requestFocus();
                flag =1;
            }
            else {
                flag=1;
            }
        }
    }


    public void createAccount(){
        String name=register_name.getText().toString();
        String number=register_phone.getText().toString();
        String password=register_password.getText().toString();
        String invitecode = register_invite_code.getText().toString();

        if (TextUtils.isEmpty(name)){
            register_name.setError("Required");
            register_name.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(number)){
            register_phone.setError("Required");
            register_phone.requestFocus();
            return;
              }

        else if (number.length()!=10){
            register_phone.setError("Enter a valid mobile Number");
            register_phone.requestFocus();
            return;
        }
        else if (TextUtils.isEmpty(password)){
            register_password.setError("Required");
            register_password.requestFocus();
            return;
        }
        else if (password.length()<8){
            register_password.setError("Password Too Weak");
            register_password.requestFocus();
            return;
        }

        else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please Wait While We Are Checking The Credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            if (flag == 1) {
                Log.d("what u", "will run or not");



            } else {
                Log.d("It come","");
                ValidatephoneNumber(name, number, password,invite);
            }
        }
    }


    private void ValidatephoneNumber(final String name, final String number, final String password,final String invite) {

        final DatabaseReference Rootref;
        Rootref= FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
// if phone number doesnt exist in database , create new user
                if (!(dataSnapshot.child("Users").child(number).exists())) {
// hashmap will store values
                    HashMap<String,Object> userDataMap=new HashMap<>();
                    userDataMap.put("number",number);
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);
                    userDataMap.put("invite",invite);

                    Rootref.child("Users").child(number).updateChildren(userDataMap)
                            // update children is used to put values in database
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(Register.this, "Congrats!!Your Account Has Been Created", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();

                                       // Intent in=new Intent(Register.this,loginpage.class);
                                        //startActivity(in);
                                    }
                                    else {
                                        loadingbar.dismiss();
                                        Toast.makeText(Register.this, "Some Error Occured!!Please Try Again...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(Register.this, "Account with number:- "+number+" already exists", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(Register.this, "Please Try Again Using Other Phone-Number", Toast.LENGTH_SHORT).show();

                    Intent in=new Intent(Register.this,MainActivity.class);
                    startActivity(in);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle("Warning!!!!");
        builder.setIcon(R.drawable.error);
        builder.setMessage("Internet Error!! Please provide an active Active Internet Connection  " +
                "to Continue");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Register.this,MainActivity.class);
                startActivity(intent);
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b)
        {
            register_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else
        {
            register_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
