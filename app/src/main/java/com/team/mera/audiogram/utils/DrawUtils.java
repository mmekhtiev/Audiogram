package com.team.mera.audiogram.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.team.mera.audiogram.R;

import java.util.Random;

public class DrawUtils {
    public static final int ANIMATION_DURATION = 300;

    public static int getColor() {
        return Color.rgb(random(), random(), random());
    }

    public static int getAlphaColor(int alpha, int color) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int getGreyColor() {
        Random rand = new Random();
        int greyColor = rand.nextInt(85) + 171;
        return Color.argb(128, greyColor, greyColor, greyColor);
    }

    public static Paint getPaint(Context context, boolean isDashed, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.paint_line_width));
        paint.setStyle(Paint.Style.STROKE);

        int dashWidth = context.getResources().getDimensionPixelSize(R.dimen.paint_dash_width);

        if (isDashed) {
            paint.setPathEffect(new DashPathEffect(new float[] {dashWidth, dashWidth}, 0));
        }

        return paint;
    }

    private static int random() {
        return (int) Math.ceil(Math.random() * 255);
    }

    public static void resize(final View view, final int targetHeight) {
        final int initialHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.getLayoutParams().height =
                        interpolatedTime == 1 ? targetHeight
                        : initialHeight + (int)((targetHeight - initialHeight) * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(ANIMATION_DURATION);
        view.startAnimation(animation);
    }

    public static void collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                }else{
                    view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(ANIMATION_DURATION);
        view.startAnimation(animation);
    }

    public static void expand(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(ANIMATION_DURATION);
        view.startAnimation(animation);
    }
}
