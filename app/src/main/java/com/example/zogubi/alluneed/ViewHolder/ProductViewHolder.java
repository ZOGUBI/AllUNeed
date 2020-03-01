package com.example.zogubi.alluneed.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zogubi.alluneed.Interface.ItemClickListener;
import com.example.zogubi.alluneed.R;

/**
 *
 * access productItemLayout from here
 */

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productNameText , productDescriptionText , productPriceText;
    public ImageView imageViewProduct;
    public ItemClickListener listener;

    public ProductViewHolder(View itemView) {
        super(itemView);
        imageViewProduct = (ImageView) itemView.findViewById(R.id.product_image);
        productNameText = (TextView) itemView.findViewById(R.id.product_name);
        productDescriptionText = (TextView) itemView.findViewById(R.id.product_description);
        productPriceText = (TextView) itemView.findViewById(R.id.product_price);
        imageViewProduct.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;


    }


    @Override
    public void onClick(View view) {

        listener.onClick(view , getAdapterPosition() , false);

    }
}
