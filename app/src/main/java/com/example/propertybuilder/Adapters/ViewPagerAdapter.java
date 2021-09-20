package com.example.propertybuilder.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.propertybuilder.ConstantApis.Api;
import com.example.propertybuilder.R;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    // Array of images
    ArrayList<String> images;

    // Layout Inflater
    LayoutInflater mLayoutInflater;


    public
    ViewPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);

        // setting the image in the imageView
//        imageView.setImageResource(R.drawable.propertydemo);g

//        TextView textView = new TextView(context);
//        textView.setText("wow");

        Glide.with(context)
                .load(Api.IMAGE_BASE_URL+images.get(position))
                .load(Api.IMAGE_BASE_URL+""+images.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.drawable.dummy_pic)
                .into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
