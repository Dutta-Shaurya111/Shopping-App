package com.example.shopping.Payments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TransactionStatus extends AppCompatActivity {
    private TextView amount_tv,receiver_name_tv,UPI_ID_tv,note_tv,transactionID_tv,transaction_status_tv;
    private String amount,note,name,transactionId,transactionStatus,upiId,productId;
    private DatabaseReference ordersRef,productRef;
    private final int SEND_SMS_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);

        ordersRef = FirebaseDatabase.getInstance().getReference();
        productRef = FirebaseDatabase.getInstance().getReference();

        productId = getIntent().getStringExtra("productId");
        Log.d("productId : ",productId);
        initialise();

        amount = getIntent().getStringExtra("amount");
         upiId= getIntent().getStringExtra("upiId");
        note = getIntent().getStringExtra("note");
        name = getIntent().getStringExtra("name");
        transactionStatus = getIntent().getStringExtra("status");
        transactionId = getIntent().getStringExtra("refNo");

        amount_tv.setText(amount);
        receiver_name_tv.setText(name);
        transaction_status_tv.setText(transactionStatus);
        transactionID_tv.setText(transactionId);
        note_tv.setText(note);
        UPI_ID_tv.setText(upiId);

        if (transactionStatus.toLowerCase() == "Successful".toLowerCase()){
            addCredentialsinDB();
            if (checkPermission(Manifest.permission.SEND_SMS)){
                sendMessage(Prevalent.currentOnlineUser.getNumber());
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission
                .SEND_SMS},SEND_SMS_CODE);
            }


            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void addCredentialsinDB()
    {

        HashMap<String,Object> map = new HashMap<>();
        map.put("productId",productId);
        map.put("receivername",name);
        map.put("amount",amount);
        map.put("transactionStatus",transactionStatus);
        map.put("transactionId",transactionId);
        map.put("note",note);
        map.put("upiId",upiId);

        ordersRef.child("Order Payments").child(Prevalent.currentOnlineUser.getNumber()).updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Your Order has been placed and" +
                                    " is Under Review , You will be notified in 2-3 hours .",Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void sendMessage(String number)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,"Order Successfully placed for Amount worth "+
                amount+" Rs . Track your Order in MyOrders Tab in Application. Expect delivery in 5 days. after dispatch"
        ,null,null);
    }

    private void initialise()
    {
        amount_tv = findViewById(R.id.payment_amount);
        receiver_name_tv = findViewById(R.id.payment_receiver_name);
        note_tv = findViewById(R.id.payment_note);
        UPI_ID_tv = findViewById(R.id.payment_UPI_ID);
        transactionID_tv = findViewById(R.id.transaction_id);
        transaction_status_tv = findViewById(R.id.transaction_status);


    }
}
