package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopping.Adapters.CategoriesImageSliderAdapter;
import com.example.shopping.Admin.AdminNewOrdersActivity;
import com.example.shopping.LoginSignup.MainActivity;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.Payments.ReferralActivity;
import com.example.shopping.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomePageCategories extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private TextView addnewproduct,tshirts_tv,sports_tshirt_tv,watches_tv,laptops_pc_tv
            ,female_dresses_tv,glasses_tv,hats_caps_tv,shoes_all_tv,headphones_handfrees_tv,sweater_tv
            ,mobiles_tv,purses_bags_wallets_tv;
    private ImageView tshirt, sports_tshirt, sweater, female_dresses, glasses, purses, hats, shoes, mobiles, laptops, watches, headphones;
    int Flag=0;
    private DrawerLayout drawer;
    private int TotalCount;
    private SliderView sliderView;
    private ArrayList<String> arrayList;
    private LinearLayoutManager layoutManager;
    private DatabaseReference sliderImagesRef;
 private final int duration = 10,pixelsToMove = 30;

    //private final Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav);

        initialise();


        Toolbar toolbar = findViewById(R.id.categories_toolbar);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);

        sliderImagesRef = FirebaseDatabase.getInstance().getReference().child("Slider Images");

        sliderView = (SliderView) findViewById(R.id.categories_horizontal_recycler);
        sliderView.setSliderAdapter(new CategoriesImageSliderAdapter(this,TotalCount,arrayList));

         drawer = findViewById(R.id.categories_drawer_layout);


        NavigationView navigationView = findViewById(R.id.categories_nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // creating fields to be attached on navigation header ie image and textview
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImage = headerView.findViewById(R.id.user_profile_image);

            // this will get the name and picture(profile) of the user who has logged in using prevalent class
            username.setText("Hello, "+ Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImage);

    }

    @Override
    protected void onStart() {
        super.onStart();

        sliderImagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    arrayList.add(ds.child("image").getValue(String.class));
                    Log.d("Img value ",ds.child("image").getValue(String.class));

                }

                Long counts = dataSnapshot.getChildrenCount();
                TotalCount = counts.intValue();
                Log.d("Total Count is ",TotalCount+"");
                sliderView.setSliderAdapter(new CategoriesImageSliderAdapter(getApplicationContext(),TotalCount,arrayList));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialise()
    {
        addnewproduct = findViewById(R.id.addnewproduct);

        tshirts_tv = findViewById(R.id.tshirts_tv);
        sports_tshirt_tv = findViewById(R.id.sports_tshirt_tv);
        watches_tv = findViewById(R.id.watches_tv);
        laptops_pc_tv = findViewById(R.id.laptops_pc_tv);
        female_dresses_tv = findViewById(R.id.female_dresses_tv);
        glasses_tv = findViewById(R.id.glasses_tv);
        hats_caps_tv = findViewById(R.id.hats_caps_tv);
        shoes_all_tv = findViewById(R.id.shoes_all_tv);
        headphones_handfrees_tv = findViewById(R.id.headphones_handfrees_tv);
        sweater_tv = findViewById(R.id.sweater_tv);
        mobiles_tv = findViewById(R.id.mobiles_tv);
        purses_bags_wallets_tv = findViewById(R.id.purses_bags_wallets_tv);

        tshirt = findViewById(R.id.tshirt);
        sports_tshirt = findViewById(R.id.sports_tshirt);
        sweater = findViewById(R.id.sweater);
        female_dresses = findViewById(R.id.female_dresses);
        glasses = findViewById(R.id.glasses);
        purses = findViewById(R.id.purses_bags_wallets);
        hats = findViewById(R.id.hats_caps);
        shoes = findViewById(R.id.shoes_all);
        laptops = findViewById(R.id.laptops_pc);
        mobiles = findViewById(R.id.mobiles);
        headphones = findViewById(R.id.headphones_handfrees);
        watches = findViewById(R.id.watches);

        arrayList = new ArrayList<>();
        tshirt.setOnClickListener(this);
        sports_tshirt.setOnClickListener(this);
        sweater.setOnClickListener(this);
        female_dresses.setOnClickListener(this);
        glasses.setOnClickListener(this);
        purses.setOnClickListener(this);
        hats.setOnClickListener(this);
        shoes.setOnClickListener(this);
        laptops.setOnClickListener(this);
        mobiles.setOnClickListener(this);
        headphones.setOnClickListener(this);
        watches.setOnClickListener(this);

    }

    public void onBackPressed(){
        Intent in1=new Intent(HomePageCategories.this, HomepageActivity.class);
        in1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in1);
    }

    @Override
    public void onClick(View view) {

        if (view == tshirt) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Tshirts");
           // in.putExtra("subcategory","Mens");
            startActivity(in);
        }

        else if (view == sports_tshirt) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "SportsTshirts");
           // in.putExtra("subcategory", "Mens");
            startActivity(in);
        }

        else if (view==female_dresses) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Femaledresses");
            startActivity(in);
        }
        else if (view==sweater) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Sweaters");
            startActivity(in);
        }
        else if (view==glasses) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Glasses");
            startActivity(in);
        }
        else if (view==purses) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Purses");
            startActivity(in);
        }
        else if (view==hats) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Hats");
            startActivity(in);
        }
        else if (view==shoes) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Shoes");
            startActivity(in);
        }
        else if (view==laptops) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Laptops");
            startActivity(in);
        }
        else if (view==mobiles) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Mobiles");
            startActivity(in);
        }
        else if (view==headphones) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "HeadPhones");
            startActivity(in);
        }
        else if (view==watches) {
            Intent in = new Intent(HomePageCategories.this, CategoriesRecycler.class);
            in.putExtra("category", "Watches");
            startActivity(in);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.homepage, menu);
        MenuItem item = menu.findItem(R.id.search_view);
        MenuItem item1 = menu.findItem(R.id.cart);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cart){
                Intent intent = new Intent(HomePageCategories.this, CartActivity.class);
                startActivity(intent);
            }
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
                Intent cartintent = new Intent(HomePageCategories.this, CartActivity.class);
                startActivity(cartintent);
            }


        else if (id == R.id.nav_home) {
            Intent ordersintent = new Intent(getApplicationContext(), HomepageActivity.class);
            startActivity(ordersintent);

        }

        else if (id == R.id.nav_orders) {
            Intent ordersintent = new Intent(HomePageCategories.this, AdminNewOrdersActivity.class);
            startActivity(ordersintent);

        }

        else if (id == R.id.nav_cancel_order) {
            Intent ordersintent = new Intent(HomePageCategories.this, AdminNewOrdersActivity.class);
            ordersintent.putExtra("User","User");
            startActivity(ordersintent);

        }

        else if (id == R.id.nav_categories) {

            }

        else if (id == R.id.nav_settings) {
            Intent settingsintent = new Intent(HomePageCategories.this, settings_activity.class);
            startActivity(settingsintent);
        }

        else if (id == R.id.nav_support) {

        }

        else if (id == R.id.nav_my_wallet) {
            Intent intent = new Intent(HomePageCategories.this, ReferralActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_logout) {
            Paper.book().destroy();

            Intent intent = new Intent(HomePageCategories.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

