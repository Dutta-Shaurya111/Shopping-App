package com.example.shopping.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.shopping.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoriesImageSliderAdapter extends SliderViewAdapter<CategoriesSliderViewHolder>
{
Context context;
int setTotalCount;
private ArrayList<String> arrayList;

public CategoriesImageSliderAdapter(Context context,int setTotalCount,ArrayList<String> arrayList){
    this.context = context;
    this.setTotalCount = setTotalCount;
    this.arrayList = arrayList;
}

    @Override
    public CategoriesSliderViewHolder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_slider_product_details_layout,parent,false);

        arrayList = new ArrayList<>();

        return new CategoriesSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoriesSliderViewHolder viewHolder, final int position) {


        FirebaseDatabase.getInstance().getReference().child("Slider Images")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds:dataSnapshot.getChildren()){


                    String link1 = ds.child("image").getValue(String.class);
                    arrayList.add(link1);

                }
                Picasso.get().load(arrayList.get(position)).placeholder(R.drawable.logo).into(viewHolder.imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (int i=0;i<arrayList.size();i++){
            Log.d("Image: ",arrayList.get(i));
        }


    }

    @Override
    public int getCount() {
    // ie total images that come from firebase
        return setTotalCount;
    }
}

class CategoriesSliderViewHolder extends SliderViewAdapter.ViewHolder{

    ImageView imageView;
    View itemView;

    public CategoriesSliderViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;
        this.imageView = itemView.findViewById(R.id.slider_image2);
    }
}
