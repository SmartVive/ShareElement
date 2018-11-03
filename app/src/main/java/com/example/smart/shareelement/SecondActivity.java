package com.example.smart.shareelement;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private int currentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        supportPostponeEnterTransition();
        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    //super.onMapSharedElements(names, sharedElements);
                    names.clear();
                    sharedElements.clear();
                    ImageView imageView = viewPager.findViewWithTag("image" + currentIndex);
                    if (imageView == null) {
                        Log.e("view", "view==null");
                        return;
                    }
                    //ImageView imageView = view.findViewById(R.id.image_view);
                    imageView.setTransitionName("image");
                    sharedElements.put("image", imageView);
                }
            });
        }
    }

    public void supportFinishAfterTransition() {
        Intent data = new Intent();
        data.putExtra("index", currentIndex);
        setResult(RESULT_OK, data);
        super.supportFinishAfterTransition();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            supportFinishAfterTransition();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initView() {
        Intent intent = getIntent();
        ArrayList<Integer> list = intent.getIntegerArrayListExtra("list");
        int index = intent.getIntExtra("index", 0);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this, list, index);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(index);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentIndex = Math.round(position + positionOffset);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private ArrayList<Integer> mList;
        private Context mContext;
        private int mIndex;

        ViewPagerAdapter(Context context, ArrayList<Integer> list, int index) {
            mList = list;
            mContext = context;
            mIndex = index;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item, container, false);
            ImageView imageView = view.findViewById(R.id.image_view);
            imageView.setImageResource(mList.get(position));
            imageView.setTag("image" + position);
            if (mIndex == position) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        ((SecondActivity) mContext).supportStartPostponedEnterTransition();
                    }
                });
            }
            container.addView(view);
            return view;
        }



    }
}
