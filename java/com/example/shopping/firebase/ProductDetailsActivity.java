package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

   private TabLayout tabLayout;
   private ViewPager viewPager;
   private SliderView sliderView;
   private ArrayList<String> arrayList;
    private TextView productName,productPrice, productDescription,product_Discount,
            text_num,text_minus,text_plus,quantity,product_mrp,rating_heading,product_material
            ,product_type,product_style,product_design,product_materialH
            ,product_typeH,product_styleH,product_designH;
    private String productId = "",description="Description",colors,sizes,details,color="",size="",
    material="",type="",style="",design="";
    private int num;
    private Button addtocartbtn;
    private Spinner sizesSpinner,colourSpinner;
    private DatabaseReference productRef,desc_ref,sliderRef;
    private String CategoryName="",SubCategoryName="",imgLink="";
    private int TotalCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // we get productId from homepage
        productId = getIntent().getStringExtra("pid");
        initialise();

        sliderRef = FirebaseDatabase.getInstance().getReference().child("Products")
                .child(productId).child("Images");

        arrayList = new ArrayList<>();

        sliderView = findViewById(R.id.product_image_details);

        sliderView.setSliderAdapter(new ImageSliderAdapter(this,TotalCount,productId,arrayList));

        desc_ref = FirebaseDatabase.getInstance().getReference().child("Products");

        // getting product details from db
        getProductDetails();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // adding fragments to adapter

        adapter.AddFragment(new FragmentOverview(productId,description),"Overview");

        adapter.AddFragment(new FragmentDetails(productId,colors,sizes),"Details");
        adapter.AddFragment(new FragmentReview(productId),"Review");

        // adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



//        getIntent().getExtras().get("subcategory").toString();

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
                    Toast.makeText(ProductDetailsActivity.this,"Item count cannot be negative",Toast.LENGTH_SHORT).show();

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
           if (num!=0){
               addingToCartList();
           }

            else
                toast("Product Quantity cannot be 0");
            }
        });



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
        cartMap.put("pname" , productName.getText().toString().trim());
       // cartMap.put("price" , productPrice.getText().toString().substring(7,10));
        cartMap.put("price" , productPrice.getText().toString().trim());
        cartMap.put("date" , saveCurrentDate);
        cartMap.put("time" , saveCurrentTime);
        cartMap.put("quantity" , text_num.getText().toString());
        cartMap.put("discount" , "");
        cartMap.put("pimage",imgLink);
        cartMap.put("color" , color);
        cartMap.put("size",size);

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

                                        Intent intent = new Intent(ProductDetailsActivity.this,
                                                CartActivity.class);
                                        intent.putExtra("productId",productId);
                                        intent.putExtra("color",color);
                                        intent.putExtra("size",size);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });

    }

    private void toast(String added_to_cart)
    {
        Toast.makeText(getApplicationContext(),added_to_cart,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (num == 0){
            addtocartbtn.setBackgroundColor(getResources().getColor(R.color.onboard_gray));

        }
        else {
            addtocartbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

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

    private void getProductDetails()
    {

            productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // for displaying products from a node in db , we add valueEventListener
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // send db values in product class object
                    Products products = dataSnapshot.getValue(Products.class);

                    // setting all values using products class reference object
                    productName.setText(products.getName());
                    //"Price: "+
                    productPrice.setText("₹ "+products.getPrice());
                    product_mrp.setText("Mrp: ₹"+products.getMrp());


                    int discount = Integer.parseInt(products.getMrp())
                            - Integer.parseInt(products.getPrice());

                    description = products.getDescription();
                    colors = products.getColor();
                    sizes = products.getSize();
                    material = products.getMaterial();
                    type = products.getType();
                    style = products.getStyle();
                    design = products.getDesign();

                    product_material.setText(material);
                    product_type.setText(type);
                    product_style.setText(style);
                    product_design.setText(design);

                    List<String> sizesList = new ArrayList<>(Arrays.asList(sizes.split(
                            "\\s*,\\s*")));

                            sizesList.add(0,"Choose Size");
                    List<String> colorsList = new ArrayList<>(Arrays.asList(colors.split(" ")));

                    colorsList.add(0,"Choose Colour");
                    ArrayAdapter<String> sizesAdapter = new ArrayAdapter<String>(ProductDetailsActivity.this,
                            R.layout.spinner_layout,sizesList);

                    ArrayAdapter<String> colorsAdapter = new ArrayAdapter<String>(ProductDetailsActivity.this,
                            R.layout.spinner_layout,colorsList);

                    for(int i=0;i<colorsList.size();i++){
                        Log.d("Value  :",colorsList.get(i));
                    }

                    sizesSpinner.setAdapter(sizesAdapter);
                    sizesAdapter.setDropDownViewResource(R.layout.coloured_spinner_layout);
                    colourSpinner.setAdapter(colorsAdapter);
                    colorsAdapter.setDropDownViewResource(R.layout.coloured_spinner_layout);
                    sizesAdapter.notifyDataSetChanged();
                    colorsAdapter.notifyDataSetChanged();

                    colourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            color = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    sizesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            size = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    details = products.getDescription();

                    product_Discount.setText(String.valueOf(discount) + " OFF");
                    productDescription.setText(description);
                    imgLink = products.getImage();
                   // Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialise()
    {

//        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        product_mrp = findViewById(R.id.category_product_mrp_details);
        product_Discount = findViewById(R.id.category_product_discount_details);

        product_Discount = (TextView) findViewById(R.id.product_discount_details);
        product_mrp = (TextView) findViewById(R.id.product_mrp_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        quantity= this.<TextView>findViewById(R.id.quantity);
        text_minus= this.<TextView>findViewById(R.id.text_minus);
        text_num= this.<TextView>findViewById(R.id.text_number);
        text_plus= this.<TextView>findViewById(R.id.text_plus);
        addtocartbtn=(Button)findViewById(R.id.add_to_cart);
        product_material = (TextView) findViewById(R.id.product_material_details1);
        product_materialH = (TextView) findViewById(R.id.product_material_details);
        product_type = (TextView) findViewById(R.id.product_type_details1);
        product_typeH= this.<TextView>findViewById(R.id.product_type_details);
        product_style= this.<TextView>findViewById(R.id.product_style_details1);
        product_styleH= this.<TextView>findViewById(R.id.product_style_details);
        product_design= this.<TextView>findViewById(R.id.product_design_details1);
        product_designH=(TextView) findViewById(R.id.product_design_details);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);

       // rating_heading = findViewById(R.id.rating_heading);

        colourSpinner = findViewById(R.id.colors_spinner);
        sizesSpinner = findViewById(R.id.sizes_spinner);
    }


}
