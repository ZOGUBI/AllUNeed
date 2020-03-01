package com.example.zogubi.alluneed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CardInformationActivity extends AppCompatActivity {

    CardForm cardForm;
    Button confirmOrderButton;
    String cardNum, expirationDate , cvv , cardPhoneNumber;
    DatabaseReference addressBackRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_information);

         cardForm = findViewById(R.id.card_form);
         confirmOrderButton = findViewById(R.id.confirm_final_order_button);
         cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(CardInformationActivity.this);


        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()) {
                    cardNum =cardForm.getCardNumber().toString();
                    expirationDate = (cardForm.getExpirationMonth().toString() + "/" + cardForm.getExpirationYear().toString());
                    cvv = cardForm.getCvv().toString();
                    cardPhoneNumber = cardForm.getMobileNumber().toString();

                    final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                            .child("Orders").child(Prevalent.currentOnlineUser.getPhone());

                    final HashMap<String,Object> creditCartMap = new HashMap<>();
                    creditCartMap.put("cardNumber" , cardNum);
                    creditCartMap.put("expirationDate" , expirationDate);
                    creditCartMap.put("cvv" , cvv);
                    creditCartMap.put("card phone number" , cardPhoneNumber);
                    creditCartMap.put("state" , "not shipped");


                    orderRef.updateChildren(creditCartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Cart List")
                                        .child("User View")
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(CardInformationActivity.this, "Your Order Has Been Placed Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(CardInformationActivity.this , HomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }


                                            }
                                        });

                            }
                        }
                    });


                }else {
                    Toast.makeText(CardInformationActivity.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
