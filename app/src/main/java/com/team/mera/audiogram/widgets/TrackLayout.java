package com.team.mera.audiogram.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.TrackDescriptionListener;
import com.team.mera.audiogram.utils.DrawUtils;
import com.team.mera.audiogram.widgets.pinchview.PinchLayout;
import com.team.mera.audiogram.widgets.waveview.WaveView;

public class TrackLayout extends PinchLayout {
    protected String mPath;
    protected int mDuration;
    protected int mColor;

    private int mPosition;
    private WaveView mWaveView;

    private TrackDescriptionListener mListener;
    private TrackDescription mDescription = new TrackDescription();

    public TrackLayout(@NonNull Context context, TrackDescriptionListener listener, int position) {
        super(context);
        mListener = listener;
        mPosition = position;
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        mWaveView = new WaveView(context);

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        addView(mWaveView, params);
    }

    public void setTrack(Track track) {
        if (mPath != null && mPath.equals(track.getPath())) {
            return;
        }

        mColor = track.getColor();
        mPath = track.getPath();
        mDuration = track.getDuration();

        mWaveView.setHeights(track.getHeights());
        mWaveView.setColor(DrawUtils.getAlphaColor(150, track.getColor()));

        setPaintColor(track.getColor());
        setBackgroundColor(DrawUtils.getAlphaColor(20, track.getColor()));
    }

    @Override
    public void setActive(boolean isActive) {
        super.setActive(isActive);
        DrawUtils.resize(this, mIsActive ? mWaveView.MAX_HEIGHT : mWaveView.MIN_HEIGHT);
    }

    public TrackDescription getDescription() {
        float startTime = mPositionX * mDuration * mParentScaleX / (mChildWidth / mScaleX);

        mDescription.setPath(mPath);
        mDescription.setColor(mColor);
        mDescription.setIsRepeated(false);
        mDescription.setScaleDuration(1 / mScaleX);
        mDescription.setScaleVolume(mScaleY / 2);
        mDescription.setStartTime(startTime / 1000000f);

        return mDescription;
    }

    @Override
    public void onPinch(float deltaX, float deltaY) {
        super.onPinch(deltaX, deltaY);
        mListener.onUpdate(getDescription(), mPosition);
    }

    @Override
    public void onScroll(float x, float deltaX) {
        super.onScroll(x, deltaX);
        mListener.onUpdate(getDescription(), mPosition);
    }
}
