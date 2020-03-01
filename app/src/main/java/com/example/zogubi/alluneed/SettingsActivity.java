package com.example.zogubi.alluneed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zogubi.alluneed.Model.Users;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private CircleImageView profileImageView;
    private EditText fullNameEditText , userPhoneEditText , addressEditText , passwordEditText;
    private TextView profileChangeTextBtn , closeTextBtn , saveTextBtn;
    private Button securityQuestionButton;


    //Now To change Infos
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    private StorageTask uploadTask;
    DatabaseReference userRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        passwordEditText = (EditText) findViewById(R.id.settings_password);
        profileChangeTextBtn = (TextView) findViewById(R.id.settings_profile_image_change);
        closeTextBtn = (TextView) findViewById(R.id.settings_close_btn);
        saveTextBtn = (TextView) findViewById(R.id.settings_update_account_btn);
        securityQuestionButton = findViewById(R.id.security_question_button);

        UserInfoDisplay(profileImageView , fullNameEditText , userPhoneEditText , addressEditText, passwordEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this , HomeActivity.class);
                startActivity(intent);
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){ // give clicked value for every parameter that user want to change in settings , don't change everything,just the clicked one's
                    userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";

                CropImage.activity(imageUri) //Android Image Cropper implementation from ArthurHub
                        .setAspectRatio(1 , 1)
                        .start(SettingsActivity.this);


            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String name = (String) dataSnapshot.child("name").getValue();
                fullNameEditText.setText(name);
                String phone = (String) dataSnapshot.child("phone").getValue();
                userPhoneEditText.setText(phone);
                String address = (String) dataSnapshot.child("address").getValue();
                addressEditText.setText(address);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        securityQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this , ResetPasswordActivity.class);
                intent.putExtra("check" , "settings");
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri(); // store uri result
            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "NetWork Error,Please Try Again Later", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this , SettingsActivity.class)); // go back to Normal sttings
            finish();
        }
    }
    // he just want update Information , without The pics
    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String , Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
         if (!TextUtils.isEmpty(passwordEditText.getText().toString())){
            userMap.put("password", passwordEditText.getText().toString());
        }
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        startActivity(new Intent(SettingsActivity.this , HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Your Informations Has Been Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();


    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please Enter Your Address", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked")){
            uploadImage();
        }




    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait , Profile is Being Updated ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri); // now the picture is saved to the storage successfully
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException(); // in case of any problem
                    }
                    return fileRef.getDownloadUrl(); // else return the url
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult(); // take the url from uri and store it in downloadUrl
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String , Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        //we want to put the image into data base
                        if (!TextUtils.isEmpty(passwordEditText.getText().toString())){
                        userMap.put("password", passwordEditText.getText().toString());
                        }
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this , HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Your Informations Has Been Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "NetWork Error , Your Information Has Not Been Updated", Toast.LENGTH_SHORT).show();

                    }
                }
            });




        }
        else{
            Toast.makeText(this, "Image Has Not Been Set", Toast.LENGTH_SHORT).show();
        }


    }


    private void UserInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText , final EditText passwordEditText) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){ // check if we have that phone number exists in database

                    if(dataSnapshot.child("image").exists()){

                        String image = dataSnapshot.child("image").getValue().toString();// access dataBase's image url
                        String name = dataSnapshot.child("name").getValue().toString();// access dataBase's name
                        String phone = dataSnapshot.child("phone").getValue().toString();// access dataBase's phone
                       // String password = dataSnapshot.child("password").getValue().toString();// access dataBase's password
                        String address = dataSnapshot.child("address").getValue().toString();// access dataBase's address

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        //passwordEditText.setText(password); // to make it invisible to others
                        addressEditText.setText(address);
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
