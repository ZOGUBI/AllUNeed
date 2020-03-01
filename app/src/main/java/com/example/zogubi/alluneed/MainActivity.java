package com.example.zogubi.alluneed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.zogubi.alluneed.AdminPanel.AdminAddProductActivity;
import com.example.zogubi.alluneed.AdminPanel.AdminCategoryActivity;
import com.example.zogubi.alluneed.Model.Users;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {



    private Button mainJoinBtn , mainLoginBtn ;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainJoinBtn = (Button) findViewById(R.id.mainJoinBtn);
        mainLoginBtn = (Button) findViewById(R.id.loginLoginButton);
        loadingBar = new ProgressDialog(this);
        Paper.init(this); //initilize paper

        mainLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        });
        mainJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
                startActivity(intent);

            }
        });
        String userPhoneKey= Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey= Paper.book().read(Prevalent.userPaswordKey);

        if(userPhoneKey != null && userPasswordKey != null){

            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){

                AllowAccess(userPhoneKey, userPasswordKey);
                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
        }

    private void AllowAccess(final String phone, final String password) {


        final DatabaseReference rootRef ;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);//take the vlaue and put it in users class

                    if (usersData.getPhone().equals(phone) && usersData.getPassword().equals(password)){ //if entered phone is true with same password in data base then let him in
                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        Prevalent.currentOnlineUser= usersData;
                        startActivity(intent);
                    }else {

                        loadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                    }


                }
                else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "No Account Exists With This Phone Number", Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

