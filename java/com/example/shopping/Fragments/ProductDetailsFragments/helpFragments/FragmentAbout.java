package com.example.shopping.Fragments.ProductDetailsFragments.helpFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.google.firebase.database.DatabaseReference;

public class FragmentAbout extends Fragment
{
    private View view;
    private TextView heading;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private RecyclerView.LayoutManager layoutManager;


    public FragmentAbout()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.faqs_fragment,container,false);
        return view;

    }



    private void initialise()
    {
        heading = view.findViewById(R.id.faqs_heading);
        recyclerView = view.findViewById(R.id.faqs_recycler);
    }
}