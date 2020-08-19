package com.example.shopping.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopping.LoginSignup.MainActivity;
import com.example.shopping.NavigationDrawer.HomepageActivity;
import com.example.shopping.R;

public class AdminProduct extends AppCompatActivity implements View.OnClickListener {
    private TextView addnewproduct,tshirts_tv,sports_tshirt_tv,watches_tv,laptops_pc_tv
            ,female_dresses_tv,glasses_tv,hats_caps_tv,shoes_all_tv,headphones_handfrees_tv,sweater_tv
            ,mobiles_tv,purses_bags_wallets_tv;
    private ImageView tshirt, sports_tshirt, sweater, female_dresses, glasses, purses, hats, shoes, mobiles, laptops, watches, headphones;
int Flag=0;
private Button logout_btn,checkOrders_btn,maintain_products_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        initialize();

        checkOrders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProduct.this,AdminNewOrdersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(AdminProduct.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            }
        });
    }

    public void onBackPressed(){
        Intent in1=new Intent(AdminProduct.this, MainActivity.class);
        in1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in1);
    }

   private void initialize(){
       addnewproduct = findViewById(R.id.addnewproduct);

       checkOrders_btn = findViewById(R.id.admin_check_new_product);
       logout_btn = findViewById(R.id.admin_logout_btn);
       maintain_products_btn = findViewById(R.id.admin_maintain_product);

       tshirts_tv = findViewById(R.id.tshirts_tv);
       sports_tshirt_tv = findViewById(R.id.sports_tshirt_tv);
       watches_tv = findViewById(R.id.watches_tv);
       laptops_pc_tv = findViewById(R.id.laptops_pc_tv);
       female_dresses_tv = findViewById(R.id.female_dresses_tv);
       glasses_tv = findViewById(R.id.glasses_tv);
       hats_caps_tv = findViewById(R.id.hats_caps_tv);
       shoes_all_tv = findViewById(R.id.shoes_all_tv);
       headphones_handfrees_tv = findViewById(R.id.headphones_handfrees_tv);
       sweater_tv = findViewById(R.id.sweater_tv);
       mobiles_tv = findViewById(R.id.mobiles_tv);
       purses_bags_wallets_tv = findViewById(R.id.purses_bags_wallets_tv);

       tshirt = findViewById(R.id.tshirt);
       sports_tshirt = findViewById(R.id.sports_tshirt);
       sweater = findViewById(R.id.sweater);
       female_dresses = findViewById(R.id.female_dresses);
       glasses = findViewById(R.id.glasses);
       purses = findViewById(R.id.purses_bags_wallets);
       hats = findViewById(R.id.hats_caps);
       shoes = findViewById(R.id.shoes_all);
       laptops = findViewById(R.id.laptops_pc);
       mobiles = findViewById(R.id.mobiles);
       headphones = findViewById(R.id.headphones_handfrees);
       watches = findViewById(R.id.watches);

       tshirt.setOnClickListener(this);
       sports_tshirt.setOnClickListener(this);
       sweater.setOnClickListener(this);
       female_dresses.setOnClickListener(this);
       glasses.setOnClickListener(this);
       purses.setOnClickListener(this);
       hats.setOnClickListener(this);
       shoes.setOnClickListener(this);
       laptops.setOnClickListener(this);
       mobiles.setOnClickListener(this);
       headphones.setOnClickListener(this);
       watches.setOnClickListener(this);
       maintain_products_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               Intent intent = new Intent(AdminProduct.this, HomepageActivity.class);
               intent.putExtra("Admin","Admin");
               startActivity(intent);

           }
       });

   }

    @Override
    public void onClick(View view) {

        if (view == tshirt) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Tshirts");
            in.putExtra("subcategory","Mens");
            startActivity(in);
        }
       else if (view == sports_tshirt) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "SportsTshirts");
            in.putExtra("subcategory", "Mens");
            startActivity(in);
        }

        else if (view==female_dresses) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Femaledresses");
            startActivity(in);
        }
        else if (view==sweater) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Sweaters");
            startActivity(in);
        }
       else if (view==glasses) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Glasses");
            startActivity(in);
        }
        else if (view==purses) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Purses");
            startActivity(in);
        }
        else if (view==hats) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Hats");
            startActivity(in);
        }
       else if (view==shoes) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Shoes");
            startActivity(in);
        }
        else if (view==laptops) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Laptops");
            startActivity(in);
        }
        else if (view==mobiles) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Mobiles");
            startActivity(in);
        }
        else if (view==headphones) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "HeadPhones");
            startActivity(in);
        }
        else if (view==watches) {
            Intent in = new Intent(AdminProduct.this, AdminAddNewProduct.class);
            in.putExtra("category", "Watches");
            startActivity(in);
        }
    }



}

