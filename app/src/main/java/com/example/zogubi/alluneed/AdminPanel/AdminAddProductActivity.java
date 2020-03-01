package com.example.zogubi.alluneed.AdminPanel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zogubi.alluneed.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {
    private String categoryName , description , price , pname , saveCurrentDate , saveCurrentTime;
    private Button addNewProductButton;
    EditText inputProductName , inputProductDescription , inputProductPrice;
    TextView categoryNameTextView;
    ImageView addNewProductImage ;
    private static final int GalleryPick = 1;
    private Uri imageUri , fileUri ;
    private String productRandomKey;
    private String [] downloadImageUrl = new String[10];
    private StorageReference productImageReference , mStorage;
    private DatabaseReference productReference;
    private ProgressDialog loadingBar;
    public int totalItemSelected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);
        addNewProductButton = (Button) findViewById(R.id.addNewProductButton);
        inputProductName = (EditText) findViewById(R.id.productNameEditText);
        inputProductDescription = (EditText) findViewById(R.id.productDescEditText);
        inputProductPrice = (EditText) findViewById(R.id.productPriceEditText);
        addNewProductImage = (ImageView) findViewById(R.id.addNewProductImage);
        //categoryName = getIntent().getStringExtra("category");
        categoryNameTextView = findViewById(R.id.category_name_text_view);
       // productImageReference = FirebaseStorage.getInstance().getReference().child("Product Images");
        mStorage = FirebaseStorage.getInstance().getReference().child("Product Images");
        productReference = FirebaseDatabase.getInstance().getReference().child("Products");
        Calendar();
       // categoryNameTextView.setText("You Are Adding on "+ "((" +categoryName+")) Category");
        loadingBar = new ProgressDialog(this);

        addNewProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });


    }

    private void Calendar(){
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Pictures") , GalleryPick);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK ){
            if(data.getClipData()!=null){//Selected Multiple Pictures

               totalItemSelected = data.getClipData().getItemCount();
               if (totalItemSelected <= 8){

                  addNewProductButton.setEnabled(false);

                  Toast.makeText(this, "Please Wait Until All Images Is Uploaded,You Will Be Notified When Pictures Uploaded Successfully", Toast.LENGTH_LONG).show();
                for ( int i = 1 ; i <= totalItemSelected ; i++){

                    final int j = i;

                   fileUri= data.getClipData().getItemAt(i-1).getUri();


                    final StorageReference fileToUpload = mStorage.child(fileUri.getLastPathSegment()+productRandomKey+".jpg");

                    final UploadTask uploadTask = fileToUpload.putFile(fileUri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String message = e.toString();
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();



                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AdminAddProductActivity.this, "Product Image Number " + j + " Uploaded Successfully", Toast.LENGTH_LONG).show();

                            //////////////////// notifying when all images is uploaded successfully and reEnabling Add Product Button
                            if (j == totalItemSelected){
                               AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddProductActivity.this);
                                builder.setTitle("All Images Have Been Uploaded Successfully");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });builder.show();
                                addNewProductButton.setEnabled(true);
                            }
                            ///////////////////

                            // now we want to return image's url and name
                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()){ // in case we have no url
                                        throw task.getException();

                                    }
                                    //now let's return the uri , Not URL
                                    downloadImageUrl[j-1] = fileToUpload.getDownloadUrl().toString(); // we have the uri
                                    return fileToUpload.getDownloadUrl();



                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() { // tell the admin by using on complete listener
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        downloadImageUrl[j-1] = task.getResult().toString();



                                    }

                                }
                            });

                        }
                    });




                }

                }
                else {
                   Toast.makeText(this, "You Can't upload more than 8 pictures", Toast.LENGTH_SHORT).show();

               }

            }else if (data.getData() != null){ //Selected Single Pictures

                Toast.makeText(this, "Please Upload At Least 2 Images", Toast.LENGTH_SHORT).show();

            }



        }

    }


    private void ValidateProductData() {

        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        pname = inputProductName.getText().toString();

        if( fileUri== null){
            Toast.makeText(this, "You Must Upload Product Image", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Product Description Can Not Be Empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(pname)){
            Toast.makeText(this, "Product Name Can Not Be Empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Product Price Can Not Be Empty", Toast.LENGTH_SHORT).show();
        }

        else {// everything is ok let us store them in dataBase


            StoreProductInformation();

        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Adding New Product .. ");
        loadingBar.setMessage("Please wait While The New Product is Being Saved");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();




        SaveProductInfoInDataBase();
    }

    private void SaveProductInfoInDataBase() {

        HashMap<String , Object> productMap = new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", pname);

        for (int i =1 ; i <= totalItemSelected ; i++){
            HashMap<String , Object> productMapImages = new HashMap<>();
            if (i==1){
                productMap.put("image", downloadImageUrl[i-1]);// save it with other product information
                productReference.child(productRandomKey).updateChildren(productMapImages);
            }
                productMapImages.put("image"+i, downloadImageUrl[i-1]);
                productReference.child(productRandomKey).child("Images").updateChildren(productMapImages);// save it into Images Node


        }
        productReference.child(productRandomKey).updateChildren(productMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminAddProductActivity.this, "Product has been added Successfully into Data Base", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                }else{
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddProductActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
