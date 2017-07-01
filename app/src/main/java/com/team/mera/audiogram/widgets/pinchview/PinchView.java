package com.team.mera.audiogram.widgets.pinchview;

public interface PinchView {
    void onPinch(float deltaX, float deltaY);
    void onScroll(float x, float deltaX);

    boolean isActive();
    void setActive(boolean isActive);
}
