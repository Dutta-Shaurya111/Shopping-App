package com.example.shopping.Fragments.ProductDetailsFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.OverviewProductViewHolder;
import com.example.shopping.firebase.ProductDetailsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FragmentOverview extends Fragment {
    private TextView descriptiontxt,heading,reviews_count,review_text;
    private RecyclerView overview_recycler;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference reference,reviewRef;
    private View view;
    private int count;
    private RatingBar ratingBar;
    private String productId;
    private String description;

    public FragmentOverview(String productId , String description) {
        this.productId = productId;
        this.description = description;
    }

    public FragmentOverview(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.overview_fragment, container, false);

        initialise();
      // Log.d("Description :  ",description);
try {
    descriptiontxt.setText(description);
}
 catch (Exception e){
    Log.d("Error: ",e.getMessage());
 }
        reference = FirebaseDatabase.getInstance().getReference().child("Products");
        reviewRef = FirebaseDatabase.getInstance().getReference().child("Reviews");

        getDescription();
        getRating();

        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        overview_recycler.setHasFixedSize(true);
        overview_recycler.setLayoutManager(layoutManager);

        return view;
    }

    private void getRating()
    {
        final ArrayList<String> reviews = new ArrayList<>();
        reviewRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String rating =ds.child("rating").getValue(String.class);
                    reviews.add(rating);
                }
                float x=0;
                for (int i=0;i<reviews.size();i++){
                    x+= Float.parseFloat(reviews.get(i));
                    Log.d("Overview Fragment va  ",x+"");
                }
                count = reviews.size();
                reviews_count.setText("("+count+")");
                ratingBar.setRating(x/reviews.size());
                ratingBar.setEnabled(false);
                String text = "";
                switch ((int)(x/reviews.size())){
                    case 1:
                        text = "Worst";
                        review_text.setTextColor(getResources().getColor(android.R.color.black));
                        break;

                    case 2:
                        text = "Bad";
                        review_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        break;

                    case 3:
                        text = "Average";
                        review_text.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        break;

                    case 4:
                        text = "Good";
                        review_text.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                        break;

                    case 5:
                        text = "Excellent";
                        review_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        break;

                        default:
                            text="Rating";
                            review_text.setTextColor(getResources().getColor(android.R.color.black));
                            break;
                }
                review_text.setText(text);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getDescription()
    {
        reference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if (dataSnapshot.exists()){
    Products products = dataSnapshot.getValue(Products.class);
    description = products.getDescription();
    Log.d("Description",description);
    descriptiontxt.setText(description);
}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
Log.d("Error Db",databaseError.getMessage());
            }
        });
    }

    private void initialise() {
        descriptiontxt = view.findViewById(R.id.overview_description);
        overview_recycler = view.findViewById(R.id.overview_recycler);
        heading = view.findViewById(R.id.overview_ratings_heading);
        ratingBar = view.findViewById(R.id.overview_ratingbar);
        reviews_count = view.findViewById(R.id.overview_reviews_count);
        review_text = view.findViewById(R.id.overview_rating_bar_text);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference, Products.class).build();
        //productRef has reference to products node in db

        FirebaseRecyclerAdapter<Products, OverviewProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, OverviewProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OverviewProductViewHolder holder, int position, @NonNull final Products model) {

                holder.txtProductName.setText(model.getName());
                holder.txtProductPrice.setText("Price is : " + model.getPrice() + " Rs");
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductMrp.setText("Mrp: " + model.getMrp());


                int discount = Integer.parseInt(model.getMrp()) - Integer.parseInt(model.getPrice());
                holder.txtProductDiscount.setText("Rs "+discount + " OFF");

                // setting image using picasso
                Picasso.get().load(model.getImage()).into(holder.ProductImg);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        {
                            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    }
                });


            }

            @NonNull
            @Override
            public OverviewProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_product_items_layout
                        , parent, false);

                OverviewProductViewHolder viewHolder = new OverviewProductViewHolder(view);

                return viewHolder;


            }

        };
        overview_recycler.setAdapter(adapter);
        adapter.startListening();

    }

}
