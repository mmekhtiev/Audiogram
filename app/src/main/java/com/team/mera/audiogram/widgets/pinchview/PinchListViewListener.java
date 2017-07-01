package com.team.mera.audiogram.widgets.pinchview;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class PinchListViewListener implements View.OnTouchListener {
    private float mFirstX = -1;
    private float mFirstY = -1;
    private float mSecondX = -1;
    private float mSecondY = -1;
    private float mDownX = -1;
    private float mDownY = -1;
    private float mDistance = 0;

    private int mPointerCount = 0;
    private int mSlop = 0;

    private boolean mIsActive = false;

    private ListView mListView;
    private PinchView mPinchView;
    private int mPinchViewPosition;
    private PinchListener mActivateListener;

    public PinchListViewListener(@NonNull ListView listView) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(listView.getContext());
        mSlop = viewConfiguration.getScaledTouchSlop();

        mListView = listView;
        mActivateListener = (PinchListener) listView.getAdapter();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = (event.getAction() & MotionEvent.ACTION_MASK);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                mPointerCount++;

                if (mPointerCount == 1) {
                    mPinchView = (PinchView) getChildView(event);

                    if (mPinchView != null) {
                        mDownX = event.getX();
                        mDownY = event.getY();

                        mPinchViewPosition = mListView.getPositionForView((View) mPinchView);
                        mIsActive = mPinchView.isActive();
                    } else {
                        mPinchViewPosition = -1;
                        mIsActive = false;
                    }

                    mFirstX = event.getX(0);
                    mFirstY = event.getY(0);

                } else if (mPointerCount == 2) {
                    mSecondX = event.getX(1);
                    mSecondY = event.getY(1);
                    mDistance = distance(event, 0, 1);
                }
                //view.onTouchEvent(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mPointerCount--;

                if (mPointerCount == 1 && event.getActionIndex() == 0) {
                    mFirstX = event.getX(1);
                    mFirstY = event.getY(1);
                }

                if (distance(event) < mSlop) {
                    mActivateListener.onActive(mPinchViewPosition);
                }

                mIsActive = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mPinchView == null || !mIsActive) {
                    //do nothing
                } else if (mPointerCount == 1) {
                    mPinchView.onScroll(mFirstX, event.getX(0) - mFirstX);

                    mFirstX = event.getX(0);
                    mFirstY = event.getY(0);

                } else {
                    float distanceCurrent = distance(event, 0, 1);
                    float deltaFirstX = mFirstX - event.getX(0);
                    float deltaFirstY = mFirstY - event.getY(0);
                    float deltaSecondX = mSecondX - event.getX(1);
                    float deltaSecondY = mSecondY - event.getY(1);

                    float sign = Math.signum(distanceCurrent - mDistance);

                    float pinchX = sign * Math.abs(deltaFirstX - deltaSecondX);
                    float pinchY = sign * Math.abs(deltaFirstY - deltaSecondY);

                    mDistance = distance(event, 0, 1);
                    mFirstX = event.getX(0);
                    mFirstY = event.getY(0);
                    mSecondX = event.getX(1);
                    mSecondY = event.getY(1);

                    mPinchView.onPinch(pinchX, pinchY);
                }

                return mIsActive;
        }

        return false;
    }

    // Find distance between touches
    private float distance(MotionEvent event, int first, int second) {
        if (event.getPointerCount() >= 2) {
            final float x = event.getX(first) - event.getX(second);
            final float y = event.getY(first) - event.getY(second);

            return (float) Math.sqrt(x * x + y * y);
        }

        return 0;
    }

    // Find distance from last touch
    private float distance(MotionEvent event) {
        float x = event.getX(0) - mDownX;
        float y = event.getY(0) - mDownY;

        return (float) Math.sqrt(x * x + y * y);
    }

    // Find the child view that was touched (perform a hit test)
    private View getChildView(MotionEvent motionEvent) {
        int[] coordinates = new int[2];
        mListView.getLocationOnScreen(coordinates);

        int x = (int) motionEvent.getRawX() - coordinates[0];
        int y = (int) motionEvent.getRawY() - coordinates[1];

        Rect rect = new Rect();
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View child = mListView.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y)) {
                return child;
            }
        }
        return null;
    }
}
