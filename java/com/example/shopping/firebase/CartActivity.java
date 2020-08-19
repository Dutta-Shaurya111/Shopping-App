package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Cart;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class CartActivity extends AppCompatActivity
{

private TextView text_total_amt,txtmsg1;
private Button next_btn;
private RecyclerView recyclerView;
private RecyclerView.LayoutManager layoutManager;
private int totalPrice;
private String productId,color="",size="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart2);

        productId = getIntent().getStringExtra("productId");

        color = getIntent().getStringExtra("color");
        size = getIntent().getStringExtra("size");
        initialise();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalPrice > 0) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
                    intent.putExtra("totalprice", String.valueOf(totalPrice));
                    intent.putExtra("productId", productId);
                    intent.putExtra("color", color);
                    intent.putExtra("size", size);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
if (totalPrice > 0)
        next_btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        // creating reference to cartlist node in db to get products details
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");

        // creating firebaseRecyclerOptions and passing cart model class in it
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
        .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                .child("Products") , Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartViewHolder> cartadapter = new
                FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    // on bind view holder runs as many times data is there to get
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                        holder.txtProductQuantity.setText("Quantity is : " + model.getQuantity());
                        holder.txtProductPrice.setText("Price is : " +model.getPrice());
                        holder.txtProductName.setText(model.getPname());

                        Picasso.get().load(model.getPimage()).into(holder.productImg);

                        Log.d("PRICE.......",model.getPrice());
                        Log.d("Quantity.......",model.getQuantity());
                        // getting individual total price of eachitem
                        int oneProductTPrice = ((Integer.valueOf(model.getPrice())))
                                * Integer.valueOf(model.getQuantity());
                        totalPrice+=oneProductTPrice;
                        text_total_amt.setText("Total Price : "+String.valueOf(totalPrice)+" Rs");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence[] options = new CharSequence[]{
                                        "Edit","Delete"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options:")
                                        .setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (i == 0){
                                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                                    // we are sending id from cartactivity to ProductDetailsActivity because it retreives
                                                    // products using id
                                                    intent.putExtra("pid", model.getPid());
                                                    startActivity(intent);
                                                }

                                                else if (i ==1){
                                                    // creating reference to cartlist node in db to get products details
                                                    cartListRef.child("User View")
                                                            .child(Prevalent.currentOnlineUser.getNumber())
                                                            .child("Products").
                                                            child(model.getPid())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                  if (task.isSuccessful()){
                                                                      cartListRef.child("Admin View")
                                                                              .child(Prevalent.currentOnlineUser.getNumber())
                                                                              .child("Products").child(model.getPid())
                                                                              .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              toast("Item Removed successfully");
                                                                              Intent intent = new Intent(CartActivity.this, HomepageActivity.class);
                                                                              startActivity(intent);
                                                                          }
                                                                      });

                                                                  }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // creating view of the layout by inflating it
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout
                        , parent ,false);

                        // creating viewholder which will hold the view
                        CartViewHolder viewHolder = new CartViewHolder(view);

                        return viewHolder;
                    }
                };
        recyclerView.setAdapter(cartadapter);
        cartadapter.startListening();

        if (totalPrice == 0){
            text_total_amt.setText("Cart is Empty!!!!!!!");
            next_btn.setBackgroundColor(getResources().getColor(R.color.onboard_gray));
        }

    }

    // method for adding validation if order has been shipped or not
    private void CheckOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getNumber());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState= dataSnapshot.child("shippingState").getValue().toString();
                    String userName= dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equalsIgnoreCase("shipped")){
                        text_total_amt.setText("Dear " + userName + " ," +"\n" +" Your " +
                                "order has been shipped....You will receive shortly");
                        txtmsg1.setText("Hurrah!!! Your Final Order has been Shipped Successfully..\n" +
                                "Soon You will receive your order at your doorstep");
                    }
                    else if (shippingState.equalsIgnoreCase("not shipped")){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialise()
    {
        text_total_amt = findViewById(R.id.text_total_amt);
        next_btn = findViewById(R.id.button_next);
        txtmsg1 = findViewById(R.id.msg1);
        recyclerView = findViewById(R.id.recycler_view_cart);
    }

    private void toast(String added_to_cart)
    {
        Toast.makeText(getApplicationContext(),added_to_cart,Toast.LENGTH_SHORT).show();
    }

}
