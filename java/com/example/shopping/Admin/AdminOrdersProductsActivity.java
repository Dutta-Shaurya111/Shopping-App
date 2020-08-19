package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shopping.Model.Cart;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminOrdersProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    private DatabaseReference productsRef;
    private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders_products);

        userId = getIntent().getStringExtra("number");

        productsList = findViewById(R.id.orders_list_admin);
        productsList.setHasFixedSize(true);
        productsList.setLayoutManager(new LinearLayoutManager(this));

        productsRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
        .child("Admin View").child(userId).child("Products");



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(productsRef,Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductQuantity.setText("Quantity is : " + model.getQuantity());
                holder.txtProductPrice.setText("Price is : " +model.getPrice());
                holder.txtProductName.setText(model.getPname());

                Picasso.get().load(model.getPimage()).into(holder.productImg);
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

        productsList.setAdapter(adapter);
        adapter.startListening();

    }
}
