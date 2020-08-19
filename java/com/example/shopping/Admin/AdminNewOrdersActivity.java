package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shopping.Model.AdminOrders;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private String userType="Admin";
    private int SEND_SMS_CODE=1;
    private RequestQueue requestQueue;
    private ArrayList<String> cities_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        cities_list = new ArrayList<>();



        if (getIntent().getStringExtra("User") != null){
            userType = "User";
        }

        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_list);
        ordersList.setHasFixedSize(true);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    void json(){

        requestQueue = Volley.newRequestQueue(this);

        String url = "http://www.json-generator.com/api/json/get/bUqVBMBTyq?indent=2";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("cities");

                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject cities = jsonArray.getJSONObject(i);
                                String city = cities.getString("District");
                                cities_list.add(city);

                                Log.d("", city+ ",");
                            }
                            Log.d("Total Size Value is:  ", cities_list.size()+"");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Occured :  " ,error.getMessage());
            }
        });

        requestQueue.add(request);

    }


    @Override
    protected void onStart() {
        super.onStart();

        json();

        FirebaseRecyclerOptions<AdminOrders> options = new
                FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                holder.userName.setText("Name: "+model.getName());
                holder.userNumber.setText("Phone Number: "+model.getPhone());

                holder.userShippingState.setText("Shipping State: "+model.getShippingState());
                holder.userDateTime.setText("Ordered At: "+model.getDate()+"  "+model.getTime());
                holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());
                holder.userTotalPrice.setText("Total Amount: "+model.getTotalAmount()+" Rs");

                holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // getting unique key of the product from database using position
                        String uId = getRef(position).getKey();

                        Intent intent = new Intent(AdminNewOrdersActivity.this,AdminOrdersProductsActivity.class);
                        intent.putExtra("number",uId);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // creating options to be displayed in dialog box

                            CharSequence options[] = new CharSequence[]{
                                    "Yes",
                                    "No",
                                    "Change Shipping State"
                            };
                        if (userType == "Admin") {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                            builder.setTitle("Have you shipped this order's products ??");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        // If yes, then we shall delete products that have already been shipped
                                        // from the db
                                        // getting unique key of the product from database using position
                                        String uId = getRef(position).getKey();

                                        removeOrder(uId);
                                    } else if (i == 1) {
                                        finish();
                                    }
                                    else if (i == 2){
                                        changeShippingState();
                                    }
                                }
                            });
                            builder.show();
                        }
                        else {
                            // if the user comes to this activity
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Do You Want To Cancel This Order's Products ??");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            // If yes, then we shall delete products that have already been shipped
                                            // from the db
                                            // getting unique key of the product from database using position
                                            String uId = getRef(position).getKey();

                                            showReferAlertDialog(uId,holder.userName.getText()
                                                    .toString(),holder.userNumber.getText().toString(),holder
                                            .userTotalPrice.getText().toString(),holder.userShippingAddress.getText().toString());
                                            //removeOrder(uId);
                                            //toast("Products deleted Successfully");
                                        } else if (i == 1) {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();

                        }
                    }
                });

            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_orders_layout
                ,parent,false);
                AdminOrdersViewHolder holder = new AdminOrdersViewHolder(view);
                return holder;
            }
        };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeShippingState()
    {
                ordersRef.child(Prevalent.currentOnlineUser.getNumber())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState= dataSnapshot.child("shippingState").getValue().toString();
                    if (shippingState.equalsIgnoreCase("shipped")){
                        ordersRef.child(Prevalent.currentOnlineUser.getNumber())
                                .child("shippingState").setValue("success");
                        toast("Shipping State Changed");
                    }
                    else if (shippingState.equalsIgnoreCase("not shipped")){
                        ordersRef.child(Prevalent.currentOnlineUser.getNumber())
                                .child("shippingState").setValue("shipped");
                        toast("Shipping State Changed");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void showReferAlertDialog(final String UId, final String name, final String number, final String totalPrice, final String shippingAddress){

        final ArrayList<String> listOfReasons = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.alertdialogcancel,null);

        final EditText editText = view.findViewById(R.id.ed_reason_cancel);
        final EditText editText_reason = view.findViewById(R.id.ed_reason_user);
         TextView heading = view.findViewById(R.id.cancellation_heading);
        final TextView cancelText = view.findViewById(R.id.reason_tv);
        final Spinner spinner = view.findViewById(R.id.autotv);
        Button acceptBtn = view.findViewById(R.id.btn_cancel_shipment);
        Button cancelBtn = view.findViewById(R.id.btn_exit);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.reasons,R.layout.coloured_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0) {
                cancelText.setText(parent.getItemAtPosition(position).toString());
                if (position == 1){
                    listOfReasons.clear();
                    editText.setVisibility(View.VISIBLE);
                    editText_reason.setVisibility(View.GONE);
                    listOfReasons.add(parent.getItemAtPosition(position).toString());
                }
                else if (position == 5){
                    editText_reason.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    listOfReasons.clear();
                    listOfReasons.add(parent.getItemAtPosition(position).toString());
                }
                else {
                    listOfReasons.clear();
                    editText_reason.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    listOfReasons.add(parent.getItemAtPosition(position).toString());
                }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Please select any One",Toast.LENGTH_LONG).show();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(DetectReferral.this,ReferralActivity.class);
                startActivity(intent);
                */
                if ( !listOfReasons.get(0).isEmpty()) {
                    toast("Requested Product Cancellation");
                    if (checkPermission(Manifest.permission.SEND_SMS)) {
                        toast("Requested Product Cancellation");
                        sendMessage(UId, name, number, totalPrice, shippingAddress, listOfReasons.get(0));
                    } else {
                        ActivityCompat.requestPermissions(AdminNewOrdersActivity.this, new String[]{Manifest.permission
                                .SEND_SMS}, SEND_SMS_CODE);
                    }
                    removeOrder(UId);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();
    }

    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void sendMessage(String UId,String name,String number,String amount, String shippingAddress,String reason)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("9877388119",null,"Order Cancellation request Submitted for Amount "+
                        amount+" Rs . by Name: "+name+" , UID: "+UId+" , Contact No. "+number+" , Shipping Address was: "
                +shippingAddress+" Reason For cancellation given is: "+ reason+". Get in touch with them Shortly",null,null);

        smsManager.sendTextMessage(number,null,"Our Customer Care executive shall shortly get in" +
                "touch with you to discuss about cancellation. For other help , you can " +
                "contact us in Customer-Service panel in Application. Thank You!!!",null,null);
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userName,userNumber,userTotalPrice,userShippingAddress,userDateTime
                ,userShippingState;
        public Button showOrdersBtn;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            userShippingState = itemView.findViewById(R.id.order_shipping_state);
            userDateTime = itemView.findViewById(R.id.order_date_time);

            showOrdersBtn = itemView.findViewById(R.id.show_all_orders);

        }

    }

    private void removeOrder(String uId)
    {
    ordersRef.child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          if (task.isSuccessful()){
              toast("Order has been removed successfully");
          }
          else {
              toast("Please try again");
          }
        }
    });
    }

    private void toast(String message){
        Toast.makeText(AdminNewOrdersActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
