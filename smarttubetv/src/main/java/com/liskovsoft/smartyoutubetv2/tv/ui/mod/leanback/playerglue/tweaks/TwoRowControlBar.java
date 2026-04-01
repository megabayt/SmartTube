package com.liskovsoft.smartyoutubetv2.tv.ui.mod.leanback.playerglue.tweaks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * A ControlBar that lays its children in two centered horizontal rows.
 * Used in portrait orientation so player buttons don't overflow the screen width.
 */
class TwoRowControlBar extends ControlBar {

    public TwoRowControlBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoRowControlBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * No-op: TwoRowControlBar positions children itself; center-margin logic is not needed.
     */
    @Override
    public void setChildMarginFromCenter(int marginFromCenter) {
        // intentionally empty
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        if (count == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }

        final int split = (count + 1) / 2;
        int row1W = 0, row1H = 0, row2W = 0, row2H = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            if (i < split) { row1W += cw; row1H = Math.max(row1H, ch); }
            else            { row2W += cw; row2H = Math.max(row2H, ch); }
        }

        setMeasuredDimension(
            resolveSize(Math.max(row1W, row2W), widthMeasureSpec),
            resolveSize(row1H + row2H, heightMeasureSpec)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        if (count == 0) return;

        final int split = (count + 1) / 2;
        final int availW = r - l;

        int row1H = 0, row2H = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (i < split) row1H = Math.max(row1H, child.getMeasuredHeight());
            else            row2H = Math.max(row2H, child.getMeasuredHeight());
        }

        int row1W = 0, row2W = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            if (i < split) row1W += child.getMeasuredWidth();
            else            row2W += child.getMeasuredWidth();
        }

        int x = Math.max(0, (availW - row1W) / 2);
        for (int i = 0; i < split; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            child.layout(x, (row1H - ch) / 2, x + cw, (row1H + ch) / 2);
            x += cw;
        }

        x = Math.max(0, (availW - row2W) / 2);
        for (int i = split; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            child.layout(x, row1H + (row2H - ch) / 2, x + cw, row1H + (row2H + ch) / 2);
            x += cw;
        }
    }
}
