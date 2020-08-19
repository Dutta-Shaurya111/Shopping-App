package com.example.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;

public class FragmentHome extends Fragment {

    private View view;
    private DatabaseReference productRef;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private String CategoryName="",SubCategoryName="";
    private ImageView img,img2;
    private int flag = 0;

    private String userType="";


    FragmentHome(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.home_fragment,container,false);
/*
        checkInternetConnection();

        // initialising paper for using remember_me
        Paper.init(getContext());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){
            // ie only if we are coming to this activity from admin activity , then only it will work
            userType = getIntent().getStringExtra("Admin");
        }

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

*/

        return view;
    }
}
