package com.liskovsoft.smartyoutubetv2.tv.presenter;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewConfiguration;

import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.RowPresenter;
import com.liskovsoft.smartyoutubetv2.tv.util.ViewUtil;

public class CustomListRowPresenter extends ListRowPresenter {
    private static final int AXIS_NONE = 0;
    private static final int AXIS_HORIZONTAL = 1;
    private static final int AXIS_VERTICAL = 2;

    public CustomListRowPresenter() {
        super(ViewUtil.FOCUS_ZOOM_FACTOR, ViewUtil.FOCUS_DIMMER_ENABLED);
        setSelectEffectEnabled(ViewUtil.ROW_SELECT_EFFECT_ENABLED);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);

        ViewHolder rowViewHolder = (ViewHolder) holder;
        TouchAxisLockListener touchAxisLockListener = new TouchAxisLockListener(
                rowViewHolder.getGridView(),
                ViewConfiguration.get(holder.view.getContext()).getScaledTouchSlop());

        rowViewHolder.getGridView().setOnTouchInterceptListener(touchAxisLockListener::onTouchEvent);
        rowViewHolder.getGridView().setOnTouchListener((view, event) -> touchAxisLockListener.onTouchEvent(event));
    }

    private static final class TouchAxisLockListener {
        private final View mTargetView;
        private final int mTouchSlop;
        private float mDownX;
        private float mDownY;
        private int mLockedAxis;

        private TouchAxisLockListener(View targetView, int touchSlop) {
            mTargetView = targetView;
            mTouchSlop = touchSlop;
        }

        private boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    mLockedAxis = AXIS_NONE;
                    requestParentIntercept(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mLockedAxis == AXIS_NONE) {
                        float dx = event.getX() - mDownX;
                        float dy = event.getY() - mDownY;

                        if (Math.abs(dx) >= mTouchSlop || Math.abs(dy) >= mTouchSlop) {
                            mLockedAxis = Math.abs(dx) >= Math.abs(dy) ? AXIS_HORIZONTAL : AXIS_VERTICAL;
                        }
                    }

                    requestParentIntercept(mLockedAxis != AXIS_VERTICAL);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mLockedAxis = AXIS_NONE;
                    requestParentIntercept(false);
                    break;
                default:
                    break;
            }

            return false;
        }

        private void requestParentIntercept(boolean disallowIntercept) {
            ViewParent parent = mTargetView.getParent();

            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(disallowIntercept);
            }
        }
    }
}
