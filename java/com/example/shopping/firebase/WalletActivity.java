package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Prevalent.Transactions;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.WalletTransactionViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletActivity extends AppCompatActivity {
private RecyclerView recyclerView,recyclerView2;
private LinearLayoutManager layoutManager,layoutManager2;
private TextView number,money,wallet,name,all_transactions;
private DatabaseReference productRef,transactionRef;
private ImageView img1,img2;
private EditText editText;
private DatabaseReference walletRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

walletRef = FirebaseDatabase.getInstance().getReference().child("Wallet");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        transactionRef = FirebaseDatabase.getInstance().getReference().child("Order Payments");

      initialise();

recyclerView.setHasFixedSize(false);
layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
recyclerView.setLayoutManager(layoutManager);
recyclerView.setNestedScrollingEnabled(false);


    }

    private void initialise()
    {
        recyclerView = findViewById(R.id.recycler_view_1);
        money = findViewById(R.id.money);
        name = findViewById(R.id.name1);
        number = findViewById(R.id.number);
        wallet = findViewById(R.id.wallet);
        all_transactions = findViewById(R.id.all_transactions);
        img1 = findViewById(R.id.img123);
        img2 = findViewById(R.id.img1234);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {

            walletRef.child(Prevalent.currentOnlineUser.getNumber()).child("money").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                money.setText("₹ " + dataSnapshot.child("money").getValue(String.class));
                                name.setText(Prevalent.currentOnlineUser.getName());
                                number.setText(Prevalent.currentOnlineUser.getNumber());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );


            // FirebaseRecyclerOptions is required to work with FirebaseRecyclerAdapter
            FirebaseRecyclerOptions<Transactions> options = new FirebaseRecyclerOptions.Builder<Transactions>()
                    .setQuery(productRef, Transactions.class).build();
            //productRef has reference to products node in db

            FirebaseRecyclerAdapter<Transactions, WalletTransactionViewHolder> adapter = new FirebaseRecyclerAdapter<Transactions, WalletTransactionViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final WalletTransactionViewHolder holder, int position, @NonNull final Transactions model) {

                    holder.txtProductId.setText("Product Id: " + model.getProductId());
                    holder.txtNote.setText("Note: " + model.getNote());
                    holder.txtAmount.setText("₹ " + model.getAmount());
                    holder.txtReceiverName.setText("ReceiverName: " + model.getReceiverName());

                    holder.txtTransactionStatus.setText("Transaction Status: " + model.getTransactionStatus());

                    holder.txtTransactionId.setText("TransactionId: " + model.getTransactionId());
                    holder.txtUpiId.setText("UpiId: " + model.getUpiId());


                }

                @NonNull
                @Override
                public WalletTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_items_layout
                            , parent, false);

                    WalletTransactionViewHolder viewHolder = new WalletTransactionViewHolder(view);

                    return viewHolder;


                }
            };

            recyclerView.setAdapter(adapter);
            adapter.startListening();

            transactionRef.child(Prevalent.currentOnlineUser.getNumber()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }



}
