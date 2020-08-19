package com.example.shopping.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.shopping.R;

public class OnBoardSliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public OnBoardSliderAdapter(Context context)
    {
        this.context = context;
    }

    // Arrays
    public int[] slider_images = {
            R.drawable.imag1,
            R.drawable.img2,
            R.drawable.img3
    };

    public String[] slider_names = {
            "EAT",
            "SLEEP",
            "CODE"
    };

    public String[] slider_descriptions = {
            "This is a description 1 This is a description 1",
            "This is a description 2 This is a description 2",
            "This is a description 3 This is a description 3"
    };

    @Override
    public int getCount() {
        return slider_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboard_viewpager_layout,container,false);

        ImageView slideImageView = view.findViewById(R.id.onboard_image);
        TextView slideHeadingTextView = view.findViewById(R.id.onboard_heading);
        TextView slideDescTextView = view.findViewById(R.id.onboard_description);

        slideImageView.setImageResource(slider_images[position]);
        slideHeadingTextView.setText(slider_names[position]);
        slideDescTextView.setText(slider_descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);
    }
}
