package com.sasha.lampwiz.activity.scan.ui;

import android.content.Context;
import android.graphics.PointF;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class ScanLayoutManager extends LinearLayoutManager {

    public ScanLayoutManager(Context context) {

        super(context, VERTICAL, false);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        this.startSmoothScroll(smoothScroller);
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {

        public TopSnappedSmoothScroller(Context context) {

            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {

            return ScanLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {

            return SNAP_TO_START;
        }
    }
}
