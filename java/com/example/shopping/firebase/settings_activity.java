package com.example.shopping.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.LoginSignup.ResetPasswordActivity;
import com.example.shopping.Model.Prevalent.Prevalent;
import com.example.shopping.Model.Users;
import com.example.shopping.NavigationDrawer.HomepageActivity;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings_activity extends AppCompatActivity implements View.OnClickListener {

    private TextView update,close,profile_change;
    private CircleImageView profile_image;
    private EditText phonenumber,fullname,fulladdress;
private Button security_questions_btn;

    private Uri imageuri;
    private String myUrl="";
    private StorageTask uploadtask;
    private StorageReference storageprofilepicreference;
    private String checker="";

    private static final int REQUEST_CODE_GALLERY=999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity);

        storageprofilepicreference= FirebaseStorage.getInstance().getReference().child("Profile pictures");

        initialise();

        profile_change.setOnClickListener(this);
        close.setOnClickListener(this);
        update.setOnClickListener(this);
        security_questions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings_activity.this, ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                // we will put this intent so that when user clicks on forgot password,user will be directed
                // to this activity where he shall have to ans few security questions and in this activity
                // user can change the security questions to be asked
                startActivity(intent);
            }
        });
       // imageView.setOnClickListener(this);

        UserInfoDisplay(profile_image,fullname,phonenumber,fulladdress);
    }

    private void initialise()
    {
        close= this.<TextView>findViewById(R.id.close_settings);
        update= this.<TextView>findViewById(R.id.update_settings);
        profile_change= this.<TextView>findViewById(R.id.settings_profile_change);

        security_questions_btn = findViewById(R.id.security_questions_btn);

        profile_image= this.<CircleImageView>findViewById(R.id.settings_profile_image);
        fullname= this.<EditText>findViewById(R.id.setting_full_name);
        phonenumber= this.<EditText>findViewById(R.id.settings_phone_number);
        fulladdress= this.<EditText>findViewById(R.id.settings_address);
    }

    private void UserInfoDisplay(final CircleImageView imageView, final EditText fullname, final EditText phonenumber, EditText address)
    {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getNumber());

        // to get/display information from db , we use valueEventListener
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // means if the user exists
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        Users user = dataSnapshot.getValue(Users.class);
                        // one and the same thing in commented code,just tried
                        /*
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String number=dataSnapshot.child("number").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();
                        */
                        Picasso.get().load(user.getImage()).into(profile_image);
                        fullname.setText(user.getName());
                        phonenumber.setText(user.getNumber());
                        fulladdress.setText(user.getAddress());
/*
                      Picasso.get().load(image).into(profile_image);
                        fullname.setText(name);
                        phonenumber.setText(number);
                        fulladdress.setText(address);
                        */

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view==close)
        {
            finish();
          //  Intent intent=new Intent(settings_activity.this,ssample.class);
           // startActivity(intent);
        }
        else if (view==update)
        {
           /* String a=phonenumber.getText().toString();
            String b=fullname.getText().toString();
            String c=fulladdress.getText().toString();
            int d=imageView.getId();
*/

           if (checker.equals("clicked"))
           {
               userInfoSaved();
               // for validation purpose
           }

else {
    // ie donot upload image , rest of the data will be uploaded
    updateOnlyUserInfo();
           }
        }


        else if (view==profile_change)
        {
            // update string checker to clicked
            checker="clicked";

            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,REQUEST_CODE_GALLERY);

           // ActivityCompat.requestPermissions(settings_activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GALLERY);


           /* profile_image.setImageURI(imageuri);

Intent galleryintent=new Intent(Intent.ACTION_PICK);
galleryintent.setType("image/*");
startActivityForResult(galleryintent,REQUEST_CODE_GALLERY);*/

        }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            //Uri
            imageuri = data.getData();
             profile_image.setImageURI(imageuri);
            //userInfoSaved();
            // this will open crop-image-activity that we have additionally added in manifest
            // This will allow us to crop our image
        }
    }


    private void userInfoSaved()
    {
if (TextUtils.isEmpty(fullname.getText().toString())){
    fullname.setError("Name is mandatory");
    fullname.requestFocus();
}
       else if (TextUtils.isEmpty(phonenumber.getText().toString())){
    phonenumber.setError("Name is mandatory");
    phonenumber.requestFocus();
       }

        if (TextUtils.isEmpty(fulladdress.getText().toString())){
            fulladdress.setError("Name is mandatory");
            fulladdress.requestFocus();
        }
    else if (checker.equals("clicked")){
        uploadImage();
        }
    }

    private void uploadImage()
    {
        /*
        final ProgressDialog progressDialog=new ProgressDialog(this);
       progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait,while we are updating your profile info....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        */


        if (imageuri!=null)
        {
            finish();
            final StorageReference fileref = storageprofilepicreference
                    .child(Prevalent.currentOnlineUser.getNumber() +".jpg");

            //it will save image to folder in firebasestorage
            uploadtask=fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    myUrl = uri.toString();

                                /*
                                final String downloadUrl = uri.toString();
                                */
                                    final DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            HashMap<String,Object> usermap=new HashMap<>();
                                            usermap.put("name",fullname.getText().toString());
                                            usermap.put("number",phonenumber.getText().toString());
                                            usermap.put("password",Prevalent.currentOnlineUser.getPassword());
                                            usermap.put("address",fulladdress.getText().toString());
                                            usermap.put("image",myUrl);
                                            usermap.put("invite",dataSnapshot.child(Prevalent.currentOnlineUser.getNumber()).child("invite")
                                                    .getValue(String.class));

                                            reference.child(Prevalent.currentOnlineUser.getNumber())
                                                    .setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // progressDialog.dismiss();
                                                        toast("Profile Info Updated Successfully..");
                                                    } else {

                                                        //progressDialog.dismiss();
                                                        toast("Error : " + task.getException().toString());

                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                    });
                }
                                                                      }
            );



        }
        else {
            Toast.makeText(settings_activity.this,"Image Is Not Selected",Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOnlyUserInfo()
    {
        final DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> usermap=new HashMap<>();
                usermap.put("name",fullname.getText().toString());
                usermap.put("number",phonenumber.getText().toString());
                usermap.put("address",fulladdress.getText().toString());
                usermap.put("invite",dataSnapshot.child(Prevalent.currentOnlineUser.getNumber()).child("invite")
                .getValue(String.class));
                reference.child(Prevalent.currentOnlineUser.getNumber()).updateChildren(usermap).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(settings_activity.this,HomepageActivity.class));
                                Toast.makeText(settings_activity.this,"Profile Info Updated Successfully..",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

void toast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
}
}

