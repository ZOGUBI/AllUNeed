package com.example.zogubi.alluneed;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check ="" ;
    private TextView headlineTitle , answerQuestions , question1 , question2;
    Button verifyButton ;
    EditText resetPhoneEditText , resetQuestion1EditText , resetQuestion2EditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        headlineTitle = findViewById(R.id.activity_headline_reset);
        answerQuestions = findViewById(R.id.answer_this_question_text_view);
        question1 = findViewById(R.id.reset_question_1_text_view);
        question2 = findViewById(R.id.reset_question_2_text_view);

        verifyButton = findViewById(R.id.verify_button);
        resetPhoneEditText = findViewById(R.id.reset_phone_number);
        resetQuestion1EditText = findViewById(R.id.reset_security_question_1);
        resetQuestion2EditText = findViewById(R.id.reset_security_question_2);

         check = getIntent().getStringExtra("check");

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (check.equals("settings")){

            headlineTitle.setText("Set Security Questions");
            resetPhoneEditText.setVisibility(View.GONE);
            verifyButton.setText("Update Questions");
                DisplayPreviousAnswers();


            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        UpdateQuestions();


                }
            });


        }else if (check.equals("login")){

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerifyUser();
                }
            });

        }
    }



    private void UpdateQuestions() {

        String answer1 = resetQuestion1EditText.getText().toString().toLowerCase();
        String answer2 = resetQuestion2EditText.getText().toString().toLowerCase();


        if (answer1.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please Answer The First Question", Toast.LENGTH_SHORT).show();
        } else if (answer2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please Answer The Second Question", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();

            userdataMap.put("answer1", answer1);
            userdataMap.put("answer2", answer2);
            //Create Another child inside PhoneNumber And Call it (Security Questions) and put the answers there
            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Intent intent = new Intent(ResetPasswordActivity.this, SettingsActivity.class);
                        Toast.makeText(ResetPasswordActivity.this, "Security Question Has Updated Successfully", Toast.LENGTH_LONG).show();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }
            });

        }

    }

    private void DisplayPreviousAnswers(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Security Questions");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String answer1 = dataSnapshot.child("answer1").getValue().toString();
                    String answer2 = dataSnapshot.child("answer2").getValue().toString();

                    resetQuestion1EditText.setText(answer1);
                    resetQuestion2EditText.setText(answer2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void VerifyUser() {

        final String verifyPhone = resetPhoneEditText.getText().toString();
        final String verifyAnswer1 = resetQuestion1EditText.getText().toString().toLowerCase();
        final String verifyAnswer2 = resetQuestion2EditText.getText().toString().toLowerCase();


        if(!verifyPhone.equals("") && !verifyAnswer1.equals("") && !verifyAnswer2.equals("")){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(verifyPhone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if (dataSnapshot.hasChild("Security Questions")){

                            String answerDataBase1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String answerDataBase2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();


                            if (!(answerDataBase1.equals(verifyAnswer1))){

                                Toast.makeText(ResetPasswordActivity.this, "Your First Answer is Wrong !", Toast.LENGTH_SHORT).show();

                            }else if (!answerDataBase2.equals(verifyAnswer2)){

                                Toast.makeText(ResetPasswordActivity.this, "Your Second Answer is Wrong !", Toast.LENGTH_SHORT).show();
                            }else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this );
                                builder.setTitle("New Password");

                                final EditText newPassword= new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter Your New Password");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (!newPassword.getText().toString().equals("")){ // he entered new password now let's send t to database
                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                            Intent intent = new Intent(ResetPasswordActivity.this , LoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });




                                        }
                                    }
                                });


                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        }

                        else {
                            Toast.makeText(ResetPasswordActivity.this, "You Have Not Set You Security Questions, Please Contact With The Admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ResetPasswordActivity.this, "The Phone Number You Have Entered Does not Have An Account", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }else{
            Toast.makeText(this, "Please Enter Phone Number And All Questions Answers", Toast.LENGTH_SHORT).show();
        }




    }
}
