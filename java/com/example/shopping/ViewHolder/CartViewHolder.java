package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName , txtProductPrice , txtProductQuantity ;
    private ItemClickListener itemclickListener;
    public ImageView productImg;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);

        productImg = itemView.findViewById(R.id.cart_product_image);

    }

    @Override
    public void onClick(View view) {
        itemclickListener.onClick(view , getAdapterPosition() ,false);
    }

    // this method will set itemclicklistner object to listener
    public void setItemClickListner(ItemClickListener itemclickListener){
        this.itemclickListener = itemclickListener;
    }
}
