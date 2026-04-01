package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.leanback.widget.VerticalGridView;

public class TouchVerticalGridView extends VerticalGridView {
    private static final int AXIS_NONE = 0;
    private static final int AXIS_VERTICAL = 1;
    private static final int AXIS_HORIZONTAL = 2;

    private final int mTouchSlop;
    private float mDownX;
    private float mDownY;
    private int mLockedAxis;

    public TouchVerticalGridView(Context context) {
        this(context, null);
    }

    public TouchVerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchVerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        updateAxisLock(event);

        if (mLockedAxis == AXIS_HORIZONTAL) {
            return true;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        updateAxisLock(event);

        if (mLockedAxis == AXIS_HORIZONTAL) {
            return false;
        }

        return super.onInterceptTouchEvent(event);
    }

    private void updateAxisLock(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mLockedAxis = AXIS_NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLockedAxis != AXIS_NONE) {
                    break;
                }

                float dx = event.getX() - mDownX;
                float dy = event.getY() - mDownY;

                if (Math.abs(dx) < mTouchSlop && Math.abs(dy) < mTouchSlop) {
                    break;
                }

                mLockedAxis = Math.abs(dy) >= Math.abs(dx) ? AXIS_VERTICAL : AXIS_HORIZONTAL;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(mLockedAxis == AXIS_VERTICAL);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLockedAxis = AXIS_NONE;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
    }
}
