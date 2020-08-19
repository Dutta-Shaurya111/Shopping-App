package com.example.shopping.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductsRVAdapter extends RecyclerView.Adapter<ProductsRVAdapter.ProductViewHolder> {

    private List<Products> userModels;
    private ItemClickListener itemClickListener;

    public ProductsRVAdapter(ItemClickListener itemClickListener) {
        this.userModels = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_items_layout, parent, false),itemClickListener);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.setData(userModels.get(position));

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public void addAll(List<Products> newUsers) {
        int initialSize = userModels.size();
        userModels.addAll(newUsers);
        notifyItemRangeInserted(initialSize, newUsers.size());
    }


    public void removeData(){
        userModels.clear();
    }

    public String getLastItemId() {
        return userModels.get(userModels.size() - 1).getPid();
    }

   // Now we need to query the records from the Firebase Realtime Database, I have created a separate function for that which takes the nodeId and brings the specified number of records after that nodeId. The function is simple and looks like this:

    public class ProductViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductMrp,txtProductDiscount;
        public ImageView ProductImg,product_like;


        public ProductViewHolder(@NonNull View itemView, final ItemClickListener itemClickListener) {
            super(itemView);

            // defining fields present in cardview to display all products
            ProductImg = itemView.findViewById(R.id.product_image);
            txtProductName = itemView.findViewById(R.id.product_name);
            //txtProductDescription = itemView.findViewById(R.id.product_description);
            txtProductPrice = itemView.findViewById(R.id.product_price);
            txtProductMrp = itemView.findViewById(R.id.product_mrp);
            txtProductDiscount = itemView.findViewById(R.id.product_discount);
            product_like = itemView.findViewById(R.id.product_like);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }

        public void setData(Products userModel) {
            txtProductName.setText(userModel.getName());
            txtProductPrice.setText("₹" +userModel.getPrice()+"/-");
            int discount = Integer.parseInt(userModel.getMrp())-Integer.parseInt(userModel.getPrice());
            float perc = (float)((discount*100) / Integer.parseInt(userModel.getMrp()));
            txtProductDiscount.setText(String.valueOf((int)perc)+"% OFF");
            txtProductMrp.setText("Mrp: "+userModel.getMrp()+"/-");

            Picasso.get().load(userModel.getImage()).into(ProductImg);
        }

    }


}
