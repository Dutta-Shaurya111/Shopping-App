package com.example.shopping.Payments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentSDK extends AppCompatActivity {
private TextView amount_ed,receiver_name_ed,UPI_ID_ed;
private EditText note_ed;
private Button payment_COD;
private String paymentMode = "";
private Button sendMoney;
private final int UPI_PAYMENT=0,SEND_SMS_CODE=1;
private String amount,upi_id,note,receiver_name,productId;
private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_sdk);

        ordersRef = FirebaseDatabase.getInstance().getReference();
        productId = getIntent().getStringExtra("productId");

        initialise();
        amount = getIntent().getStringExtra("amount");
        amount_ed.setText("Amount:  "+amount);
       // upi_id = "thukral.saksham132@okaxis";
       // receiver_name = "Saksham Thukral";
        UPI_ID_ed.setText("Receiver Upi-Id:  "+upi_id);
        receiver_name_ed.setText("Receiver Name:  "+receiver_name);

        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // amount = amount_ed.getText().toString();


                note = note_ed.getText().toString();

                if (!validate()) {
                } else {
                    PayUsingUPI(amount, upi_id, receiver_name, note);
                }
            }
        });

        payment_COD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    addCredentialsinDB();
                    sendMessage(Prevalent.currentOnlineUser.getNumber());
                    Toast.makeText(getApplicationContext(), "Your Order has been placed and" +
                            " is Under Review , You will be notified in 2-3 hours .", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
                }
                else {
                    ActivityCompat.requestPermissions(PaymentSDK.this,new String[]{Manifest.permission
                            .SEND_SMS},SEND_SMS_CODE);
                }
            }
        });
    }

    private void addCredentialsinDB()
    {

        HashMap<String,Object> map = new HashMap<>();
        map.put("productId",productId);
        map.put("receivername",receiver_name);
        map.put("amount",amount);
        map.put("transactionStatus","COD");


        ordersRef.child("Order Payments").child(Prevalent.currentOnlineUser.getNumber()).updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void PayUsingUPI(String amount, String upi_id, String receiver_name, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upi_id)
                .appendQueryParameter("pn",receiver_name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR").build();

        Intent upiIntent = new Intent(Intent.ACTION_VIEW);
        upiIntent.setData(uri);

        // it will always show user a dialog from where the user can choose upi App to proceed
        Intent chooser = Intent.createChooser(upiIntent,"Pay with");
        // check if Intent resolves
        if (null != chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);

        }
        else {
            Toast.makeText(getApplicationContext(),"No UPI App Found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case UPI_PAYMENT:
                if (resultCode == RESULT_OK || resultCode==11) {
                    if (data != null) {
                        String txt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + txt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(txt);
                        upiPaymentDataOperation(dataList);
                    }
                    else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                }
                else {
                    // when user simply goes from back from chooser without choosing payment gateway
                    Log.d("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    //D/UPI: onActivityResult: txnId=SBI26c27e05adc34e0eb1868b961c325d3e&responseCode=UP00&Status=SUCCESS&txnRef=019201448899
    //E/UPIPAY: upiPaymentDataOperation: txnId=SBI26c27e05adc34e0eb1868b961c325d3e&responseCode=UP00&Status=SUCCESS&txnRef=019201448899
    //E/UPI: payment successful: 019201448899

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(getApplicationContext())) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("SUCCESS")) {
                //Code to handle successful transaction here.
                Intent transactionIntent = new Intent(getApplicationContext(),TransactionStatus.class);
                transactionIntent.putExtra("amount",amount);
                transactionIntent.putExtra("upiId",upi_id);
                transactionIntent.putExtra("name",receiver_name);
                transactionIntent.putExtra("note",note);
                transactionIntent.putExtra("status",status);
                transactionIntent.putExtra("refNo",approvalRefNo);
                transactionIntent.putExtra("productId",productId);
                startActivity(transactionIntent);

                Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successful: "+approvalRefNo);
                Toast.makeText(getApplicationContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successful: "+status);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);

            }
            else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);

            }
        } else {
            Log.e("UPI", "Internet issue: ");

            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
    private void initialise()
    {
        amount_ed = findViewById(R.id.payment_amount);
        receiver_name_ed = findViewById(R.id.payment_receiver_name);
        note_ed = findViewById(R.id.payment_note);
        UPI_ID_ed = findViewById(R.id.payment_UPI_ID);

        payment_COD = findViewById(R.id.payment_cod_option);
        sendMoney = findViewById(R.id.payment_send);

       // validate();
    }

    private boolean validate()
    {

         if (TextUtils.isEmpty(note_ed.getText().toString().trim())){
            note_ed.setError("Required");
            return false;
        }
     return true;
    }

    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void sendMessage(String number)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,"Your Order has been placed and " +
                        " is Under Review , You will be notified in 2-3 hours ."
                ,null,null);
    }
}
