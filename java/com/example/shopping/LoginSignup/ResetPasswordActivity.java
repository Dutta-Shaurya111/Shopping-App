package com.example.shopping.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
private EditText phonenumber,ques1,ques2;
private TextView pageTitle,titleQues;
private Button verifyBtn;


    private String check="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        initialise();


    }

    private void initialise()
    {
        pageTitle = findViewById(R.id.page_title);
        titleQues = findViewById(R.id.title_ques);

        phonenumber = findViewById(R.id.find_phone_number);
        ques1 = findViewById(R.id.ques_1);
        ques2 = findViewById(R.id.ques_2);

        verifyBtn = findViewById(R.id.verify_ques_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        phonenumber.setVisibility(View.GONE);

        if (check.equals("settings")){
            // if user comes from settings activity, then we shall do this and allow user to set his own ques
            pageTitle.setText("Set Your Own Ques");
            titleQues.setText("Please Set Answers for the following Security Ques");

            verifyBtn.setText("Set");


            displayPreviousAnswers();

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                setAnswers();
                }
            });
        }

        else if (check.equals("login")){
            // if users comes here from login Activity
            phonenumber.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyLoginUser();
                }
            });
        }


    }


    private void displayPreviousAnswers(){
        // we shall retreive already saved answers in database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Prevalent.currentOnlineUser.getNumber());

        reference.child("Security Ques").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String ans1= dataSnapshot.child("answer1").getValue().toString();
                    String ans2= dataSnapshot.child("answer2").getValue().toString();

                    ques1.setText(ans1);
                    ques2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAnswers(){
        String ans1 = ques1.getText().toString().toLowerCase();
        String ans2 = ques2.getText().toString().toLowerCase();

        if (ans1.isEmpty()  || ans2.isEmpty()){
            ques2.setError("Please Ans Both Ques");
            toast("Please ans both ques");
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(Prevalent.currentOnlineUser.getNumber());

            // we shall add answers to a hashmap which will further send data to database
            HashMap<String,Object> userDataMap=new HashMap<>();
            userDataMap.put("answer1",ans1);
            userDataMap.put("answer2",ans2);

            reference.child("Security Ques").updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                toast("Answers Saved Successfully");
                                Intent intent = new Intent(ResetPasswordActivity.this, HomepageActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    private void verifyLoginUser()
    {
        final String phonenum = phonenumber.getText().toString();
        final String answer1 = ques1.getText().toString().toLowerCase();
        final String answer2 = ques2.getText().toString().toLowerCase();

        if (!phonenum.equals("") && !answer1.equals("") && !answer2.equals("") && phonenum.length()==10) {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(phonenum);

            // first we shall verify if that phonenumber exists in our db or not
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.hasChild("Security Ques")) {
                            // getting previous answers in db
                            String ans1 = dataSnapshot.child("Security Ques").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Ques").child("answer2").getValue().toString();

                            if (!ans1.equals(answer1)) {
                                ques1.setError("Wrong Ans");
                                toast("First Ans is wrong");
                            } else if (!ans2.equals(answer2)) {
                                ques2.setError("Wrong Ans");
                                toast("Second Ans is wrong");
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("New Password");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (!newPassword.getText().toString().equals("")) {
                                            reference.child("password").setValue(newPassword.getText()
                                                    .toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    toast("Password Changed Successfully");
                                                    Intent intent = new Intent(ResetPasswordActivity.this,
                                                            loginpage.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            toast("Please Write Password");
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                     dialogInterface.cancel();
                                    }
                                });
                                builder.show();
                            }

                        } else {
                            toast("You have not set Security Questions");
                        }
                    }
                    else {
                        toast("Phone number does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            toast("Fill All Fields First");
        }
    }

    private void toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();

    }
}
