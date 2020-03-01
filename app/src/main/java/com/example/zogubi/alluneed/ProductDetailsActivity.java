package com.example.zogubi.alluneed;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.zogubi.alluneed.Model.Products;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProductDetailsActivity extends AppCompatActivity {
   // private FloatingActionButton addToCart;
    private String productId = "";
    private TextView pNameDetails , pPriceDetails , pDescriptionDetails , tlTextview;
    private ElegantNumberButton pCountButton;
    private Button pAddToCartButton ;
    public String saveCurrentTime,saveCurrentDate;
    private String state = "Normal";

    //swap Images

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    static String [] images = new String[8];


    private void GetProductDetails(final String productId) {

        final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    pNameDetails.setText(products.getPname());
                    pPriceDetails.setText(products.getPrice());
                    pDescriptionDetails.setText(products.getDescription());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productId = getIntent().getStringExtra("pid");
        pNameDetails = (TextView) findViewById(R.id.pNameDetails);
        pDescriptionDetails = (TextView) findViewById(R.id.pDescriptionDetails);
        pPriceDetails = (TextView) findViewById(R.id.pPriceDetails);
        pCountButton = (ElegantNumberButton) findViewById(R.id.pCountButton);
        tlTextview = findViewById(R.id.TL);
        pAddToCartButton = findViewById(R.id.pAddToCartButton);
        ///////////////////////////////////////



        final DatabaseReference numOfItemRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");
        numOfItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(productId)){
                    numOfItemRef.child(productId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String numOfItemsExists = dataSnapshot.child("quantity").getValue().toString();
                        pCountButton.setNumber(String.valueOf(numOfItemsExists));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//*********************************************


//********************************************************


        GetProductDetails(productId);


        pAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("Order Placed") || state.equals("Order Shipped")) {

                    Toast.makeText(ProductDetailsActivity.this, "Sorry,You Can't Purchase Until Previous Order got Approved", Toast.LENGTH_LONG).show();
                }else{
                    AddingTCartList();

                }
            }
        });





    }




    @Override
    protected void onStart() {


        super.onStart();

//********************************************************
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Products");

        myRef.child(productId).child("Images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int numberOfImages = (int) dataSnapshot.getChildrenCount();
                images = new String[numberOfImages];
                for ( int i = 0 ; i <numberOfImages ; i++){
                    final int j = i ;
                    myRef.child(productId).child("Images").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url =dataSnapshot.child("image"+(j+1)).getValue().toString();
                            images[j] =  url;
                            Log.i("image"+(j+1),images[j]);

                            viewPager = (ViewPager) findViewById(R.id.viewPager);
                            adapter = new ViewPagerAdapter(ProductDetailsActivity.this , images);
                            viewPager.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//********************************************************



        CheckOrderState();

    }

    private void AddingTCartList() {



        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid" , productId);
        cartMap.put("pname" , pNameDetails.getText().toString());
        cartMap.put("price" , pPriceDetails.getText().toString());
        cartMap.put("date" , saveCurrentDate);
        cartMap.put("time" , saveCurrentTime);
        cartMap.put("quantity" , pCountButton.getNumber());
        cartMap.put("discount" , "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products").child(productId).updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(ProductDetailsActivity.this, "Added To Cart Successfully", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });

                            }
                    }
                });




    }


    private void CheckOrderState (){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shipmentState  = dataSnapshot.child("state").getValue().toString();
                    if (shipmentState.equals("shipped")) {

                        state = "Order Shipped";

                    }else if (shipmentState.equals("not shipped")) {

                        state = "Order Placed";

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    }



