package com.ace.qnote.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ace.qnote.R;

/**
 * Created by ice on 2018/7/8.
 */

public class BannerAdapter extends PagerAdapter{

    int[] images = {R.mipmap.navigator_1,R.mipmap.navigator_1};

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_banner, container,false);
        ImageView ivBanner = view.findViewById(R.id.iv_banner);
        ivBanner.setImageResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
