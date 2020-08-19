package com.example.shopping.NavigationDrawer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.example.shopping.Adapters.ProductsRVAdapter;
import com.example.shopping.Admin.AdminNewOrdersActivity;
import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.LoginSignup.MainActivity;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Products;
import com.example.shopping.Payments.DetectReferral;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.ProductViewHolder;
import com.example.shopping.Admin.AdminMaintainProductsActivity;
import com.example.shopping.firebase.CartActivity;
import com.example.shopping.firebase.HelpActivity;
import com.example.shopping.firebase.HomePageCategories;
import com.example.shopping.firebase.ProductDetailsActivity;
import com.example.shopping.firebase.WalletActivity;
import com.example.shopping.firebase.settings_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomepageActivity extends AppCompatActivity
        implements ItemClickListener,NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productRef, referRef, cartRef, reviewRef;

    private AutoCompleteTextView autoCompleteTextView;
    private ProgressDialog loadingbar;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    //private ProgressBar progressBar;
    private int flag = 0,totalProducts=0, mTotalItemCount = 0,mPostsPerPage=10, mLastVisibleItemPosition = 0,currentItems=0,totalItems=0,
    scrolledOutItems=0,startP=0,endP=0;
    private boolean mIsLoading = false,sortDataByPrice=false,filterBy=false;
    private ProductsRVAdapter adapter;
    private String userType = "", invitecode = "",searched="";
    private ArrayList<Products> userModels;
    private ArrayList<String> searchedItems;
    private HashMap<String,String> map;
    private Button search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        map = new HashMap<>();
        autoCompleteTextView = findViewById(R.id.auto_search);
        search_btn = findViewById(R.id.search_btn);
        //progressBar = findViewById(R.id.progress_bar);
        checkInternetConnection();

        // initialising paper for using remember_me
        Paper.init(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        loadingbar = new ProgressDialog(this);

        if (bundle != null) {
            // ie only if we are coming to this activity from admin activity , then only it will work
            userType = getIntent().getStringExtra("Admin");
        }

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        referRef = FirebaseDatabase.getInstance().getReference().child("Referrals");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        reviewRef = FirebaseDatabase.getInstance().getReference().child("Reviews");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // creating fields to be attached on navigation header ie image and textview
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImage = headerView.findViewById(R.id.user_profile_image);

        // in case we are logging in as a user and not as admin
        if (!userType.equals("Admin")) {
            // this will get the name and picture(profile) of the user who has logged in using prevalent class
            username.setText("Hello, " + Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImage);
        } else {
            username.setText("Welcome Admin");
        }

         userModels = new ArrayList<>();
        searchedItems = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_products);
        recyclerView.setHasFixedSize(true);
        // applying grid layoutmanager to recyclerview
        layoutManager = new GridLayoutManager(getApplicationContext(),2);
        //layoutManager.setStackFromEnd(true);
        //layoutManager.scrollToPosition();
        //layoutManager.setReverseLayout(true);

        // adapter = new ProductsRVAdapter(this);
        recyclerView.setLayoutManager(layoutManager);

       // recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    mIsLoading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrolledOutItems = layoutManager.findFirstVisibleItemPosition();


                if (mIsLoading && (currentItems + scrolledOutItems)== totalItems){
                    mIsLoading = false;
                    fetchData();
                }
                /*
                mTotalItemCount = layoutManager.getItemCount();
                mLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition
                        + 3)) {
                    getUsers(adapter.getLastItemId());
                    mIsLoading = true;
                }
                */

            }
        });
        //showReferAlertDialog();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autoCompleteTextView.getText().toString().isEmpty()) {
                    firbaseSearch(autoCompleteTextView.getText().toString());
                }
            }
        });

       /* tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tab.getIcon().setTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                int pos = tab.getPosition();
                if (pos == 0){
                    sortAllDataByPrice();
                }
                else {
                    Intent intent = new Intent(HomepageActivity.this, CartActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        */

    }

    private void getAllSearchedItems()
    {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           for (DataSnapshot ds : dataSnapshot.getChildren())
           {
               searchedItems.add(ds.getValue(Products.class).getName());
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchData() {

            if (mPostsPerPage <= totalProducts) {
                loadingbar.setTitle("Loading");
                loadingbar.setCancelable(false);
                loadingbar.show();
                //progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // getUsers(adapter.getLastItemId());
                        mPostsPerPage *= 2;
                        if (sortDataByPrice) {
                            sortAllDataByPrice();
                        }
                        else if (filterBy)
                        {
                            filterByPrice(startP,endP);
                        }
                        else if (!searched.equals("")){
                            firbaseSearch(searched);
                        }
                        else
                            gettingAllProductsInRecyclerView();

                        //progressBar.setVisibility(View.GONE);
                        loadingbar.dismiss();

                        recyclerView.smoothScrollToPosition(mPostsPerPage - 4);

                        //   float y = recyclerView.getChildAt(mPostsPerPage-5).getY();
                        //  scrollView.smoothScrollTo(0, (int) y);
                    }
                }, 3000);
            }
        }


    @Override
    protected void onStart() {
        super.onStart();

        getAllSearchedItems();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,searchedItems);
        autoCompleteTextView.setAdapter(adapter);

        //getUsers(null);
        gettingAllProductsInRecyclerView();
        recyclerView.smoothScrollToPosition(mPostsPerPage-3);
       // recyclerView.requestChildFocus(mPostsPerPage-3,getCurrentFocus());
        checkCartCount();
        // checkRefer();

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               totalProducts = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getUsers(final String nodeId) {
        Query query;

        if (nodeId == null) {
            query = productRef
                    .orderByKey()
                    .limitToFirst(3);
          //  userModels.clear();
        }
        else {
            query = productRef
                    .orderByKey()
                    .startAt(nodeId + 1)
                    .limitToFirst(3);

        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Products user;
                //userModels.clear();
                for (final DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userModels.add(userSnapshot.getValue(Products.class));

                }

                adapter.addAll(userModels);
                adapter.notifyDataSetChanged();
                Log.d("UserModelContains :",userModels+"");
               // adapter.notifyDataSetChanged();
                Log.d("Usermodels size: ",userModels.size()+"");
                mIsLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mIsLoading = false;
            }
        });
    }



    private void gettingAllProductsInRecyclerView()
    {

        Query query ;
        //= productRef.limitToFirst(3);
        // we are using default FirebaseRecyclerDatabase to display products from db

       /* if (nodeId == null)
            query = FirebaseDatabase.getInstance().getReference()
                    .child("Products")
                    .orderByKey()
                   // .startAt(nodeId)
                    .limitToFirst(mPostsPerPage);
        else
*/
            query = FirebaseDatabase.getInstance().getReference()
                    .child("Products")
                    .orderByKey()
                   // .startAt(nodeId+1)
                    .limitToFirst(mPostsPerPage);

        // FirebaseRecyclerOptions is required to work with FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(query , Products.class).build();
        //productRef has reference to products node in db

        FirebaseRecyclerAdapter<Products , ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final Products model) {
                userModels.add(model);

                Log.d("UserModelsize : ",userModels.size()+"");

                holder.txtProductName.setText(model.getName());
                holder.txtProductPrice.setText("₹" + model.getPrice()+"/-");
                //holder.txtProductDescription.setText( model.getDescription());
                holder.txtProductMrp.setText("Mrp: "+model.getMrp()+"/-");

                int discount = Integer.parseInt(model.getMrp())-Integer.parseInt(model.getPrice());
                float perc = (float)((discount*100) / Integer.parseInt(model.getMrp()));
                holder.txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");

                // setting image using picasso
                Picasso.get().load(model.getImage()).into(holder.ProductImg);

                holder.ProductImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (userType.equals("Admin"))
                        {
                            Intent intent = new Intent(HomepageActivity.this, AdminMaintainProductsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);

                        }
                        else
                        {
                            Intent intent = new Intent(HomepageActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    }
                });

                holder.product_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        change_image(holder.product_like);
                    }
                });


            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout
                        ,parent,false);

                ProductViewHolder viewHolder = new ProductViewHolder(view);

                return viewHolder;


            }

        };


        //Collections.reverse(userModels);

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        recyclerView.smoothScrollBy(0,(mPostsPerPage-3)*200);
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-3);
    }


    private void checkCartCount() {
        Query query = cartRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                .child("Products");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkRefer() {
        Log.d("In Check Refer", "In Check Refer");
        //final DatabaseReference referRef = FirebaseDatabase.getInstance().getReference();
       /* final  DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("invite")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
*/

        referRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot referShot : dataSnapshot.getChildren()) {
                    Log.d("senderno", referShot.child("senderno").getValue(String.class));
                    Log.d("status", referShot.child("status").getValue(String.class));
                    if ((referShot.child("receiverno").getValue(String.class).equals(
                            Prevalent.currentOnlineUser.getNumber()
                    )) && (referShot.child("status").equals("Unsuccess"))) {

                        Log.d("In If stat", "tes");


                    } else {
                        Log.d("Nothing happens", "nothing happening in this");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void change_image(View v) {
        ImageButton iv = (ImageButton) v;
        //use flag to change image
        if (flag == 0) {
            iv.setBackground(getResources().getDrawable(R.drawable.heart_clicked));
            flag = 1;
        } else {
            iv.setBackground(getResources().getDrawable(R.drawable.heart_vector));
            flag = 0;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
       // MenuItem item = menu.findItem(R.id.search_view);
        MenuItem item_spinner = menu.findItem(R.id.filter);
        /*SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firbaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newtext) {
                // Filter as you type
                firbaseSearch(newtext);
                return false;
            }
        });
*/
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomepageActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.filtering));
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.coloured_spinner_layout);
        adapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0){
                switch (i) {
                    case 1:
                        //getUsers(null);
                        gettingAllProductsInRecyclerView();
                        toast("Showing All Products");
                        break;

                    case 2:
                        startP = 1;
                        endP = 500;
                        filterByPrice(startP, endP);
                        break;

                    case 3:
                        startP = 501;
                        endP = 1000;
                        filterByPrice(startP, endP);
                        break;

                    case 4:
                        startP = 1001;
                        endP = 1500;
                        filterByPrice(startP, endP);
                        break;

                    case 5:
                        startP = 1501;
                        endP = 2000;
                        filterByPrice(startP, endP);
                        break;

                    case 6:
                        startP = 2001;
                        endP = 35000;
                        filterByPrice(startP, endP);
                        break;
                }

                }

                else {
                    toast("No Filter Selected");
                }

                Log.d("Value is : ", adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cart) {
            if (!userType.equals("Admin")) {
                Intent intent = new Intent(HomepageActivity.this, CartActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.sort) {
            sortAllDataByPrice();

        }
        else if (id == R.id.about) {
            sortAllDataByPrice();

        }
        else if (id == R.id.terms_of_use) {
            openUrl("https://shauryaarts.com");

        }
        else if (id == R.id.privacy_policy) {
            openUrl("https://shauryaarts.com");

        }
        else if (id == R.id.feedback) {
            GiveFeedback();

        }

        return super.onOptionsItemSelected(item);
    }

    private void openUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    private void GiveFeedback()
    {
        final DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference()
                .child("Feedbacks");

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.feedback_form_dialog,null);

        final EditText editText = view.findViewById(R.id.feedback_text);
        TextView heading = view.findViewById(R.id.feedback_heading);
        TextView heading2 = view.findViewById(R.id.feedback_tv);
        final Button submitBtn = view.findViewById(R.id.feedback_submit);
        Button cancelBtn = view.findViewById(R.id.feedback_cancel);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("In Button click ","Yesssss");
                feedbackRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(Prevalent.currentOnlineUser.getNumber()).exists() ||
                                !dataSnapshot.exists()) {

                            String text = editText.getText().toString();
                            Log.d("Inside On Datachange ","Yesssss");
                            if (!text.isEmpty()) {
                                Log.d("Inside On Text... ","Yesssss");
                                feedbackRef.child(Prevalent.currentOnlineUser.getNumber()).child("feedback")
                                        .setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            toast("Feedback Submitted Successfully!!!");
                                        }
                                    }
                                });
                            }
                            else {
                                editText.setError("Empty");
                                editText.requestFocus();
                            }
                        } else {
                            submitBtn.setBackgroundColor(getResources().getColor(R.color.onboard_gray));
                            submitBtn.setEnabled(false);
                            toast("You have already given Feedback");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
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

    private void filterByPrice(int startPrice, int endPrice) {
//DatabaseReference xyzRef = FirebaseDatabase.getInstance().getReference().child("Xyz");
        Query query = productRef.orderByChild("price1").startAt(startPrice).endAt(endPrice);

       /* query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d("Found something in ","found"+dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
        if (!filterBy) {
            mPostsPerPage = 1140;
        }
        filterBy = true;
        valueEventListener(query);


        /*query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, Products.class).build();
                //productRef has reference to products node in db

                FirebaseRecyclerAdapter<Products , ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText("₹" + model.getPrice());
                        //holder.txtProductDescription.setText( model.getDescription());

                        // setting image using picasso
                        Picasso.get().load(model.getImage()).into(holder.ProductImg);

                        Log.d("TAG", "Getting all products from db");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomepageActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder (@NonNull ViewGroup parent,
                                                                 int viewType){
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout
                                , parent, false);

                        ProductViewHolder viewHolder = new ProductViewHolder(view);

                        return viewHolder;

                    }

                };

                recyclerView.setAdapter(adapter);
                adapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }

    private void sortAllDataByPrice() {
        Query query = productRef.orderByChild("price");//.limitToFirst(5);
        if (!sortDataByPrice) {
            mPostsPerPage = 10;
        }
        sortDataByPrice = true;
        valueEventListener(query);

    }

    private void valueEventListener(final Query query) {
       // query.orderByKey()
                // .startAt(nodeId+1)

                query.limitToFirst(mPostsPerPage)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(query, Products.class).build();
                //productRef has reference to products node in db

                FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        userModels.add(model);
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductPrice.setText("₹" + model.getPrice());
                        //holder.txtProductDescription.setText( model.getDescription());

                        // setting image using picasso
                        Picasso.get().load(model.getImage()).into(holder.ProductImg);

                        Log.d("TAG", "Getting all products from db");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomepageActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout
                                , parent, false);

                        ProductViewHolder viewHolder = new ProductViewHolder(view);

                        return viewHolder;

                    }

                };

                layoutManager.scrollToPosition(adapter.getItemCount()-1);
                recyclerView.setAdapter(adapter);
                adapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void firbaseSearch(String searchText) {
        Query firebaseSearchQuery = productRef.orderByChild("category").startAt(searchText.toUpperCase())
                .endAt(searchText + "\uf8ff");
        searched=searchText;
        valueEventListener(firebaseSearchQuery);

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            if (!userType.equals("Admin")) {
                Intent cartintent = new Intent(HomepageActivity.this, CartActivity.class);
                startActivity(cartintent);
            }
        } else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_orders) {
            Intent ordersintent = new Intent(HomepageActivity.this, AdminNewOrdersActivity.class);
            ordersintent.putExtra("User", "User");
            startActivity(ordersintent);

        } else if (id == R.id.nav_cancel_order) {
            Intent ordersintent = new Intent(HomepageActivity.this, AdminNewOrdersActivity.class);
            ordersintent.putExtra("User", "User");
            startActivity(ordersintent);

        } else if (id == R.id.nav_referandearn) {
            Intent refersintent = new Intent(HomepageActivity.this, DetectReferral.class);
            startActivity(refersintent);

        } else if (id == R.id.nav_categories) {
            if (!userType.equals("Admin")) {
                Intent settingsintent = new Intent(HomepageActivity.this, HomePageCategories.class);
                startActivity(settingsintent);
            }

        } else if (id == R.id.nav_settings) {
            if (!userType.equals("Admin")) {
                Intent settingsintent = new Intent(HomepageActivity.this, settings_activity.class);
                startActivity(settingsintent);
            }
        } else if (id == R.id.nav_support) {
               Intent intent = new Intent(HomepageActivity.this, HelpActivity.class);
             startActivity(intent);
        } else if (id == R.id.nav_my_wallet) {
            Intent intent = new Intent(HomepageActivity.this, WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();

            Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            opendialog();
        }

    }


    private void opendialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Warning!!!!");
        builder.setMessage("Internet Error!! Please provide an active Active Internet Connection  " +
                "to Continue");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomepageActivity.this, MainActivity.class);
                startActivity(intent);
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(int position) {
        if (position < userModels.size()) {



            Log.d("ssssss", userModels.get(position).getPid());
            toast(userModels.get(position).getPid());
        }
    }

    @Override
    public void onClick(View view ,int position, boolean isLongClick) {

    }


}

/*



     <TextView
            android:id="@+id/product_description"
            android:layout_width="320dp"
            android:layout_height="wrap_content"
            android:text="Product Description"
            android:minLines="1"
            android:maxLines="3"
            android:layout_marginTop="10sp"
            android:layout_marginLeft="15dp"
            android:layout_marginBottom="15dp"
            android:layout_below="@id/product_mrp"
            android:textAlignment="center"
            android:textSize="16dp"
            android:textStyle="bold"
            android:textColor="@color/colorPrimary"/>

             <com.google.android.material.tabs.TabLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:elevation="12dp"
        app:tabIndicatorHeight="2.5dp"
        app:tabSelectedTextColor="@color/colorPrimary"
        android:background="@android:color/white"
        android:id="@+id/tab_layout_">

        <com.google.android.material.tabs.TabItem
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:foregroundTint="@color/colorPrimary"
            android:icon="@drawable/ic_sort_black_24dp"
            android:text="Sort By price"/>




        <com.google.android.material.tabs.TabItem
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="go to cart"
            android:icon="@drawable/cart"/>

    </com.google.android.material.tabs.TabLayout>


            <View
                android:id="@+id/viewe"
                android:scaleType="fitXY"
                android:layout_width="200dp"
                android:src="@drawable/wallet"
                android:background="@color/purple_light"
                android:layout_centerHorizontal="true"
                android:layout_height="220dp"
                android:layout_marginTop="2dp"/>

            <androidx.appcompat.widget.AppCompatTextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Hi There">

            </androidx.appcompat.widget.AppCompatTextView>
 */
