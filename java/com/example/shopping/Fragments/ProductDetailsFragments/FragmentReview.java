package com.example.shopping.Fragments.ProductDetailsFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Reviews;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.ReviewsProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FragmentReview extends Fragment
{
    View view;
    private TextView review_heading,like_heading,review_main_heading;
    private EditText write_review_ed;
    private Button submit_review;
    //private ImageView like_btn;
    private RecyclerView recyclerView_reviews;
    private String productId;
    private DatabaseReference rev_reference,reference;
    private RecyclerView.LayoutManager layoutManager;
    private int flag =0;
    private float myRating = 0;
    private RatingBar ratingBar;


    public FragmentReview(String productId)
    {
        this.productId = productId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.review_fragment,container,false);

        initialise();

        //rev_reference = FirebaseDatabase.getInstance().getReference();

        reference = FirebaseDatabase.getInstance().getReference().child("Reviews")
        .child(productId);

        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView_reviews.setHasFixedSize(true);
        recyclerView_reviews.setLayoutManager(layoutManager);

        submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

/*like_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
change_image(view);
    }
});
*/

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rating = (int)v;
                String message = null;

                myRating = ratingBar.getRating();

                switch (rating){
                    case 1:
                        message="Sorry to hear that :(";
                        break;
                    case 2:
                        message="We shall Improve Ourselves :(";
                        break;
                    case 3:
                        message="Good Enough :(";
                        break;
                    case 4:
                        message="Great! Thank You... :(";
                        break;
                    case 5:
                        message="Awesome! You are the Best :(";
                        break;

                    default:
                }
            }
        });


        return view;
    }

    private void validateData() {
        if (write_review_ed.getText().toString().isEmpty() || myRating == 0) {
            write_review_ed.setError("Cannot be empty");
            write_review_ed.requestFocus();
        } else {

            reference.child(Prevalent.currentOnlineUser.getNumber()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        submit_review.setEnabled(false);
                        submit_review.setBackgroundColor(getResources().getColor(R.color.onboard_gray));
                        toast("You have already reviewed this product");
                    }
                    else {
                        addReviewToDatabase();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void addReviewToDatabase()
    {
    HashMap<String,Object> reviewMap = new HashMap<>();
    reviewMap.put("review",write_review_ed.getText().toString().trim());
    reviewMap.put("name", Prevalent.currentOnlineUser.getName());
    reviewMap.put("rating",String.valueOf(myRating));

    reference.child(Prevalent.currentOnlineUser.getNumber()).setValue(reviewMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
           if (task.isSuccessful()){
               toast("Review Added Successfully");
               write_review_ed.setText("");
           }
           else
               toast("Error Occured");
        }
    });
    }

private void toast(String mess){
        Toast.makeText(getContext(),mess,Toast.LENGTH_SHORT).show();
}

    @Override
    public void onStart() {
        super.onStart();

        getReviewCount();
        getAllReviews();
    }

    private void getReviewCount(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             long count = dataSnapshot.getChildrenCount();
             if (count<=1){
                 review_main_heading.setText("Found "+count+" Review");
             }
             else
             review_main_heading.setText("Found "+count+" Reviews");
                Log.d("Count",count+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllReviews()
    {
if (reference == null){
    return;
}


        FirebaseRecyclerOptions<Reviews> options = new FirebaseRecyclerOptions.Builder<Reviews>()
                .setQuery(reference, Reviews.class).build();
        //productRef has reference to products node in db

        FirebaseRecyclerAdapter<Reviews, ReviewsProductViewHolder> adapter = new FirebaseRecyclerAdapter<Reviews, ReviewsProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewsProductViewHolder holder, int position, @NonNull final Reviews model) {

                holder.txtUserName.setText("Reviewed By: "+model.getName());
                holder.txtreview.setText(model.getReview());
                holder.txtRatingBar.setRating(Float.parseFloat(model.getRating()));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });


            }

            @NonNull
            @Override
            public ReviewsProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_product_items_layout
                        , parent, false);

                ReviewsProductViewHolder viewHolder = new ReviewsProductViewHolder(view);

                return viewHolder;


            }

        };
        recyclerView_reviews.setAdapter(adapter);
        adapter.startListening();
    }

    /*
    public void change_image(View v)
    {
        ImageView iv=(ImageView)v.findViewById(R.id.review_like_btn);
        //use flag to change image
        if(flag==0)
        {
            iv.setImageResource(R.drawable.heart_vector);
            flag=1;
        }
        else
        {
            iv.setImageResource(R.drawable.heart_clicked);
            flag=0;
        }
    }
    */

    private void initialise()
    {
        review_heading = view.findViewById(R.id.review_heading);
        write_review_ed = view.findViewById(R.id.review_write_review);
        //like_heading = view.findViewById(R.id.review_rating);
        //like_btn = view.findViewById(R.id.review_like_btn);
        submit_review = view.findViewById(R.id.review_submit_review);
        recyclerView_reviews = view.findViewById(R.id.review_recycler);
        review_main_heading = view.findViewById(R.id.reviews_all_reviews_heading);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
    }
}

