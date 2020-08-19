package com.example.shopping.Fragments.ProductDetailsFragments;

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

import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentDetails extends Fragment
{
    private RatingBar ratingBar;
    private View view;
    private int count;

    private DatabaseReference reference,reviewRef;
    private TextView colors_tv,sizes_tv,ratings_tv,tv_count1,tv_count2,tv_count3,tv_count4,tv_count5;
    private String colors,sizes,productId;
    private int count1=0,count2=0,count3=0,count4=0,count5=0;
    private RatingBar ratingBar1,ratingBar2,ratingBar3,ratingBar4,ratingBar5;

    public FragmentDetails(String productId,String colors,String sizes)
    {
this.colors = colors;
this.sizes = sizes;
this.productId = productId;
    }

    public FragmentDetails(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.details_fragment,container,false);

        reviewRef = FirebaseDatabase.getInstance().getReference().child("Reviews");
        initialise();


        getRating();
        reference = FirebaseDatabase.getInstance().getReference().child("Products");

        reference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   // Map<String,String> map =dataSnapshot.getValue(Products.class);
                    Products products = dataSnapshot.getValue(Products.class);
                    colors = products.getColor();
                    Log.d("colors",colors);
                    sizes = products.getSize();
                    Log.d("sizes",sizes);
                    colors_tv.setText("Colors Available: "+colors);
                    sizes_tv.setText("Sizes Available: "+sizes);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


    private void getRating()
    {

        reviewRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String rating =ds.child("rating").getValue(String.class);
                    int i = (int)Float.parseFloat(rating);

                    if (i == 1){
                        count1++;
                    }
                    else if (i == 2){
                        count2++;
                    }
                    else if (i == 3){
                        count3++;
                    }
                    else if (i == 4){
                        count4++;
                    }
                    else if (i == 5){
                        count5++;
                    }
                }

                tv_count1.setText("("+count1+")");
                tv_count2.setText("("+count2+")");
                tv_count3.setText("("+count3+")");
                tv_count4.setText("("+count4+")");
                tv_count5.setText("("+count5+")");
                ratingBar1.setRating(1);
                ratingBar2.setRating(2);
                ratingBar3.setRating(3);
                ratingBar4.setRating(4);
                ratingBar5.setRating(5);
                ratingBar1.setEnabled(false);
                ratingBar2.setEnabled(false);
                ratingBar3.setEnabled(false);
                ratingBar4.setEnabled(false);
                ratingBar5.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initialise()
    {
        colors_tv = view.findViewById(R.id.details_colors);
        sizes_tv = view.findViewById(R.id.details_sizes);
        ratings_tv = view.findViewById(R.id.ratings_heading);

        ratingBar1 = view.findViewById(R.id.details_ratingbar1);
        ratingBar2 = view.findViewById(R.id.details_ratingbar2);
        ratingBar3 = view.findViewById(R.id.details_ratingbar3);
        ratingBar4 = view.findViewById(R.id.details_ratingbar4);
        ratingBar5 = view.findViewById(R.id.details_ratingbar5);

        tv_count1 = view.findViewById(R.id.rating_bar_count1);
        tv_count2 = view.findViewById(R.id.rating_bar_count2);
        tv_count3 = view.findViewById(R.id.rating_bar_count3);
        tv_count4 = view.findViewById(R.id.rating_bar_count4);
        tv_count5 = view.findViewById(R.id.rating_bar_count5);
    }
}

