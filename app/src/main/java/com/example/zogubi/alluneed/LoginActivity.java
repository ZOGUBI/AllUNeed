package com.example.zogubi.alluneed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zogubi.alluneed.AdminPanel.AdminCategoryActivity;
import com.example.zogubi.alluneed.Model.Users;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputPhone , inputPassword ;
    private Button loginButton , goToRegPage , adminButton , notAdminButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox rememberMeCheckBox;//remember me box
    private TextView forgetPassword ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone =  findViewById(R.id.loginPhoneInput);
        inputPassword =  findViewById(R.id.loginPasswordInput);
        loginButton =  findViewById(R.id.loginLoginButton);
        goToRegPage =  findViewById(R.id.loginGoToRegPage);
        rememberMeCheckBox =  findViewById(R.id.rememberMeChcB);//remember me box
        adminButton =  findViewById(R.id.adminButton);
        notAdminButton =  findViewById(R.id.notAdminButton);
        forgetPassword =  findViewById(R.id.forgetPasswordLogin);

        Paper.init(this); //remember me box

        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        goToRegPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(intent);
            }
        });


        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Admin Login");
                adminButton.setVisibility(View.INVISIBLE);
                notAdminButton.setVisibility(View.VISIBLE);
                parentDbName="Admin";


            }
        });

        notAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Login");
                adminButton.setVisibility(View.VISIBLE);
                notAdminButton.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this , ResetPasswordActivity.class);
                intent.putExtra("check" , "login");
                startActivity(intent);
            }
        });



    }

    private void loginUser() {

        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
        }

        else{
            // User Login
            loadingBar.setTitle("Login Process in Progress");
            loadingBar.setMessage("Please wait we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount (phone , password);



        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (rememberMeCheckBox.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey , phone); //write inserted phone inside (UserPhoneKey)
            Paper.book().write(Prevalent.userPaswordKey , password);
        }


        final DatabaseReference rootRef ;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);//take the vlaue and put it in users class

                    if (usersData.getPhone().equals(phone)) {

                        if (usersData.getPassword().equals(password)) { //if entered phone is true with same password in data base then let him in

                            if (parentDbName.equals("Admin")) {

                                loadingBar.dismiss();
                               // Toast.makeText(LoginActivity.this, "Welcome Admin you have Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else if (parentDbName.equals("Users")) {

                                if(dataSnapshot.child(parentDbName).child(phone).hasChild("Security Questions")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(  LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Prevalent.currentOnlineUser = usersData; // to pass name to home activity
                                startActivity(intent);
                                }else {

                                    Prevalent.currentOnlineUser = usersData; // to pass name to home activity
                                    Intent intent = new Intent(LoginActivity.this , ResetPasswordActivity.class);
                                    intent.putExtra("check" , "settings");
                                    startActivity(intent);
                                }
                            }
                        } else {

                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                        }
                    }


            }
            else{
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "No Account Exists With This Phone Number", Toast.LENGTH_LONG).show();
                    goToRegPage.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
