package com.team.mera.audiogram.widgets.pinchview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.team.mera.audiogram.utils.DrawUtils;

public abstract class PinchLayout extends FrameLayout implements PinchView {
    protected float mScaleX = 1f;
    protected float mScaleY = 1f;

    protected float mPositionX = 0;

    // Zoom level
    public static float mParentScaleX = 3;

    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Paint mDashPaint = new Paint();

    protected float mChildWidth = 0;

    protected int mHeight = 0;
    protected int mWidth = 0;

    protected boolean mIsActive = false;

    public PinchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected void init(Context context) {
        setWillNotDraw(false);
    }

    public void setPaintColor(int color) {
        mPaint = DrawUtils.getPaint(getContext(), false, color);
        mDashPaint = DrawUtils.getPaint(getContext(), true, color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mHeight = canvas.getHeight();
        mWidth = canvas.getWidth();

        // Draw bounds
        canvas.drawLine(0, 0, mWidth, 0, mPaint);
        canvas.drawLine(0, mHeight, mWidth, mHeight, mPaint);

        // Draw init level
        if (mScaleY > 1) {
            float deltaHeight = 0.5f / mScaleY;

            mPath.reset();

            mPath.moveTo(0, mHeight * (0.5f - deltaHeight));
            mPath.quadTo(mWidth / 2, mHeight * (0.5f - deltaHeight), mWidth, mHeight * (0.5f - deltaHeight));

            mPath.moveTo(0, mHeight * (0.5f + deltaHeight));
            mPath.quadTo(mWidth / 2, mHeight * (0.5f + deltaHeight), mWidth, mHeight * (0.5f + deltaHeight));

            canvas.drawPath(mPath, mDashPaint);
        }

        if (mChildWidth == 0 && getChildCount() > 0) {
            mChildWidth = getChildAt(0).getWidth();
        }

        // Draw scaled child view
        float canvasScaleX = mScaleX / mParentScaleX;
        float canvasScaleY = Math.min(mScaleY, 1);

        canvas.translate(mChildWidth / 2f , mHeight * (1 - canvasScaleY) / 2f);
        canvas.scale(canvasScaleX, canvasScaleY);
        canvas.translate(mPositionX / canvasScaleX - mChildWidth / 2f / canvasScaleX, 0);

        super.onDraw(canvas);
    }

    @Override
    public void onPinch(float deltaX, float deltaY) {
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            mScaleX = Math.max(0.25f, Math.min(2f, mScaleX + 2 * deltaX / mWidth));
        } else {
            mScaleY = Math.max(0.25f, Math.min(2f, mScaleY + 2 * deltaY / mHeight));
        }

        mChildWidth = getChildAt(0).getWidth() * mScaleX;

        if (mPositionX + mChildWidth / mParentScaleX > mWidth) {
            mPositionX = mWidth - mChildWidth / mParentScaleX;
        }

        invalidate();
    }

    @Override
    public void onScroll(float x, float deltaX) {
        mPositionX += deltaX;

        if (mPositionX < 0) {
            mPositionX = 0;
        }

        if (mPositionX + mChildWidth / mParentScaleX > mWidth) {
            mPositionX = mWidth - mChildWidth / mParentScaleX;
        }

        invalidate();
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public void setActive(boolean isActive) {
        if (mIsActive == isActive) {
            return;
        }

        mIsActive = isActive;
    }
}
