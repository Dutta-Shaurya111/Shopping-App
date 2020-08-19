package com.example.shopping.Fragments.ProductDetailsFragments.AddNewProductsFragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.shopping.Admin.AdminAddNewProduct;
import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class FragmentSliderImages extends Fragment
{
    View view;

    private DatabaseReference reference;
    private ImageView imageView;
    private Button addImage;
    private ProgressDialog loadingbar;
    private Uri imageUri;
    private int REQUEST_CODE_GALLERY = 1;
    private StorageReference sliderImagesRef;
    private String productRandomKey,savecurrentDate,savecurrentTime,downloadUrl="";



    public FragmentSliderImages(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.review_slider,container,false);

        initialise();

        sliderImagesRef = FirebaseStorage.getInstance().getReference().child("Slider Images");
        reference = FirebaseDatabase.getInstance().getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                imageView.setImageResource(R.drawable.profile);
            }
        });

        return view;
    }

    private void validateData()
    {
        if (imageUri==null){
            imageView.requestFocus();
        toast("Product image is necessary");
        }
        else {
            storeImage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK && data!=null){

            imageUri =data.getData();
            imageView.setImageURI(imageUri);


        }
        else {
            toast("Please select an image");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


   private void storeImage(){
       loadingbar.setTitle("Adding New Banner");
       loadingbar.setMessage("Dear Admin,Please Wait while we add new product....");
       loadingbar.setCanceledOnTouchOutside(false);
       loadingbar.show();

       Calendar calendar = Calendar.getInstance();

    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
    savecurrentDate = currentDate.format(calendar.getTime());

    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
    savecurrentTime = currentTime.format(calendar.getTime());

    productRandomKey = savecurrentDate + savecurrentTime;

    //imageUri = ImageList.get(0);
    final StorageReference filePath = sliderImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("TAG", "In Mid task");
                        downloadUrl = uri.toString();


                        SaveProductInfoToDatabase();

                        toast("Image Saved Successfully in Storage");
                        // loadingbar.dismiss();

                    }
                });
            }
        });
    }
});
}

    private void SaveProductInfoToDatabase() {

        final HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("image",downloadUrl);

        reference.child("Slider Images").child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG","In Final task");
                   toast("Image Added Successfully in DB");
                    loadingbar.dismiss();
                }
                else {
                    loadingbar.dismiss();
                    String message=task.getException().toString();
                toast(message);
                }
            }
        });

    }


    private void initialise()
    {
        imageView = view.findViewById(R.id.slider_images);
        addImage = view.findViewById(R.id.add_image);
        loadingbar = new ProgressDialog(getContext());
    }

    public void toast(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }
}

