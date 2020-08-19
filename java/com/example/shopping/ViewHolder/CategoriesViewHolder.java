package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

// viewholder class for recycler view
public class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtProductName,txtProductDiscount,txtProductPrice,txtProductMrp;
public ImageButton LikeImg;
public ImageView ProductImg;
public ItemClickListener listener;

    public CategoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        ProductImg = itemView.findViewById(R.id.categories_product_image);
        LikeImg = itemView.findViewById(R.id.categories_product_like);
        txtProductName = itemView.findViewById(R.id.categories_product_name);
        txtProductMrp = itemView.findViewById(R.id.categories_product_mrp);
        txtProductDiscount = itemView.findViewById(R.id.categories_product_discount);
        txtProductPrice = itemView.findViewById(R.id.categories_product_price);

    }

    // this method will set itemclicklistener object to listener
    public void settItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
     listener.onClick(view,getAdapterPosition(),false);
    }
}
