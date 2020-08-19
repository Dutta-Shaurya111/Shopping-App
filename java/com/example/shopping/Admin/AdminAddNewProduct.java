package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopping.Fragments.ProductDetailsFragments.AddNewProductsFragments.FragmentCategoriesOnly;
import com.example.shopping.Fragments.ProductDetailsFragments.AddNewProductsFragments.FragmentSliderImages;
import com.example.shopping.Fragments.ProductDetailsFragments.ViewPagerAdapter;
import com.example.shopping.NavigationDrawer.AddImagesActivity;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class AdminAddNewProduct extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_MULTIPLE_IMAGES=101 ;
    private String CategoryName,SubCategoryName, Pname, Description, Price, savecurrentDate,
            savecurrentTime,Mrp,size,color;
    private Button addnewproduct_btn;//,upload_images;
    private ImageView select_image;//,select_images;
    private EditText product_name,product_subcategory, product_description, product_price,
            product_mrp,product_size,product_color,product_material,product_type,product_style,product_design;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadUrl,material,type,style,design;
    private StorageReference productImagesRef;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ProgressDialog loadingbar,progressDialog;
    private DatabaseReference ProductRef,prodref;
    private FirebaseAuth mAuth;
    private int upload_count=0;

    private int uploads = 0;
    private DatabaseReference databaseReference;
    int index = 0;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

          CategoryName = getIntent().getExtras().get("category").toString();
       // SubCategoryName = getIntent().getExtras().get("subcategory").toString();

        progressDialog = new ProgressDialog(this);
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        ProductRef = FirebaseDatabase.getInstance().getReference();
        prodref = FirebaseDatabase.getInstance().getReference().child("Products");

        init();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // adding fragments to adapter

        adapter.AddFragment(new FragmentSliderImages(),"SliderImages");

        adapter.AddFragment(new FragmentCategoriesOnly(),"AddCategories");
       // adapter.AddFragment();

        // adapter setup
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        select_image.setOnClickListener(this);
        addnewproduct_btn.setOnClickListener(this);
    }
    private void init()
    {
        select_image = this.<ImageView>findViewById(R.id.select_product_image);
        product_name = this.<EditText>findViewById(R.id.product_name);
        product_description = this.<EditText>findViewById(R.id.product_Description);
        product_subcategory = this.<EditText>findViewById(R.id.product_subcategory);
        product_price = this.<EditText>findViewById(R.id.product_price);
        product_material = this.<EditText>findViewById(R.id.material_type);
        product_type = findViewById(R.id.type);
        product_style = findViewById(R.id.product_style);
        product_design = findViewById(R.id.product_design);

        addnewproduct_btn = this.<Button>findViewById(R.id.add_new_product);
        product_mrp = findViewById(R.id.product_mrp);
        product_size = findViewById(R.id.product_size);
        product_color = findViewById(R.id.product_color);



        loadingbar = new ProgressDialog(this);
        viewPager = findViewById(R.id.viewpager_add_new);
        tabLayout = findViewById(R.id.tab_layout2);

    }

    @Override
    public void onClick(View view) {
        if
        (view==select_image) {
            chooseOne(view);
        }

        else if (view==addnewproduct_btn){
             ValidateProductData();
            try {


                   // Toast.makeText(AdminAddNewProduct.this," Data Added successfully...",Toast.LENGTH_SHORT).show();
                    product_name.setText("");
                    product_description.setText("");
                    product_price.setText("");
                    product_mrp.setText("");
                product_subcategory.setText("");
                product_size.setText("");
                product_color.setText("");
                product_material.setText("");
                product_type.setText("");
                product_style.setText("");
                product_design.setText("");
                    select_image.setImageResource(R.drawable.profile);

            }

            catch (Exception e){
                e.printStackTrace();
            }

        }
    }


