package com.example.smart.shareelement;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Integer> list = new ArrayList<>();
    private int currentIndex;
    private int scrollY;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVew();


        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, final Map<String, View> sharedElements) {
                //super.onMapSharedElements(names, sharedElements);
                names.clear();
                sharedElements.clear();

                if (isBack){
                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                    int recyclerViewHeight = recyclerView.getHeight();
                    int height = gridLayoutManager.findViewByPosition(firstVisibleItemPosition).getHeight();
                    if (currentIndex >= firstVisibleItemPosition && currentIndex <= lastVisibleItemPosition) {
                        View view = gridLayoutManager.findViewByPosition(currentIndex);
                        int top = view.getTop();
                        int bottom = view.getBottom();

                        if (top < 0) {
                            recyclerView.scrollBy(0, top);
                        } else if (bottom > recyclerViewHeight) {
                            recyclerView.scrollBy(0, bottom - recyclerViewHeight);
                        }
                    } else {
                        if (firstVisibleItemPosition > currentIndex) {
                            recyclerView.scrollBy(0, (currentIndex / 2) * height - scrollY);
                            Log.e("ccc", (currentIndex / 2) * height - scrollY - recyclerViewHeight + "");
                        } else {
                            recyclerView.scrollBy(0, (currentIndex / 2 + 1) * height - scrollY - recyclerViewHeight);
                        }

                    }
                    isBack = false;
                }



                View view = gridLayoutManager.findViewByPosition(currentIndex);
                if (view != null) {
                    ImageView imageView = view.findViewById(R.id.image_view);
                    imageView.setTransitionName("iamge");
                    sharedElements.put("image", imageView);
                } else {
                    Log.e("view", "view==null");
                }

            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = new Bundle(data.getExtras());
            currentIndex = bundle.getInt("index");
            isBack =true;
        }
    }

    private void initVew() {
        list.add(R.drawable.jpg1);
        list.add(R.drawable.jpg2);
        list.add(R.drawable.jpg3);
        list.add(R.drawable.jpg4);
        list.add(R.drawable.jpg5);
        list.add(R.drawable.jpg6);
        list.add(R.drawable.jpg1);
        list.add(R.drawable.jpg2);
        list.add(R.drawable.jpg3);
        list.add(R.drawable.jpg4);
        list.add(R.drawable.jpg5);
        list.add(R.drawable.jpg6);

        recyclerView = findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                currentIndex = position;
                //view.setTransitionName("image"+position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, view, "image" + position);
                intent.putIntegerArrayListExtra("list", list);
                intent.putExtra("index", position);
                startActivity(intent, activityOptions.toBundle());
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;
            }
        });
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<Integer> mList;
        private OnItemClickListener mOnItemClickListener;

        RecyclerViewAdapter(ArrayList<Integer> list) {
            mList = list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.imageView.setImageResource(mList.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, holder.getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        interface OnItemClickListener {
            void onItemClick(View view, int position);
        }


    }


}
