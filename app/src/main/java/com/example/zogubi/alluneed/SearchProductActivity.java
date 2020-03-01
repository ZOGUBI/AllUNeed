package com.example.zogubi.alluneed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.zogubi.alluneed.Model.Products;
import com.example.zogubi.alluneed.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {


    private Button searchButton ;
    private EditText searchInputText;
    private RecyclerView searchList;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        searchInputText = findViewById(R.id.search_product_name);
        searchButton = findViewById(R.id.search_button);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchInput = searchInputText.getText().toString();
                onStart();


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>().setQuery(reference.orderByChild("pname").startAt(searchInput).endAt(searchInput+"\uf8ff") , Products.class)
                .build(); // we Can Search By Description , what ever you need

        FirebaseRecyclerAdapter <Products , ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                holder.productNameText.setText(model.getPname());
                holder.productDescriptionText.setText(model.getDescription());
                holder.productPriceText.setText("Price:" + model.getPrice() + "₺");
                Picasso.get().load(model.getImage()).into(holder.imageViewProduct);

                // we are now making progress of sending user to product details and purchase activity

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchProductActivity.this , ProductDetailsActivity.class);
                        intent.putExtra("pid" , model.getPid());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout , parent , false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();




    }
}
