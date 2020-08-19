package com.example.shopping.Interface;

import android.view.View;

public interface ItemClickListener
{
    void onClick(View view , int position,boolean isLongClick);

    void onItemClick(int position);
}
