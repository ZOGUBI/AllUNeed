package com.example.zogubi.alluneed.AdminPanel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zogubi.alluneed.HomeActivity;
import com.example.zogubi.alluneed.MainActivity;
import com.example.zogubi.alluneed.R;

import io.paperdb.Paper;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirts , sportTShirts , femaleDresses , sweathers;
    private ImageView glasses , purssesWallet , hats , shoes;
    private ImageView headphones , laptops , watches , mobilePhones;
    private Button logoutButton , checkOrderButton , maintainProductsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts = (ImageView) findViewById(R.id.t_shirts);
        sportTShirts = (ImageView) findViewById(R.id.sport_t_shirts);
        femaleDresses = (ImageView) findViewById(R.id.femaledresses);
        sweathers = (ImageView) findViewById(R.id.sweathers);
        glasses = (ImageView) findViewById(R.id.glasses);
        purssesWallet = (ImageView) findViewById(R.id.purses_bag);
        hats = (ImageView) findViewById(R.id.hats);
        shoes = (ImageView) findViewById(R.id.shoes);
        headphones = (ImageView) findViewById(R.id.headphones);
        laptops = (ImageView) findViewById(R.id.laptops);
        watches = (ImageView) findViewById(R.id.watches);
        mobilePhones = (ImageView) findViewById(R.id.mobile_phones);
        logoutButton = (Button) findViewById(R.id.admin_logout_button);
        checkOrderButton = (Button) findViewById(R.id.check_order_button);
        maintainProductsButton = (Button) findViewById(R.id.maintain_product_button);







            tShirts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                    intent.putExtra("category" , "tShirts");
                    startActivity(intent);
            }
        });

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "femaleDresses");
                startActivity(intent);
            }
        });



        sportTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "sportTShirts");
                startActivity(intent);
            }
        });

        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "sweathers");
                startActivity(intent);
            }
        });


        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "glasses");
                startActivity(intent);
            }
        });



        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "hats");
                startActivity(intent);
            }
        });



        purssesWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "purssesWallet");
                startActivity(intent);
            }
        });


        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "headphones");
                startActivity(intent);
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "shoes");
                startActivity(intent);
            }
        });



        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "laptops");
                startActivity(intent);
            }
        });



        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "watches");
                startActivity(intent);
            }
        });



        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminAddProductActivity.class);
                intent.putExtra("category" , "mobilePhones");
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(AdminCategoryActivity.this , MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(intent);
            }
        });

        checkOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this , AdminNewOrdersActivity.class);
                startActivity(intent);

            }
        });

        maintainProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override     public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivity.this , HomeActivity.class);
                intent.putExtra("Admin" , "Admin");
                startActivity(intent);

            }
        });



    }
}
