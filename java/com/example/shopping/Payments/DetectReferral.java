package com.example.shopping.Payments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.HashMap;

public class DetectReferral extends AppCompatActivity {

    private TextView welcome;
    private Button create_link_btn,apply_btn;
    private EditText invite_code;
    private ProgressDialog loadingbar;
    private DatabaseReference referRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_referral);

       initialise();

       referRef = FirebaseDatabase.getInstance().getReference()
                .child("Referrals");

        create_link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetectReferral.this,ReferralActivity.class);
                startActivity(intent);
            }
        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.d("my Refer Link","my Refer Link"+deepLink.toString());

                            // Suppose the referral link is of this type
                            // https://www.shoppingapp.com/myrefer.php?custId=cust123+prod456
                            // getting deep link in referLink using substring
                            String referLink =deepLink.toString();
                            try {
                            referLink = referLink.substring(referLink.lastIndexOf("=")+1);
                            Log.d("TAG substring",referLink);

                                String custId = referLink.substring(0,referLink.indexOf("-"));
                                String prodId = referLink.substring(referLink.indexOf("-")+1);

                                Log.d("TAG","custId: "+custId);
                                Log.d("TAG","productId: "+prodId);
                            }
                            catch (Exception e){
                                Log.d("Error Occured: ",e.toString());
                            }
                        }
                        else {
                            Log.d("Some Error Occured","Some Error Occured");
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Detect", "getDynamicLink:onFailure", e);
                    }
                });

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String code = invite_code.getText().toString();

                if (code.isEmpty()){
                    invite_code.setError("Empty");
                    invite_code.requestFocus();
                }
                else {
                    Log.d("Button clicked ","ssssssssssssssssss");
                    Log.d("Code is : ",code);
                    Log.d("in here elese","");
                   final DatabaseReference useref;
                   try {

                       useref = FirebaseDatabase.getInstance().getReference().child("Users");
                       useref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if (dataSnapshot.child(Prevalent.currentOnlineUser.getNumber())
                                       .child("invite").getValue(String.class).equals("")) {
                                   referRef.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                           if (dataSnapshot1.child(code).exists() && dataSnapshot1.child(code)
                                                   .child("status").getValue(String.class).equals("Unsuccess")) {
                                               Log.d("Value : ", code);
                                               Log.d("Value : ", code);
                                               Log.d("Value : ", dataSnapshot1.child(code).child("senderno")
                                                       .getValue(String.class));
                                               Log.d("Value : ", Prevalent.currentOnlineUser.getNumber());

                                               HashMap<String, Object> referMap = new HashMap<>();
                                               referMap.put("code", code);
                                               referMap.put("senderno", dataSnapshot1.child(code).child("senderno")
                                                       .getValue(String.class));
                                               referMap.put("receiverno", Prevalent.currentOnlineUser.getNumber());
                                               referMap.put("status", "Success");

                                               referRef.child(code).updateChildren(referMap)
                                                       // update children is used to put values in database
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if (task.isSuccessful()) {
                                                                   Log.d("Successful  ", "Same here as well");
                                                                   Toast.makeText(DetectReferral.this, "Congrats!! Referral Successful", Toast.LENGTH_SHORT).show();
                                                                   loadingbar.dismiss();

                                                                   showReferAlertDialog();

                                                               } else {
                                                                   loadingbar.dismiss();
                                                                   Log.d("Error", "Error");
                                                                   Toast.makeText(DetectReferral.this, "Some Error Occured!!Please Try Again...", Toast.LENGTH_SHORT).show();
                                                               }
                                                           }
                                                       });
                                           } else {
                                               Toast.makeText(DetectReferral.this, "Please provide correct Referral Code May be the code is already used", Toast.LENGTH_SHORT).show();
                                               loadingbar.dismiss();

                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {
                                           Log.d("Some Error Occured: ", "Some Error Occured: ");
                                       }
                                   });
                               } else {
                                   toast("User already referred");
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }
                   catch (Exception e){
                       toast(e.getMessage());
                   }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void showReferAlertDialog(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.alertdialog,null);

        Button acceptBtn = view.findViewById(R.id.btn_accept);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(DetectReferral.this,ReferralActivity.class);
                startActivity(intent);
                */
                AcceptMoneyPendingInWallet();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();
    }

    private void AcceptMoneyPendingInWallet()
    {
        final DatabaseReference useref;
        useref= FirebaseDatabase.getInstance().getReference().child("Users");

        final DatabaseReference referRef = FirebaseDatabase.getInstance().getReference()
                .child("Referrals");
        Log.d("in On Start","");
        referRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Log.d("running infinite","yesssssssssss");
                    if ((dataSnapshot1.child("status").getValue(String.class).equals("Success"))
                            && dataSnapshot1.child("receiverno").getValue(String.class).equals(
                            Prevalent.currentOnlineUser.getNumber()
                    )){

                        final DatabaseReference walletref = FirebaseDatabase.getInstance()
                                .getReference().child("Wallet");

                        walletref.child(dataSnapshot1.child("receiverno").getValue(String.class)
                        ).child("money").addValueEventListener(new ValueEventListener() {
                            int x=0;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && x==0){
                                    Log.d("aaaaaaaaa","aaaaaaaaaaaaaaaa");
                                    if ((dataSnapshot1.child("status").getValue(String.class).equals("Success")) && x==0) {
                                        Log.d("bbbbbbbbbb","bbbbbbbbbbbbb");
                                        String existing_money = dataSnapshot.child("money").getValue(String.class);

                                        HashMap<String, Object> map = new HashMap<>();
                                        int finalmoney = Integer.parseInt(existing_money) + 100;
                                        map.put("money", String.valueOf(finalmoney));

                                        x++;
                                        walletref.child(dataSnapshot1.child("receiverno").getValue(String.class)
                                        ).child("money").updateChildren(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Money receiver", "\"Money Credited  Receiver Wallet Successfully\"");
                                                            toast("Money Credited in Receiver Wallet Successfully");
                                                        } else {
                                                            toast("Some Error Occured");
                                                        }
                                                    }
                                                });
                                    }

                                }
                                else {
                                    // means if the sender's wallet does not exist before
                                    // creating hashmap for wallet to store values ie money
                                    if (x == 0) {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("money", "100");

                                        // code for updating 70 Rs in wallet of sender
                                        walletref.child(dataSnapshot1.child("receiverno").getValue(String.class))
                                                .child("money")
                                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Money receiver", "\"Money Credited  Receiver Wallet Successfully\"");
                                                    toast("Money Credited in Wallet Successfully");
                                                } else {
                                                    toast("Some Error Occured");
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child("Wallet");

                        ref.child(dataSnapshot1.child("senderno").getValue(String.class)
                        ).child("money").addValueEventListener(new ValueEventListener() {
                            int i=0;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && i==0){
                                    Log.d("aaaaaaaaa","aaaaaaaaaaaaaaaa");
                                    if ((dataSnapshot1.child("status").getValue(String.class).equals("Success")) && i==0) {
                                        Log.d("bbbbbbbbbb","bbbbbbbbbbbbb");
                                        String existing_money = dataSnapshot.child("money").getValue(String.class);

                                        HashMap<String, Object> map = new HashMap<>();
                                        int finalmoney = Integer.parseInt(existing_money) + 70;
                                        map.put("money", String.valueOf(finalmoney));

                                        i++;
                                        ref.child(dataSnapshot1.child("senderno").getValue(String.class)
                                        ).child("money").updateChildren(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("Money receiver", "\"Money Credited  Sender Wallet Successfully\"");
                                                            toast("Money Credited in Sender Wallet Successfully");
                                                        } else {
                                                            toast("Some Error Occured");
                                                        }
                                                    }
                                                });
                                    }

                                }
                                else {
                                    // means if the sender's wallet does not exist before
                                    // creating hashmap for wallet to store values ie money
                                    if (i == 0) {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("money", "70");

                                        // code for updating 70 Rs in wallet of sender
                                        walletref.child(dataSnapshot1.child("senderno").getValue(String.class))
                                                .child("money")
                                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Money Sender", "\"Money Credited  Sender Wallet Successfully\"");
                                                    toast("Money Credited in Wallet Successfully");
                                                } else {
                                                    toast("Some Error Occured");
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("Referrals");
                        reference.addValueEventListener(new ValueEventListener() {
                            String code;
                            @Override
                            @Exclude
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    if (ds.getKey().substring(5,10).equals(walletref.child("senderno")))
                                     code=ds.getKey();
                                    Log.d("sender no is0 ",ds.getKey().substring(5,10));

                                    HashMap<String,Object> referM=new HashMap<>();
                                    Log.d("code is ",ds.getKey());
                                   // referM.put("code",code);
                                   // referM.put("senderno",walletref.child("senderno"));
                                   // referM.put("receiverno",Prevalent.currentOnlineUser.getNumber());
                                    referM.put("status","Done");

                                    reference.child(ds.getKey()).updateChildren(referM).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Log.d("Successful  ","Same here as well");
                                                loadingbar.dismiss();

                                            }
                                            else {
                                                loadingbar.dismiss();
                                                Log.d("Error","Error"  );
                                                 }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        HashMap<String,Object> usermap = new HashMap<>();
                        usermap.put("invite","Successful");
                        useref.child(Prevalent.currentOnlineUser.getNumber())
                                .updateChildren(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Log.d("Users data changed","ddddd");
                               }
                            }
                        });

                        finish();
                        invite_code.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void initialise()
    {
        welcome = findViewById(R.id.welcome_link);
        create_link_btn = findViewById(R.id.create_link_btn);
        apply_btn = findViewById(R.id.apply);
        invite_code = findViewById(R.id.invite_code_ed);

        loadingbar = new ProgressDialog(this);
    }
}
