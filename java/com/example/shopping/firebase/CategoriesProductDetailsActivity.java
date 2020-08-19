package com.example.shopping.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.shopping.Adapters.ImageSliderAdapter;
import com.example.shopping.Fragments.ProductDetailsFragments.FragmentDetails;
import com.example.shopping.Fragments.ProductDetailsFragments.FragmentOverview;
import com.example.shopping.Fragments.ProductDetailsFragments.FragmentReview;
import com.example.shopping.Fragments.ProductDetailsFragments.ViewPagerAdapter;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CategoriesProductDetailsActivity extends AppCompatActivity {

    private TextView productName,productPrice, productDescription,
            text_num,text_minus,text_plus,quantity,product_Discount,product_Mrp;
    private String productId = "",description="Description",colors,sizes,details,imgLink="";
    private int num;
    private SliderView sliderView;
    private ArrayList<String> arrayList;
    private Button addtocartbtn;
    private DatabaseReference productRef,desc_ref,sliderRef;
    private String CategoryName="",SubCategoryName="";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int TotalCount;
    private float scaleFactor=1.0f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_details);

        // we get productId from homepage
        productId = getIntent().getStringExtra("pid");
        initialise();

        scaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener());
        arrayList = new ArrayList<>();


        sliderRef = FirebaseDatabase.getInstance().getReference().child("Products")
                .child(productId).child("Images");
        sliderView.setSliderAdapter(new ImageSliderAdapter(this,TotalCount,productId,arrayList));


        desc_ref = FirebaseDatabase.getInstance().getReference().child("Products");

       CategoryName = getIntent().getExtras().get("category").toString();
        SubCategoryName = getIntent().getExtras().get("subcategory").toString();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // adding fragments to adapter

        adapter.AddFragment(new FragmentOverview(productId,description),"Overview");

        adapter.AddFragment(new FragmentDetails(productId,colors,sizes),"Details");
        adapter.AddFragment(new FragmentReview(productId),"Review");

        // adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        num=0;
        text_num.setText(""+num);


        text_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num++;
                text_num.setText(""+num);
            }
        });
        text_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num==0){
                    text_num.setText(""+0);
                    Toast.makeText(CategoriesProductDetailsActivity.this,"Item count cannot be negative",Toast.LENGTH_SHORT).show();

                }
                else {
                    num--;
                    text_num.setText("" + num);
                }
            }
        });
        //  productId = getIntent().getStringExtra("pid");

        num=0;
        text_num.setText(""+num);

        addtocartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           if (text_num.getText() != "0")
            addingToCartList();
            else
                toast("Product Quantity cannot be 0");
            }
        });


        // getting product details from db
        getProductDetails();

    }

    private void addingToCartList()
    {
        String saveCurrentTime , saveCurrentDate;

        // using calender to get time and date at which user trying to buy product
        Calendar calForDate =Calendar.getInstance();

        SimpleDateFormat currDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currDate.format(calForDate.getTime());

        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currTime.format(calForDate.getTime());

        //creating database reference to a new node i.e cartlist inside db
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");

        // creating hashmap to store multiple values in db
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid" , productId);
        cartMap.put("pname" , productName.getText().toString());
        cartMap.put("price" , productPrice.getText().toString());
        cartMap.put("date" , saveCurrentDate);
        cartMap.put("time" , saveCurrentTime);
        cartMap.put("quantity" , text_num.getText().toString());
        cartMap.put("discount" , "");
        cartMap.put("pimage",imgLink);

        // updating data in Users Db node so that he can later view his purchases
        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getNumber()).child("Products")
                .child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // if task is successful , then , we also need to update products in Admin View
                    // so that admin can view a user's purchases
                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getNumber())
                            .child("Products").child(productId).updateChildren(cartMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        toast("Added to cart");

                                        Intent intent = new Intent(CategoriesProductDetailsActivity.this,
                                                CartActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        sliderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    arrayList.add(ds.child("link").getValue(String.class));
                    Log.d("Img value ",ds.child("link").getValue(String.class));
                }

                Long counts = dataSnapshot.getChildrenCount();
                TotalCount = counts.intValue();
                Log.d("Total Count is ",TotalCount+"");
                sliderView.setSliderAdapter(new ImageSliderAdapter(getApplicationContext(),TotalCount,productId,arrayList));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void toast(String added_to_cart)
    {
        Toast.makeText(getApplicationContext(),added_to_cart,Toast.LENGTH_SHORT).show();
    }

    private void getProductDetails()
    {

            productRef = FirebaseDatabase.getInstance().getReference().child("Product Category")
            .child(CategoryName).child(SubCategoryName);

        // for displaying products from a node in db , we add valueEventListener
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // send db values in product class object
                    Products products = dataSnapshot.getValue(Products.class);

                    // setting all values using products class reference object
                    productName.setText(products.getName());
                    productPrice.setText("Price is: "+products.getPrice());
                    productDescription.setText(products.getDescription());

                    Log.d("MRP",products.getMrp());
                    Log.d("MRP",String.valueOf(products.getPrice()));

                    int discount = Integer.parseInt(products.getMrp())
                            - Integer.parseInt(String.valueOf(products.getPrice()));

                    description = products.getDescription();
                    colors = products.getColor();
                    sizes = products.getSize();
                    details = products.getDescription();

                    product_Mrp.setText("Mrp : "+products.getMrp());
                    product_Discount.setText(String.valueOf(discount) + " OFF");
                    productDescription.setText(description);
                    imgLink = products.getImage();

                    //Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor = scaleGestureDetector.getScaleFactor();

            return true;
        }
    }

    private void initialise()
    {
//        productImage = (ImageView) findViewById(R.id.category_product_image_details);
        productName = (TextView) findViewById(R.id.category_product_name_details);
        productDescription = (TextView) findViewById(R.id.category_product_description_details);
        productPrice = (TextView) findViewById(R.id.category_product_price_details);
        quantity= this.<TextView>findViewById(R.id.category_quantity);
        text_minus= this.<TextView>findViewById(R.id.category_text_minus);
        text_num= this.<TextView>findViewById(R.id.category_text_number);
        text_plus= this.<TextView>findViewById(R.id.category_text_plus);
        addtocartbtn=(Button)findViewById(R.id.category_add_to_cart);


        sliderView = findViewById(R.id.category_product_image_details);
        product_Discount = findViewById(R.id.category_product_discount_details);
        product_Mrp = findViewById(R.id.category_product_mrp_details);

        viewPager = findViewById(R.id.category_view_pager);
        tabLayout = findViewById(R.id.category_tab_layout);

    }


}
