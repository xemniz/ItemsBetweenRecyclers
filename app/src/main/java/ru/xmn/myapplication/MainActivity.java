package ru.xmn.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView rv1;
    RecyclerView rv2;
    private ViewGroup parent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parent = (ViewGroup) findViewById(R.id.parent);
        rv1 = (RecyclerView) findViewById(R.id.rv1);
        rv2 = (RecyclerView) findViewById(R.id.rv2);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv2.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> items = new ArrayList<>(Arrays.asList("item1", "item2"));
        rv1.setAdapter(new Adapter(items, new Callback() {
            @Override
            public void onClick(int position) {
                animate(rv1, rv2, position);
            }
        }));
        ArrayList<String> items1 = new ArrayList<>(Arrays.asList("item3", "item4"));
        rv2.setAdapter(new Adapter(items1, new Callback() {
            @Override
            public void onClick(int position) {
                animate(rv2, rv1, position);
            }
        }));

    }

    private void animate(RecyclerView sourceRecycler, RecyclerView targetRecycler, int position) {
        Log.d(TAG, "animate() called with: sourceRecycler = [" + sourceRecycler + "], targetRecycler = [" + targetRecycler + "], position = [" + position + "]");
        View view = sourceRecycler.getLayoutManager().findViewByPosition(position);
        view.setClickable(false);

        int[] initial = getLocationOnScreen(view);

        sourceRecycler.getLayoutManager().removeViewAt(position);

        String removedItem = ((Adapter) sourceRecycler.getAdapter()).removeItemAt(position);

        int width = view.getWidth();
        removeFromParent(view);
        parent.addView(view);
        view.getLayoutParams().width = width;

        int[] container = getLocationOnScreen(sourceRecycler);

        view.setTranslationX(initial[0]);
        view.setTranslationY(initial[1] - container[1]);

        int newPos = ((Adapter) targetRecycler.getAdapter()).add(removedItem, true);
        int[] targetCoordinates = getTarget(targetRecycler, newPos);


        float targetX = (targetCoordinates[0] - initial[0]);
        float targetY = (targetCoordinates[1] - initial[1]);
        long duration = calcDuration(targetX, targetY);
        animateAlpha(removedItem, targetRecycler, view, duration);
        animateTranslation(view, targetX, targetY, duration);
    }

    void animateAlpha(final String removedItem, final RecyclerView targetRecycler, final View view, long duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f).setDuration(duration);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeFromParent(view);
                ((Adapter) targetRecycler.getAdapter()).showItem(removedItem);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                removeFromParent(view);
                ((Adapter) targetRecycler.getAdapter()).showItem(removedItem);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    void animateTranslation(View view, Float deltaX, Float deltaY, Long duration) {
        Log.d(TAG, "animateTranslation() called with: view = [" + view + "], deltaX = [" + deltaX + "], deltaY = [" + deltaY + "], duration = [" + duration + "]");
        view.animate().setDuration(duration)
//                .setInterpolator(OvershootInterpolator(1.1f))
                .translationXBy(deltaX)
                .translationYBy(deltaY)
                .start();
    }

    private long calcDuration(float targetX, float targetY) {
        double v = Math.sqrt(targetX * targetX + targetY * targetY) * (0.7f);
        Log.d(TAG, "calcDuration() returned: " + v);
        return (long) v;
    }

    private int[] getTarget(RecyclerView targetRecycler, int index) {
        int prev = Math.max(0, index);
        RecyclerView.ViewHolder viewHolderForAdapterPosition = targetRecycler.findViewHolderForAdapterPosition(prev);
        View targetView = null;
        if (viewHolderForAdapterPosition == null) {
            targetView = null;
        } else
            targetView = viewHolderForAdapterPosition.itemView;
        if (targetView == null) {
            if (targetRecycler.findViewHolderForAdapterPosition(prev - 1) != null) {
                targetView = targetRecycler.findViewHolderForAdapterPosition(prev - 1).itemView;
            }
            if (targetView != null) {
                int[] targetCoordinates = getLocationOnScreen(targetView);
                targetCoordinates[1] += targetView.getHeight();
                return targetCoordinates;
            }
        }

        if (targetView == null) {
            int[] targetCoordinates = getLocationOnScreen(targetRecycler);
            if (targetRecycler.getChildCount() != 0) {
                // target view is not visible because recycler view is filled
                targetCoordinates[1] += targetRecycler.getHeight();
            }
            return targetCoordinates;
        }

        return new int[]{0, 0};
    }

    private void removeFromParent(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    private int[] getLocationOnScreen(View view) {
        int[] loc = new int[]{0, 0};
        if (view!=null) {
            view.getLocationOnScreen(loc);
        }
        return loc;
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        List<String> items;
        private Callback callback;
        Set<String> hiddenItems = new HashSet<>();

        Adapter(List items, Callback callback) {
            this.callback = callback;
            this.items = items;
        }

        ;

        @Override
        public Adapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new Holder(view);
        }

        public void showItem(String item) {
            if (hiddenItems.remove(item)) {
                notifyItemChanged(items.indexOf(item));
            }
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            if (hiddenItems.contains(items.get(position)))
                holder.itemView.setVisibility(View.INVISIBLE);
            else
                holder.itemView.setVisibility(View.VISIBLE);

            holder.bind(holder, items.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public String removeItemAt(int position) {
            String s = items.get(position);
            items.remove(position);
            notifyItemRemoved(position);
            return s;
        }

        public int add(String removedItem, boolean hide) {
            items.add(removedItem);
            int index = items.size() - 1;
            notifyItemInserted(index);
            if (hide) {
                hiddenItems.add(removedItem);
            }
            return index;
        }

        static class Holder extends RecyclerView.ViewHolder {
            TextView track;

            public Holder(View itemView) {
                super(itemView);
                track = (TextView) itemView.findViewById(R.id.track);
            }

            public void bind(Holder viewHolder, String track) {
                viewHolder.track.setText(track);
            }
        }
    }

    interface Callback {
        void onClick(int position);
    }

}
