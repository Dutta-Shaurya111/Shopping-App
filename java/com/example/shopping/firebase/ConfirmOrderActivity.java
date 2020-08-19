package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Payments.PaymentSDK;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {
private EditText ed_name,ed_number,ed_city,ed_address,ed_alternate_Number;
private Button confirm_btn;
private String totalPrice,productId,color="",size="";
private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        productId = getIntent().getStringExtra("productId");
        initialize();

        size = getIntent().getStringExtra("size");
        color = getIntent().getStringExtra("color");

        totalPrice = getIntent().getStringExtra("totalprice");
        Toast.makeText(getApplicationContext(),"Total Price : "+totalPrice
                +" Rs",Toast.LENGTH_SHORT).show();

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            checkFields();
            }
        });
    }

    private void checkFields()
    {
       String number=ed_number.getText().toString();
        String alternatenumber=ed_alternate_Number.getText().toString();
        String name=ed_name.getText().toString();
        String city=ed_city.getText().toString();
        String address=ed_address.getText().toString();

        if (TextUtils.isEmpty(number))
        {
            if (number.length()!=10){
                ed_number.setError("Enter a valid number");
                ed_number.requestFocus();
            }
            Toast.makeText(ConfirmOrderActivity.this,"Please Enter Your Phone-Number...",Toast.LENGTH_SHORT).show();
        }
        else   if (TextUtils.isEmpty(alternatenumber))
        {
            if (number.length()!=10){
                ed_alternate_Number.setError("Enter a valid number");
                ed_alternate_Number.requestFocus();
            }
            Toast.makeText(ConfirmOrderActivity.this,"Please Enter Alternate Phone-Number...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(name))
        {
            ed_name.setError("Required Field");
            ed_name.requestFocus();
            Toast.makeText(ConfirmOrderActivity.this,"Please Enter Customer Name...",Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(city))
        {
            if (number.length()!=10){
                ed_city.setError("Enter a valid number");
                ed_city.requestFocus();
            }
            Toast.makeText(ConfirmOrderActivity.this,"Please Enter Your City...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address))
        {
            ed_address.setError("Required Field");
            ed_address.requestFocus();
            Toast.makeText(ConfirmOrderActivity.this,"Please Enter your Address...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Placing Your Order");
            loadingbar.setMessage("Please Wait....");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            ConfirmOrder();
        }
    }

    private void initialize()
    {
        ed_name = findViewById(R.id.shipment_name);
        ed_alternate_Number = findViewById(R.id.shipment_alternatephone);
        ed_number = findViewById(R.id.shipment_phone);
        ed_address = findViewById(R.id.shipment_address);
        ed_city = findViewById(R.id.shipment_city);
        confirm_btn = findViewById(R.id.confirm_order);

        loadingbar = new ProgressDialog(ConfirmOrderActivity.this);
    }

    private void ConfirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        // using calender to get time and date at which user trying to buy product
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currDate.format(calForDate.getTime());

        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currTime.format(calForDate.getTime());

        try {

            final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders").child(Prevalent.currentOnlineUser.getNumber());


            // using hashmap to store all shipping details of user to be put into database
            HashMap<String, Object> map = new HashMap<>();
            map.put("totalAmount", totalPrice);
            map.put("name", ed_name.getText().toString());
            map.put("phone", ed_number.getText().toString());
            map.put("alternatephone", ed_alternate_Number.getText().toString());
            map.put("date", saveCurrentDate);
            map.put("time", saveCurrentTime);
            map.put("city", ed_city.getText().toString());
            map.put("address", ed_address.getText().toString());
            map.put("shippingState", "not shipped");
            map.put("color",color);
            map.put("size",size);

            orderRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                                .child(Prevalent.currentOnlineUser.getNumber()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            loadingbar.dismiss();
                                            Toast.makeText(getApplicationContext(), "Please Proceed For Payment", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(getApplicationContext(), PaymentSDK.class);
                                            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("amount", totalPrice);
                                            intent.putExtra("productId", productId);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            });
        }
    catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
    }
    }

}
