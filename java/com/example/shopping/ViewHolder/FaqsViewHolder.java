package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

// viewholder class for recycler view
public class FaqsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtQuesHeading,txtQues,txtAnswerHeading,txtAnswer;
public ItemClickListener listener;

    public FaqsViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        txtQuesHeading = itemView.findViewById(R.id.faqs_ques_heading);
        txtAnswerHeading = itemView.findViewById(R.id.faqs_ans_heading);

        txtQues = itemView.findViewById(R.id.faqs_ques);
        txtAnswer = itemView.findViewById(R.id.faqs_ans);

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
