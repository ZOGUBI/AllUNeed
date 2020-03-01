package com.example.zogubi.alluneed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zogubi.alluneed.AdminPanel.AdminMaintainProductsActivity;
import com.example.zogubi.alluneed.Model.Products;
import com.example.zogubi.alluneed.Prevalent.Prevalent;
import com.example.zogubi.alluneed.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productReference , checkStatusRef;
    private RecyclerView  recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String control = ""; // check whether Admin Or User in Home Activity



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){

            control = getIntent().getExtras().get("Admin").toString();
        }





        Paper.init(this);
        productReference = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.recycler_menu); // for content_home Layout
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this );
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!control.equals("Admin")){
                    Intent intent = new Intent(HomeActivity.this , CartActivity.class);
                    startActivity(intent);
                }
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView userProfileImageView = headerView.findViewById(R.id.user_profile_image);
        if (!control.equals("Admin")){
        userNameTextView.setText(Prevalent.currentOnlineUser.getName()); // take it from paper and display
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(userProfileImageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productReference , Products.class)
                .build();

        FirebaseRecyclerAdapter<Products , ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) { // to display data on text view

                        holder.productNameText.setText(model.getPname());
                        holder.productDescriptionText.setText(model.getDescription());
                        holder.productPriceText.setText("Price:" + model.getPrice() + "₺");
                       Picasso.get().load(model.getImage()).into(holder.imageViewProduct);



                        //For Admin WHo Want To change or delete products




                            // we are now making progress of sending user to product details and purchase activity

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (control.equals("Admin")) {

                                        Intent intent = new Intent(HomeActivity.this , AdminMaintainProductsActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);

                                    } else {
                                        Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);

                                    }
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
        recyclerView.setAdapter(adapter);
        adapter.startListening(); // and now app is ready



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
            if (!control.equals("Admin")) {

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_search) {
            if (!control.equals("Admin")) {

                Intent intent = new Intent(HomeActivity.this, SearchProductActivity.class);
                startActivity(intent);
            }

        }

        else if (id == R.id.nav_settings) {
            if (!control.equals("Admin")) {

                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_logout) {
            if (!control.equals("Admin")) {

                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

