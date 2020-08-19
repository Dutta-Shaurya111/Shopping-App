package com.example.shopping.Fragments.ProductDetailsFragments.helpFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Model.Faqs;
import com.example.shopping.Model.Reviews;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.FaqsViewHolder;
import com.example.shopping.ViewHolder.ReviewsProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FragmentFaqs extends Fragment
{
    private View view;
    private TextView heading;
    private Button referral_btn,return_btn,refund_btn;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private RecyclerView.LayoutManager layoutManager;


    public FragmentFaqs()
    {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.faqs_fragment,container,false);

         initialise();
         reference = FirebaseDatabase.getInstance().getReference().child("Faqs");
         layoutManager = new LinearLayoutManager(getContext());
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(layoutManager);

        referral_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference = FirebaseDatabase.getInstance().getReference().child("Faqs").child("Referral");
                getAll(reference);
            }
        });

        refund_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("Faqs").child("Refund");
                getAll(reference);
            }
        });

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference().child("Faqs").child("Return");
                getAll(reference);
            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();


        reference = FirebaseDatabase.getInstance().getReference().child("Faqs").child("Refund");
        getAll(reference);

    }

    private void getAll(Query query)
    {
        FirebaseRecyclerOptions<Faqs> options = new FirebaseRecyclerOptions.Builder<Faqs>()
                .setQuery(query, Faqs.class).build();
        //productRef has reference to products node in db

        FirebaseRecyclerAdapter<Faqs, FaqsViewHolder> adapter = new FirebaseRecyclerAdapter<Faqs, FaqsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FaqsViewHolder holder, int position, @NonNull final Faqs model) {

                holder.txtQues.setText(model.getQues());
                holder.txtAnswer.setText(model.getAns());


            }

            @NonNull
            @Override
            public FaqsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faqs_items_layout
                        , parent, false);

                FaqsViewHolder viewHolder = new FaqsViewHolder(view);

                return viewHolder;


            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initialise()
    {
        heading = view.findViewById(R.id.faqs_heading);
        recyclerView = view.findViewById(R.id.faqs_recycler);
        referral_btn = view.findViewById(R.id.btn_referral);
        refund_btn = view.findViewById(R.id.btn_refund);
        return_btn = view.findViewById(R.id.btn_return);
    }
}


