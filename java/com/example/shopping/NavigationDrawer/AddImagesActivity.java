package com.example.shopping.NavigationDrawer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AddImagesActivity extends AppCompatActivity {
    private static final int PICK_IMG = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int uploads = 0;
    private DatabaseReference categoryRef,prodref;
    private ProgressDialog progressDialog;;
    int index = 0;
    //TextView textView;
    Button choose,send;
    private String SubCategoryName,CategoryName,productRandomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        productRandomKey = getIntent().getStringExtra("productRandomKey");
        CategoryName = getIntent().getStringExtra("CategoryName");
        SubCategoryName = getIntent().getStringExtra("SubCategoryName");

        categoryRef = FirebaseDatabase.getInstance().getReference().child("Product Category");
        prodref = FirebaseDatabase.getInstance().getReference().child("Products");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ..........");
        choose = findViewById(R.id.choose);
        send = findViewById(R.id.upload);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose(view);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload(view);
            }
        });
    }

    public void choose(View view) {
        //we will pick images
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMG);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        ImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    choose.setVisibility(View.GONE);
                }

            }

        }

    }

    @SuppressLint("SetTextI18n")
    public void upload(View view) {

         progressDialog.show();
        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("Product Images");
        for (uploads=0; uploads < ImageList.size(); uploads++) {
            Uri Image  = ImageList.get(uploads);
            final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment()+productRandomKey);

            imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url);
                        }
                    });

                }
            });


        }


    }

    private void SendLink(String url) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        prodref.child(productRandomKey).child("Images").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();
                send.setVisibility(View.GONE);
                ImageList.clear();
            }
        });

        progressDialog.show();
categoryRef.child(CategoryName).child(SubCategoryName).child(productRandomKey).child("Images")
        .push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {

        progressDialog.dismiss();
        send.setVisibility(View.GONE);
        ImageList.clear();
    }
});

    }
}
