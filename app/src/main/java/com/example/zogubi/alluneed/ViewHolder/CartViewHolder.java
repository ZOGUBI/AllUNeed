package com.example.zogubi.alluneed.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.zogubi.alluneed.Interface.ItemClickListener;
import com.example.zogubi.alluneed.R;

/**
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
 // access cart_item_layout Elements

    public TextView  cartProductName , cartProductQuantity , cartProductPrice;
    private ItemClickListener itemClickListenerl;


    public CartViewHolder(View itemView) {
        super(itemView);
        cartProductName = itemView.findViewById(R.id.cart_product_name);
        cartProductPrice =itemView.findViewById(R.id.cart_product_price);
        cartProductQuantity =itemView.findViewById(R.id.cart_product_quantity);
    }
    @Override
    public void onClick(View v) {
        itemClickListenerl.onClick(v , getAdapterPosition() , false);
    }

    public void setItemClickListenerl(ItemClickListener itemClickListenerl) {
        this.itemClickListenerl = itemClickListenerl;
    }
}
