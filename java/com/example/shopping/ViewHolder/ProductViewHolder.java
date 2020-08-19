package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.squareup.picasso.Picasso;

// viewholder class for recycler view
public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductMrp,txtProductDiscount;
public ImageView ProductImg;
public ImageButton product_like;
private ItemClickListener itemClickListener;
public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        ProductImg = itemView.findViewById(R.id.product_image);
        txtProductName = itemView.findViewById(R.id.product_name);
        //txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        txtProductMrp = itemView.findViewById(R.id.product_mrp);
        txtProductDiscount = itemView.findViewById(R.id.product_discount);
        product_like = itemView.findViewById(R.id.product_like);
       // ratingBar = itemView.findViewById(R.id.product_ratingbar);


    }

    // this method will set itemclicklistener object to listener
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
     listener.onClick(view,getAdapterPosition(),false);
    }

    public void setData(Products userModel) {
        txtProductName.setText(userModel.getName());
//        txtProductDescription.setText(userModel.getDescription());
        txtProductPrice.setText("₹" +userModel.getPrice()+"/-");
        int discount = Integer.parseInt(userModel.getMrp())-Integer.parseInt(userModel.getPrice());
        float perc = (float)((discount*100) / Integer.parseInt(userModel.getMrp()));
        txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");
        txtProductMrp.setText("Mrp: "+userModel.getMrp()+"/-");

        Picasso.get().load(userModel.getImage()).into(ProductImg);
    }

}
/*
<RatingBar
            android:layout_width="150sp"
            android:layout_height="30sp"
            android:numStars="5"
            android:id="@+id/product_ratingbar"
            android:layout_marginLeft="15sp"
            android:layout_marginTop="15dp"
            android:isIndicator="false"
            android:rating="1.5"
            android:layout_marginBottom="10sp"
            style="@style/ratingBarStyle"
            android:stepSize=".5"
            android:layout_below="@id/product_name"/>
 */