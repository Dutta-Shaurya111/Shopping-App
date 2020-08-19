package com.example.shopping.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Model.Categories;
import com.example.shopping.Model.Products;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.CategoriesViewHolder;
import com.example.shopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoriesRecycler extends AppCompatActivity {
private ImageView img;
private TextView text;
private TabLayout tabLayout;
private FrameLayout frameLayout;
    private ArrayList<String> searchedItems;
    private AutoCompleteTextView autoCompleteTextView;
private TextView tc;
private RecyclerView recyclerView,recyclerView2;
private RecyclerView.LayoutManager layoutManager;
private String CategoryName,SubCategoryName,searched="";
private DatabaseReference categoryproductRef,productRef;
    private Button search_btn;
private List<String> subcategoryNames;
private Spinner spinner2;
   private MenuItem item2;
   private int flag=0,startP=0,endP=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_recycler);

        CategoryName = getIntent().getExtras().get("category").toString();

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        categoryproductRef = FirebaseDatabase.getInstance().getReference()
                .child("Product Category").child(CategoryName);

        Log.d("OnCreate Called", "OnCreate Called");

        initialise();


//         SubCategoryName = getIntent().getExtras().get("subcategory").toString();

        Log.d("Category", CategoryName);
        Log.d("Sub-category", subcategoryNames.size() + "");

        Toolbar toolbar = findViewById(R.id.categories_toolbar_);
        //toolbar.setTitle(CategoryName);
        setSupportActionBar(toolbar);


        //.child(SubCategoryName);
        // tc = findViewById(R.id.tc);
        // getAllSubCategoryNames();

        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);



    }

    private void initialise()
    {
        img = findViewById(R.id.img_toolbar);
        text = findViewById(R.id.text_choose_toolbar);
        search_btn = findViewById(R.id.categories_search_btn);
        autoCompleteTextView = findViewById(R.id.categories_auto_search);

        tabLayout = findViewById(R.id.tab_layout_category);
        frameLayout = findViewById(R.id.frame);

        subcategoryNames = new ArrayList<>();

        spinner2=findViewById(R.id.spinnerr);

        recyclerView = findViewById(R.id.recycler_view_categories_);

        searchedItems = new ArrayList<>();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autoCompleteTextView.getText().toString().isEmpty()) {
                    firbaseSearch(autoCompleteTextView.getText().toString(),SubCategoryName);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Staart Called","OnStart Called");
        //subcategoryNames =

        getAllSearchedItems();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,searchedItems);
        autoCompleteTextView.setAdapter(adapter);

        subcategoryNames = getAllSubCategoryNames(0,"");
        Log.d("Sub Category list",subcategoryNames.size()+"");

    }

    private void getAllSearchedItems()
    {
        searchedItems.clear();
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

    private List<String> getAllSubCategoryNames(final int pos, final String s)
    {
         //final List<String> list=new ArrayList<>();
        categoryproductRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                try {

                    subcategoryNames.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        subcategoryNames.add(name);
                        // subcategoryNames.add(name);

                    }

                    for (int i=0;i<subcategoryNames.size();i++){
                       // toast(item2.getItemId()+"");

                    }

                    if (s == ""){
                        meth(subcategoryNames.get(0));
                    }

                    else {
                        meth(subcategoryNames.get(pos));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoriesRecycler.this, android.R.layout.simple_spinner_item, subcategoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    Log.d("Size ",subcategoryNames.size()+"");
                    spinner2.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // spinner2.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,subcategoryNames));

                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d("In Item Select","In Item Select");
                            String text = adapterView.getItemAtPosition(i).toString();
                            Log.d("Pos is",i+"");
                            Log.d("Val is",text);
                            meth(text);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < subcategoryNames.size(); i++) {
                        // builder.append(subcategoryNames.get(i)+" , ");
                        Log.d("Sub Categories Found ", subcategoryNames.get(i));
                    }
                }
                catch (Exception e){
                    toast(e.getMessage());
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
return subcategoryNames;
    }


    private void meth(final String subCategory)
    {
        if (subcategoryNames.size() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesRecycler.this);
            builder.setTitle("Sorry!!! ");
            builder.setMessage("No Products To Display in This Category......." +
                    "We will notify you when New Products are added in this category." +
                    "Meanwhile , You can Carry On Shopping");
            builder.show();
        }

        try
        {
            final String a = subCategory;
            FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                    .setQuery(categoryproductRef.child(subCategory), Categories.class)
                    .build();

            FirebaseRecyclerAdapter<Categories, CategoriesViewHolder> categoriesadapter = new
                    FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final CategoriesViewHolder holder, int position, @NonNull final Categories model) {

                            holder.txtProductName.setText(model.getName());
                            holder.txtProductPrice.setText("Price is : ₹" + model.getPrice());
                            holder.txtProductMrp.setText("Mrp: ₹"+model.getMrp()+"/-");

                            int discount = Integer.parseInt(model.getMrp())-Integer.parseInt(model.getPrice());
                            float perc = (float)((discount*100) / Integer.parseInt(model.getMrp()));
                            holder.txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");


                            // setting image using picasso
                            Picasso.get().load(model.getImage()).into(holder.ProductImg);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(CategoriesRecycler.this, CategoriesProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    intent.putExtra("category", CategoryName);
                                    intent.putExtra("subcategory", a);
                                    startActivity(intent);
                                }
                            });

                            holder.LikeImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    change_image(holder.LikeImg);
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            // creating view of the layout by inflating it
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product_items_layout
                                    , parent, false);

                            // creating viewholder which will hold the view
                            CategoriesViewHolder viewHolder = new CategoriesViewHolder(view);

                            return viewHolder;
                        }
                    };
            recyclerView.setAdapter(categoriesadapter);
            categoriesadapter.startListening();
        }
        catch (Exception e)
        {
            toast(e.getMessage());
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tab.getIcon().setTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                int pos = tab.getPosition();
                if (pos == 0){
                    sortAllDataByPrice(subCategory);
                }
                else {
                    Intent intent = new Intent(CategoriesRecycler.this, CartActivity.class);
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

SubCategoryName = subCategory;
    }

    private void sortAllDataByPrice(String subCategory)
    {
        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                .setQuery(categoryproductRef.child(subCategory).orderByChild("price"), Categories.class).build();
        //productRef has reference to products node in db

        FirebaseRecyclerAdapter<Categories , CategoriesViewHolder> adapter = new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoriesViewHolder holder, int position, @NonNull final Categories model) {

                holder.txtProductName.setText(model.getName());
                holder.txtProductPrice.setText("Price is : " + model.getPrice() + " Rs");
                holder.txtProductMrp.setText("Mrp: ₹"+model.getMrp()+"/-");

                int discount = Integer.parseInt(model.getMrp())-Integer.parseInt(model.getPrice());
                float perc = (float)((discount*100) / Integer.parseInt(model.getMrp()));
                holder.txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");
                // setting image using picasso
                Picasso.get().load(model.getImage()).into(holder.ProductImg);

                Log.d("TAG","Getting all products from db");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CategoriesRecycler.this, ProductDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });

                holder.LikeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        change_image(holder.LikeImg);
                    }
                });

            }

            @NonNull
            @Override
            public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product_items_layout
                        ,parent,false);

                CategoriesViewHolder viewHolder = new CategoriesViewHolder(view);

                return viewHolder;

            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        MenuItem item_spinner = menu.findItem(R.id.categories_filter);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoriesRecycler.this,
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
                            meth(subcategoryNames.get(0));
                            toast("Showing All Products");
                            break;

                        case 2:
                            startP = 1;
                            endP = 500;
                            filterByPrice(startP, endP,SubCategoryName);
                            break;

                        case 3:
                            startP = 501;
                            endP = 1000;
                            filterByPrice(startP, endP,SubCategoryName);
                            break;

                        case 4:
                            startP = 1001;
                            endP = 1500;
                            filterByPrice(startP, endP,SubCategoryName);
                            break;

                        case 5:
                            startP = 1501;
                            endP = 2000;
                            filterByPrice(startP, endP,SubCategoryName);
                            break;

                        case 6:
                            startP = 2001;
                            endP = 35000;
                            filterByPrice(startP, endP,SubCategoryName);
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


    private void filterByPrice(int startPrice, int endPrice, String subCategoryName) {
//DatabaseReference xyzRef = FirebaseDatabase.getInstance().getReference().child("Xyz");
        Log.d("Sub Catwegory name: ",subCategoryName);
        Query query = categoryproductRef.child(subCategoryName).orderByChild("price1").startAt(startPrice).endAt(endPrice);
        valueEventListener(query,subCategoryName);
    }


    private void valueEventListener(final Query query,final String subCategory) {
        // query.orderByKey()
        // .startAt(nodeId+1)
        Log.d("Sub Catwegory name: ",subCategory);
       // query.limitToFirst(mPostsPerPage)
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        FirebaseRecyclerOptions<Categories> options = new FirebaseRecyclerOptions.Builder<Categories>()
                                .setQuery(query, Categories.class).build();
                        //productRef has reference to products node in db

                        FirebaseRecyclerAdapter<Categories , CategoriesViewHolder> adapter = new FirebaseRecyclerAdapter<Categories, CategoriesViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull final CategoriesViewHolder holder, int position, @NonNull final Categories model) {

                                holder.txtProductName.setText(model.getName());
                                holder.txtProductPrice.setText("Price is : " + model.getPrice() + " Rs");
                                holder.txtProductMrp.setText("Mrp: ₹"+model.getMrp()+"/-");

                                int discount = Integer.parseInt(model.getMrp())-Integer.parseInt(model.getPrice());
                                float perc = (float)((discount*100) / Integer.parseInt(model.getMrp()));
                                holder.txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");
                                // setting image using picasso
                                Picasso.get().load(model.getImage()).into(holder.ProductImg);

                                Log.d("TAG","Getting all products from db");

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CategoriesRecycler.this, ProductDetailsActivity.class);
                                        intent.putExtra("pid",model.getPid());
                                        startActivity(intent);
                                    }
                                });

                                holder.LikeImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        change_image(holder.LikeImg);
                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product_items_layout
                                        ,parent,false);

                                CategoriesViewHolder viewHolder = new CategoriesViewHolder(view);

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
    }

    private void firbaseSearch(String searchText,String subCategory) {
        Log.d("Sub Category name: ",subCategory);
        Query firebaseSearchQuery = categoryproductRef.child(subCategory).orderByChild("name").startAt(searchText.toUpperCase())
                .endAt(searchText + "\uf8ff");
        searched=searchText;
        valueEventListener(firebaseSearchQuery,subCategory);

    }

    private void toast(String added_to_cart)
    {
        Toast.makeText(getApplicationContext(),added_to_cart,Toast.LENGTH_SHORT).show();
    }
}
/*
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/collapsing_toolbar_floating_action_button"
        android:layout_width="60sp"
        android:layout_height="50sp"
        android:layout_margin="19dp"
        android:background="@android:color/black"
        app:layout_anchor="@id/recycler_view_categories"
        app:layout_anchorGravity="bottom|end">


        <androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".firebase.CategoriesRecycler">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="250sp"
        android:fitsSystemWindows="true"
        android:theme="@style/AppTheme">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fitsSystemWindows="true"
            app:contentScrim="@color/colorPrimary"
            app:title="Shopping App"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="250sp"
                android:id="@+id/img"
                android:scaleType="fitXY"
                android:src="@drawable/wallet"/>

            <androidx.appcompat.widget.Toolbar
                android:layout_width="match_parent"
                android:id="@+id/categories_toolbar_"
                android:layout_height="?attr/actionBarSize"
                app:popupTheme="@style/AppTheme">

            </androidx.appcompat.widget.Toolbar>
        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>



    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view_categories"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="com.google.android.material.appbar.AppBarLayout$ScrollingViewBehavior">


    </androidx.recyclerview.widget.RecyclerView>


        <Spinner
            android:layout_width="50sp"
            android:layout_height="50sp"
            android:layout_margin="19dp"
            android:background="@android:color/holo_red_light"
            app:layout_anchor="@id/recycler_view_categories"
            app:layout_anchorGravity="bottom|end"
            android:id="@+id/spinnerr">

        </Spinner>

</androidx.coordinatorlayout.widget.CoordinatorLayout>


 */

