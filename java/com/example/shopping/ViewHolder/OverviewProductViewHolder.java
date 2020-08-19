package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

// viewholder class for recycler view
public class OverviewProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductMrp,txtProductDiscount;
public ImageView ProductImg,product_like;
public ItemClickListener listener;

    public OverviewProductViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        ProductImg = itemView.findViewById(R.id.overview_product_image);
        txtProductName = itemView.findViewById(R.id.overview_product_name);
        txtProductDescription = itemView.findViewById(R.id.overview_product_description);
        txtProductPrice = itemView.findViewById(R.id.overview_product_price);
        txtProductMrp = itemView.findViewById(R.id.overview_product_mrp);
        txtProductDiscount = itemView.findViewById(R.id.overview_product_discount);
        product_like = itemView.findViewById(R.id.overview_product_like);

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
}
