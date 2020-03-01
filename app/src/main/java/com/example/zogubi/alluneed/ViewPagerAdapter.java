package com.example.zogubi.alluneed;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

/**
 * Created by OMER ZOGUBI on 8/19/2019.
 */

public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;
    String [] images = {};
    LayoutInflater inflater;

    public ViewPagerAdapter(Activity activity, String[] images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_pager_item, container , false);

        ImageView image ;
        image = (ImageView) itemView.findViewById(R.id.view_pager_imageview);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width  = displayMetrics.widthPixels;
        image.setMinimumHeight(height);
        image.setMinimumWidth(width);

        try{
            Picasso.get()
                    .load(images[position])
                    .placeholder(null)
                    .error(R.mipmap.ic_launcher)
                    .into(image);

        }catch (Exception e){}

        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View)object);
    }
}
