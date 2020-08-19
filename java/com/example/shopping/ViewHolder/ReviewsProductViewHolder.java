package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

// viewholder class for recycler view
public class ReviewsProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtreview,txtUserName;
public RatingBar txtRatingBar;
public ItemClickListener listener;

    public ReviewsProductViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        txtreview = itemView.findViewById(R.id.review_text);
        txtUserName = itemView.findViewById(R.id.review_user_name);
        txtRatingBar = itemView.findViewById(R.id.review_ratingbar);
        txtRatingBar.setEnabled(false);


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
