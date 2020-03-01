package com.example.zogubi.alluneed.AdminPanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zogubi.alluneed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
    private Button applyChangeButton , deleteButton ;
    private EditText nameMaintain , priceMaintain , descriptionMaintain;
    private ImageView productImage;
    private String productId = "";
    private DatabaseReference productRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        applyChangeButton = findViewById(R.id.apply_change_button);
        deleteButton = findViewById(R.id.delete_product_button);

        productId = getIntent().getStringExtra("pid");
        nameMaintain = findViewById(R.id.product_name_maintain);
        priceMaintain = findViewById(R.id.product_price_maintain);
        descriptionMaintain = findViewById(R.id.product_description_maintain);
        productImage = findViewById(R.id.product_image_maintain);
        productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        DisplaySpecificProductInfo();

        applyChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApplyChanges();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteProduct();
            }
        });


    }

    private void DeleteProduct() {

        CharSequence options[] = new CharSequence[]{
                "Yes"
                ,"No"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
        builder.setTitle("Are You Sure You Want To Delete This Product ?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0){
                    productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AdminMaintainProductsActivity.this, "Product Has Been Deleted Successfully ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AdminMaintainProductsActivity.this ,AdminCategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else{finish();}
            }
        });
        builder.show();

    }





    private void ApplyChanges() {



        final String newName = nameMaintain.getText().toString();
        final String newPrice  = priceMaintain.getText().toString();
        final String newDescription  = descriptionMaintain.getText().toString();

        if (newName.equals("")){
            Toast.makeText(this, "Please Enter A name For The Product", Toast.LENGTH_SHORT).show();
        }else if (newPrice.equals("")){
            Toast.makeText(this, "Please Enter A Price For The Product", Toast.LENGTH_SHORT).show();
        }else if (newDescription.equals("")){
            Toast.makeText(this, "Please Enter A Description For The Product", Toast.LENGTH_SHORT).show();
        }else{







            CharSequence options[] = new CharSequence[]{
                    "Yes"
                    ,"No"
            };
            final AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
            builder.setTitle("Are You Sure You Want To Save Changes On This Product ?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0){

                        HashMap<String , Object> productMap = new HashMap<>();

                        productMap.put("pid", productId);
                        productMap.put("description", newDescription);
                        productMap.put("price", newPrice);
                        productMap.put("pname", newName);

                        productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminCategoryActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        });
                    }else{builder.setCancelable(true);}
                }
            });
            builder.show();

        }

        }

    private void DisplaySpecificProductInfo() {

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String editProductName = (String) dataSnapshot.child("pname").getValue();
                    String editProductPrice = (String) dataSnapshot.child("price").getValue();
                    String editProductDescription = (String) dataSnapshot.child("description").getValue();
                    String editProductImage = (String) dataSnapshot.child("image").getValue();

                    nameMaintain.setText(editProductName);
                    priceMaintain.setText(editProductPrice);
                    descriptionMaintain.setText(editProductDescription);
                    Picasso.get().load(editProductImage).into(productImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