public void chooseOne(View view){
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent, galleryPick);
}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == galleryPick){

if (resultCode == RESULT_OK) {
    imageUri =data.getData();
    select_image.setImageURI(imageUri);

}

            }
            else {
                Toast.makeText(AdminAddNewProduct.this,"You dont have permission to access the gallery",Toast.LENGTH_SHORT).show();
            }
        }


    public void upload(View view) {

        //textView.setText("Please Wait ... If Uploading takes Too much time please the button again ");
        progressDialog.show();
        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("ImageFolder");
        for (uploads=0; uploads < ImageList.size(); uploads++) {

            Uri Image  = ImageList.get(uploads);

            //Code for compressing image uploaded to firebase storage
            File file = new File(SiliCompressor.with(this).compress(FileUtils
                    .getPath(this,Image),new File(this.getCacheDir(),"temp")));

            // save the uri that you get from the file in imageuri
            Image = Uri.fromFile(file);

            final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

            filePath.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url,productRandomKey);
                        }
                    });

                }
            });

            prodref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i=0;
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (i == 0) {
                            String key = ds.getKey();
                            Log.d("Key is ",key);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


    }

    private void SendLink(String url,final String productRandomKey) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products")
                .child(productRandomKey).child("Images");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    // textView.setText("Image Uploaded Successfully");

                    ImageList.clear();


                    // textView.setText("Image Uploaded Successfully");
                    //send.setVisibility(View.GONE);

                }

            }
        });

    }

    private void ValidateProductData()
    {
        Description=product_description.getText().toString();
        Price=product_price.getText().toString();
        Pname=product_name.getText().toString();
        Mrp = product_mrp.getText().toString();
        size = product_size.getText().toString();
        color = product_color.getText().toString();
        SubCategoryName = product_subcategory.getText().toString().trim();
        material = product_material.getText().toString();
        type = product_type.getText().toString();
        style = product_style.getText().toString();
        design = product_design.getText().toString();


        if (imageUri==null){
            select_image.requestFocus();
            Toast.makeText(AdminAddNewProduct.this,"Product image is necessary",Toast.LENGTH_SHORT).show();
        }
         if (TextUtils.isEmpty(Description))
        {
        product_description.setError("Required Field");
        product_description.requestFocus();
        }
        else if (TextUtils.isEmpty(Price))
        {
            product_price.setError("Required Field");
            product_price.requestFocus();}

        else if (TextUtils.isEmpty(Pname))
        {
            product_name.setError("Required Field");
            product_name.requestFocus();}
        else if (TextUtils.isEmpty(Mrp))
        {
            product_mrp.setError("Required Field");
            product_mrp.requestFocus();}
        else if (TextUtils.isEmpty(size))
        {
            product_size.setError("Required Field");
            product_size.requestFocus();}
        else if (TextUtils.isEmpty(color))
        {
            product_color.setError("Required Field");
            product_color.requestFocus();}
        else if (TextUtils.isEmpty(SubCategoryName)){
            product_subcategory.setError("Required");
            product_subcategory.requestFocus();
        }
         else if (TextUtils.isEmpty(material))
         {
             product_material.setError("Required Field");
             product_material.requestFocus();}
         else if (TextUtils.isEmpty(type))
         {
             product_type.setError("Required Field");
             product_type.requestFocus();}
         else if (TextUtils.isEmpty(style))
         {
             product_style.setError("Required Field");
             product_style.requestFocus();}
         else if (TextUtils.isEmpty(design)){
             product_design.setError("Required");
             product_design.requestFocus();
         }
        else
            {
               // compressAndUpload();
           StoreProductInfo();

        }
    }


    private void StoreProductInfo() {
        loadingbar.setTitle("Adding New Product");
        loadingbar.setMessage("Dear Admin,Please Wait while we add new product....");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        savecurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = savecurrentDate + savecurrentTime;



        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        // Code for compressing image uploaded to firebase storage
        File file = new File(SiliCompressor.with(this).compress(FileUtils
                .getPath(this,imageUri),new File(this.getCacheDir(),"temp")));

        // save the uri that you get from the file in imageuri
        imageUri = Uri.fromFile(file);

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

                                toast("Image Saved Successfully");
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
        productMap.put("pid",productRandomKey);
        productMap.put("date",savecurrentDate);
        productMap.put("time",savecurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadUrl);
        productMap.put("category",CategoryName);
            productMap.put("subcategory",SubCategoryName);
        productMap.put("price",Price);
            productMap.put("price1",Integer.parseInt(Price));
        productMap.put("mrp",Mrp);
            productMap.put("size",size);
            productMap.put("color",color);
            productMap.put("name",Pname);
            productMap.put("material",material);
            productMap.put("type",type);
            productMap.put("style",style);
            productMap.put("design",design);

        ProductRef.child("Products").child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("TAG","In Final task");
                    // Intent intent=new Intent(AdminAddNewProduct.this,AdminProduct.class);
                    //startActivity(intent);

                    ProductRef.child("Product Category").child(CategoryName).child(SubCategoryName)
                            .child(productRandomKey).updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingbar.dismiss();
                                        Intent send = new Intent(AdminAddNewProduct.this, AddImagesActivity.class);
                                        send.putExtra("productRandomKey",productRandomKey);
                                        send.putExtra("CategoryName",CategoryName);
                                        send.putExtra("SubCategoryName",SubCategoryName);
                                        startActivity(send);
                                        Toast.makeText(AdminAddNewProduct.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
                else {
                    loadingbar.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminAddNewProduct.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

   public void toast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }


}

