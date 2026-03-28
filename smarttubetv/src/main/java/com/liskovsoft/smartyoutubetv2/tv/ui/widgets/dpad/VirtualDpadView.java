package com.liskovsoft.smartyoutubetv2.tv.ui.widgets.dpad;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.liskovsoft.smartyoutubetv2.tv.R;

public class VirtualDpadView extends FrameLayout {
    private static final long REPEAT_DELAY_MS = 400;
    private static final long REPEAT_INTERVAL_MS = 100;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public VirtualDpadView(Context context) {
        super(context);
        inflate(context, R.layout.virtual_dpad, this);
        setupButtons();
    }

    private void setupButtons() {
        bindDirection(R.id.dpad_up, View.FOCUS_UP);
        bindDirection(R.id.dpad_down, View.FOCUS_DOWN);
        bindDirection(R.id.dpad_left, View.FOCUS_LEFT);
        bindDirection(R.id.dpad_right, View.FOCUS_RIGHT);
        bindKey(R.id.dpad_center, KeyEvent.KEYCODE_DPAD_CENTER);
        bindKey(R.id.dpad_back, KeyEvent.KEYCODE_BACK);
    }

    private void bindDirection(int viewId, int focusDirection) {
        View btn = findViewById(viewId);
        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setPressed(true);
                    moveFocus(focusDirection);
                    scheduleRepeatFocus(focusDirection);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setPressed(false);
                    mHandler.removeCallbacksAndMessages(null);
                    return true;
            }
            return false;
        });
    }

    private void bindKey(int viewId, int keyCode) {
        View btn = findViewById(viewId);
        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setPressed(true);
                    fireKey(keyCode, KeyEvent.ACTION_DOWN);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setPressed(false);
                    fireKey(keyCode, KeyEvent.ACTION_UP);
                    return true;
            }
            return false;
        });
    }

    private void scheduleRepeatFocus(int focusDirection) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveFocus(focusDirection);
                mHandler.postDelayed(this, REPEAT_INTERVAL_MS);
            }
        }, REPEAT_DELAY_MS);
    }

    /**
     * Directly move focus in the given direction. This bypasses key event dispatch
     * entirely, so it works regardless of Android's touch mode state.
     * focusSearch() traverses up to the RecyclerView/GridView which uses its
     * LayoutManager to find the correct next item, then requestFocusFromTouch()
     * exits touch mode and grants focus to that item.
     */
    private void moveFocus(int direction) {
        Activity activity = (Activity) getContext();
        View focused = activity.getWindow().getDecorView().findFocus();
        if (focused == null) {
            return;
        }
        View next = focused.focusSearch(direction);
        if (next != null && next != focused) {
            next.requestFocusFromTouch();
        }
    }

    private void fireKey(int keyCode, int action) {
        Context ctx = getContext();
        if (ctx instanceof Activity) {
            ((Activity) ctx).dispatchKeyEvent(new KeyEvent(action, keyCode));
        }
    }
}
