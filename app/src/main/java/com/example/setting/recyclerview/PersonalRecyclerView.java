package com.example.setting.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PersonalRecyclerView extends RecyclerView {

    List<View> shownViews = Collections.emptyList();
    List<View> hiddenViews = Collections.emptyList();

    AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            showOrHiddenViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            showOrHiddenViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            showOrHiddenViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            showOrHiddenViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            showOrHiddenViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            showOrHiddenViews();
        }
    };

    public void showOrHiddenViews() {
        if (getAdapter() != null) {

            if (getAdapter().getItemCount() == 0) {
                setVisibility(View.GONE);
                for (View temp : hiddenViews) {
                    temp.setVisibility(View.VISIBLE);
                }
                for (View temp : shownViews)
                    temp.setVisibility(View.GONE);

            } else {
                setVisibility(View.VISIBLE);
                for (View temp : shownViews) {
                    temp.setVisibility(View.VISIBLE);
                }
                for (View temp : hiddenViews)
                    temp.setVisibility(View.GONE);
            }
        }
    }

    public void setShownViews(View... views) {
        shownViews = Arrays.asList(views);
    }

    public void setHiddenViews(View... views) {
        hiddenViews = Arrays.asList(views);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
        dataObserver.onChanged();
    }

    public PersonalRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PersonalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonalRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
