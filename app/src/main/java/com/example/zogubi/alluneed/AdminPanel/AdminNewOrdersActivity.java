package com.example.zogubi.alluneed.AdminPanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.zogubi.alluneed.Model.AdminOrders;
import com.example.zogubi.alluneed.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList = findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options  =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef , AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders ,AdminOrdersViewHolder > adapter =
             new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                 @Override
                 protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                     holder.userName.setText("Name: " + model.getName());
                     holder.userPhoneNumber.setText("Phone: " + model.getPhone());
                     holder.userTotalPrice.setText("Total Amount: " + model.getTotalAmount() + "₺");
                     holder.userShippingAdress.setText("Address: " + model.getAddress());
                     holder.userDateTime.setText("Ordered in:" + model.getDate() + " At " + model.getTime());
                     holder.userCityDistrictNeighborhood.setText("City: " + model.getCity() + " /District: "+model.getDistrict() + " /Nieghborhood:"+ model.getNeighborhood());



                     // now by Pressing Show Order Button we will send the admin to adminUserProductDetails
                     // to show orders and we will send phone number as putExtra

                     holder.showOrdersButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {

                             String uId = getRef(position).getKey();
                             Intent intent = new Intent(AdminNewOrdersActivity.this , AdminUserProductsDetailsActivity.class);
                             intent.putExtra("uid" ,uId);
                             startActivity(intent);

                         }
                     });

                     holder.orderStatus.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             CharSequence options[]= new CharSequence[]{

                                     "Yes"
                                     ,"No"
                             };

                             AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                             builder.setTitle("Have This Order Products Shipped ?");
                             builder.setItems(options, new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {

                                     if(which == 0){ // Yes
                                         String uId = getRef(position).getKey();
                                         RemoveOrder(uId);


                                     }else{ // No

                                         finish();
                                     }


                                 }
                             });
                             builder.show();

                         }
                     });



                 }

                 @NonNull
                 @Override
                 public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                     View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout , parent, false);
                     return new AdminOrdersViewHolder(view);
                 }
             };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
        public TextView userName , userPhoneNumber , userTotalPrice , userDateTime , userShippingAdress ,userCityDistrictNeighborhood;
        public Button showOrdersButton , orderStatus;


        public AdminOrdersViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAdress = itemView.findViewById(R.id.order_address);
            userCityDistrictNeighborhood = itemView.findViewById(R.id.order_city_dis_neigh);
            showOrdersButton  = itemView.findViewById(R.id.order_show_all_products_button);
            orderStatus = itemView.findViewById(R.id.order_status);


        }
    }


    private void RemoveOrder(String uId) {

        orderRef.child(uId).removeValue();

    }
}
