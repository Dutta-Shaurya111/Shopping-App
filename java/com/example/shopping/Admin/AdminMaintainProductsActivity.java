package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
private EditText name_maintain,price_maintain,description_maintain;
private ImageView image_maintain;
private Button applyChanges,deleteProduct;
private String productId="";
private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productId = getIntent().getStringExtra("pid");

        reference = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);
        initialise();

        displaySpecificProductInfo();

        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String name = name_maintain.getText().toString();
                String price = price_maintain.getText().toString();
                String description = description_maintain.getText().toString();

                if (name.isEmpty()){
                    name_maintain.setError("Required");
                }
                else if (price.isEmpty()){
                    price_maintain.setError("Required");
                }
                else if (description.isEmpty()){
                    description_maintain.setError("Required");
                }
                else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("pid",productId);
                    map.put("name",name);
                    map.put("price",Integer.parseInt(price));
                    map.put("description",description);

                    reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AdminMaintainProductsActivity.this,"" +
                                        "Product Info Updated successfully",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AdminMaintainProductsActivity.this,
                                        AdminProduct.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deleting a specific product from db
                deleteThisProduct();
            }
        });
    }

    private void deleteThisProduct()
    {
        // removevalue() method is used with reference
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              Toast.makeText(AdminMaintainProductsActivity.this,"Product Deleted Successfully"
                      ,Toast.LENGTH_SHORT).show();
              Intent intent = new Intent(AdminMaintainProductsActivity.this, HomepageActivity.class);
              startActivity(intent);
              finish();
            }
        });
    }

    private void displaySpecificProductInfo()
    {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   String pname = dataSnapshot.child("name").getValue().toString();
                   int price = Integer.parseInt(dataSnapshot.child("price").getValue().toString());
                   String description = dataSnapshot.child("description").getValue().toString();
                   String image = dataSnapshot.child("image").getValue().toString();

                   name_maintain.setText(pname);
                   price_maintain.setText(price+"");
                   description_maintain.setText(description);
                   Picasso.get().load(image).into(image_maintain);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialise()
    {
        name_maintain = findViewById(R.id.product_name_maintain);
        price_maintain = findViewById(R.id.product_price_maintain);
        description_maintain = findViewById(R.id.product_description_maintain);

        image_maintain = findViewById(R.id.product_image_maintain);

        applyChanges = findViewById(R.id.products_maintain_btn);
        deleteProduct = findViewById(R.id.delete_products_maintain_btn);


    }
}
