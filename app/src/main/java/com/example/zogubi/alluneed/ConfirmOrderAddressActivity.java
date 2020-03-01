package com.example.zogubi.alluneed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderAddressActivity extends AppCompatActivity {
    private EditText shipmentName , shipmentPhone ,shipmentCity , shipmentDistrict, shipmentNeighborhood, shipmentAddress ;
    private Button paymentButton;
    private String totalAmount = "";
    DatabaseReference infosRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_address);
        totalAmount = getIntent().getStringExtra("Total Price");

        paymentButton = (Button) findViewById(R.id.confirm_information_button);
        shipmentName = (EditText) findViewById(R.id.shipment_name);
        shipmentPhone = (EditText) findViewById(R.id.shipment_phone_number);
        shipmentCity = (EditText) findViewById(R.id.shipment_city);
        shipmentDistrict = (EditText) findViewById(R.id.shipment_district);
        shipmentNeighborhood = (EditText) findViewById(R.id.shipment_neighborhood);
        shipmentAddress = (EditText) findViewById(R.id.shipment_full_address);


        infosRef = FirebaseDatabase.getInstance().getReference();
        infosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").exists()){
                    shipmentName.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("name").getValue().toString());
                    shipmentAddress.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("address").getValue().toString());
                    shipmentPhone.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("phone").getValue().toString());
                    shipmentDistrict.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("district").getValue().toString());
                    shipmentNeighborhood.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("neighborhood").getValue().toString());
                    shipmentCity.setText(dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("Address Information").child("city").getValue().toString());


                }else{
                    infosRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
                    infosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = (String) dataSnapshot.child("name").getValue();
                            shipmentName.setText(name);
                            String phone = (String) dataSnapshot.child("phone").getValue();
                            shipmentPhone.setText(phone);
                            String address = (String) dataSnapshot.child("address").getValue();
                            shipmentAddress.setText(address);
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



        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Check();
            }
        });

    }

    private void Check() {

        if (TextUtils.isEmpty(shipmentName.getText().toString())){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(shipmentPhone.getText().toString())){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(shipmentCity.getText().toString())){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(shipmentDistrict.getText().toString())){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(shipmentNeighborhood.getText().toString())){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(shipmentAddress.getText().toString())) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {


        final String saveCurrentDate , saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd , yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference saveInformationRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        final HashMap<String,Object> ordersMap = new HashMap<>();


        ordersMap.put("totalAmount" , totalAmount);
        ordersMap.put("name" , shipmentName.getText().toString());
        ordersMap.put("phone" , shipmentPhone.getText().toString());
        ordersMap.put("city" , shipmentCity.getText().toString());
        ordersMap.put("district" , shipmentDistrict.getText().toString());
        ordersMap.put("neighborhood" , shipmentNeighborhood.getText().toString());
        ordersMap.put("address" , shipmentAddress.getText().toString());
        ordersMap.put("time" , saveCurrentTime);
        ordersMap.put("date" , saveCurrentDate);
        ordersMap.put("state" , "");

        //ordersMap.put("currentUserPhone" , Prevalent.currentOnlineUser.getPhone()); no need for that , solved in AdminNewOrderActivity , Line 71

        saveInformationRef.child("Address Information").updateChildren(ordersMap);
        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Intent intent = new Intent(ConfirmOrderAddressActivity.this , CardInformationActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}
