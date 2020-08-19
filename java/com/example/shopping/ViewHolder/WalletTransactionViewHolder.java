package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Interface.ItemClickListener;
import com.example.shopping.R;

// viewholder class for recycler view
public class WalletTransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtProductId,txtReceiverName,txtAmount,txtTransactionStatus,txtTransactionId,
    txtNote,txtUpiId;
public ItemClickListener listener;

    public WalletTransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        // defining fields present in cardview to display all products
        txtNote = itemView.findViewById(R.id.txtNote);
        txtAmount = itemView.findViewById(R.id.txtAmount);

        txtProductId = itemView.findViewById(R.id.txtProductId);
        txtReceiverName = itemView.findViewById(R.id.txtReceiverName);
        txtTransactionStatus = itemView.findViewById(R.id.txtTransactionStatus);
        txtTransactionId = itemView.findViewById(R.id.txtTransactionId);
        txtUpiId = itemView.findViewById(R.id.txtUpiId);

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
