package com.team.mera.audiogram.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackDescription implements Parcelable {

    private String mPath;
    private float mScaleDuration;
    private float mScaleVolume;
    private float mStartTime;
    private boolean mIsRepeated;
    private int mColor;
    private String mName;
    private boolean isPlaying;

    public TrackDescription() {

    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public float getScaleDuration() {
        return mScaleDuration;
    }

    public void setScaleDuration(float scaleX) {
        this.mScaleDuration = scaleX;
    }

    public float getScaleVolume() {
        return mScaleVolume;
    }

    public void setScaleVolume(float scaleY) {
        mScaleVolume = scaleY;
    }

    public float getStartTime() {
        return mStartTime;
    }

    public void setStartTime(float startTime) {
        mStartTime = startTime;
    }

    public boolean getIsRepeated() {
        return mIsRepeated;
    }

    public void setIsRepeated(boolean isRepeated) {
        mIsRepeated = isRepeated;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
        dest.writeFloat(this.mScaleDuration);
        dest.writeFloat(this.mScaleVolume);
        dest.writeFloat(this.mStartTime);
        dest.writeByte(this.mIsRepeated ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mColor);
    }

    protected TrackDescription(Parcel in) {
        this.mPath = in.readString();
        this.mScaleDuration = in.readFloat();
        this.mScaleVolume = in.readFloat();
        this.mStartTime = in.readFloat();
        this.mIsRepeated = in.readByte() != 0;
        this.mColor = in.readInt();
    }

    public static final Creator<TrackDescription> CREATOR = new Creator<TrackDescription>() {
        @Override
        public TrackDescription createFromParcel(Parcel source) {
            return new TrackDescription(source);
        }

        @Override
        public TrackDescription[] newArray(int size) {
            return new TrackDescription[size];
        }
    };
}
