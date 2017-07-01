package com.team.mera.audiogram.widgets.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.utils.DrawUtils;

public class WaveView extends View {
    public final int MAX_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.wave_max_height);
    public final int MIN_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.wave_min_height);
    public final int LINE_WIDTH = 2;

    // 1 - the best quality, > 1 - the better performance
    public final int PERFORMANCE = 2;

    private float[] mHeights;
    private Paint mPaint;

    public WaveView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHeights == null) {
            return;
        }

        int canvasHeight = canvas.getHeight();

        for (int i = 0; i < mHeights.length; i += PERFORMANCE) {
            canvas.drawLine(
                    LINE_WIDTH * i,
                    canvasHeight * (0.5f - mHeights[i]),
                    LINE_WIDTH * i,
                    canvasHeight * (0.5f + mHeights[i]),
                    mPaint
            );
        }
    }

    public void setColor(int color) {
        mPaint = DrawUtils.getPaint(getContext(), false, color);
    }

    public void setHeights(float[] heights) {
        mHeights = heights;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                mHeights.length * LINE_WIDTH,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(params);
    }
}
