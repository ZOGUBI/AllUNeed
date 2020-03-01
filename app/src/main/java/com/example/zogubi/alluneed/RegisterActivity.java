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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton , alreadyHaveAccount;
    private EditText inputName , inputPassword , inputPhoneNumber;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button) findViewById(R.id.register_create_account_button);
        inputName = (EditText) findViewById(R.id.registerNameInput);
        inputPassword= (EditText) findViewById(R.id.registerPasswordInput);
        inputPhoneNumber = (EditText) findViewById(R.id.registerPhoneInput);
        loadingBar = new ProgressDialog(this);
        alreadyHaveAccount = findViewById(R.id.register_already_have_account);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();

            }
        });


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this , LoginActivity.class );
                startActivity(intent);
            }
        });


    }

    private void CreateAccount() {

        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){

            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_SHORT).show();
        }

        else { // everything is OK
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            validatePhoneNumber (name , phone , password); // method to send infos to dataBase


        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phone).exists())){
                    // Creating HashMap to put it in FireBAse
                    HashMap<String , Object > userDataMap= new HashMap<>();
                    userDataMap.put("name" , name);
                    userDataMap.put("phone" , phone);
                    userDataMap.put("password" , password);
                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // The account is successfully created
                                        Toast.makeText(RegisterActivity.this, "Congratulations , Your Account Has Been Successfully Created ", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this , LoginActivity.class );
                                        startActivity(intent);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();
                                    }else{
                                        //problem in fireBase
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "NetWork Error ,  Plese try Again later", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });



                }else{
                    loadingBar.dismiss();

                    Toast.makeText(RegisterActivity.this, "the Phone Number You Just Have Entered is Already Used", Toast.LENGTH_SHORT).show();

                    Toast.makeText(RegisterActivity.this, "Please try again with another Phone Number", Toast.LENGTH_LONG).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
