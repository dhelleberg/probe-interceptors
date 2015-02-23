package org.cirrus.mobi.probe.interceptors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import org.lucasr.probe.Interceptor;

/**
 * Tints ImageView depending on the scaling. Upscaling may result in poor image quality,
 * therefore such ImageViews are tinted red.
 * Downscaling may result in poor performance and memory issues, those ImageViews are tinted yellow
 * If none or little scaling is detected, the Image is tinted green
 * In addition, the interceptor prints the scale factor and the width and height in pixels on
 * the screen which should help you fix the issue
 *
 * Created by dhelleberg on 08/10/14.
 */
public class ImageViewInterceptor extends Interceptor {

    public static final int ALPHA_TINT = 150;
    private final Paint mTallTextPaint;

    private final Paint mRectPaint;
    private final int mTallTextSize;

    private static final int COLOR_NO_SCALE = 0xFF2AFF80;
    private static final int COLOR_SCALE_UP = 0xFFEDFF2A;
    private static final int COLOR_SCALE_DOWN = 0xFFFFAAAA;
    private final Paint mTintPaint;
    private final int mSmallTextSize;
    private final Paint mSmallTextPaint;

    private boolean printPixelSize;
    private boolean printSourceSize;


    private ImageViewInterceptor(Context context, boolean printPixelSize, boolean printSourceSize) {
        final float density = context.getResources().getDisplayMetrics().density;

        mTallTextSize = (int) (density * 8);
        mTallTextPaint = new Paint();
        mTallTextPaint.setTextSize(mTallTextSize);
        mTallTextPaint.setAntiAlias(true);
        mTallTextPaint.setFakeBoldText(true);

        mSmallTextSize = (int) (density * 5);
        mSmallTextPaint = new Paint();
        mSmallTextPaint.setTextSize(mSmallTextSize);
        mSmallTextPaint.setAntiAlias(true);

        mTintPaint = new Paint();

        mRectPaint = new Paint();
        mRectPaint.setColor(0xFFFFFFFF);

        this.printPixelSize = printPixelSize;
        this.printSourceSize = printSourceSize;
    }



    @Override
    public void draw(View view, Canvas canvas) {
        super.draw(view, canvas);
        if(view instanceof ImageView)
        {
            final ImageView imageView = (ImageView) view;
            final Matrix drawMatrix = imageView.getImageMatrix();

            int color;
            float scale;

            if(drawMatrix.isIdentity()) {
                color = COLOR_NO_SCALE;
                scale = 1.0f;
            }
            else {
                final float[]values = new float[9];
                drawMatrix.getValues(values);
                final float scaleX = values[0];
                final float scaleY = values[4];
                final float scaleMin = Math.min(scaleX, scaleY);

                color = COLOR_NO_SCALE;

                if(scaleMin > 1.0f)
                    color = COLOR_SCALE_UP;
                else if (scaleMin < 1.0f)
                    color = COLOR_SCALE_DOWN;

                scale = scaleMin;
            }

            final int tintColor = Color.argb(ALPHA_TINT, Color.red(color), Color.green(color),
                    Color.blue(color));
            mTintPaint.setColor(tintColor);

            canvas.drawPaint(mTintPaint);

            final int width = view.getWidth();
            final int height = view.getHeight();

            int factor = Math.max(1, ((height / mTallTextSize) / 2));
            factor = Math.min(4, factor);

            mTallTextPaint.setTextSize(mTallTextSize * factor);
            mSmallTextPaint.setTextSize(mSmallTextSize * factor);

            final String text = String.valueOf(Math.round(scale * 100.0) / 100.0);
            int textWidth = (int) mTallTextPaint.measureText(text);
//                canvas.drawRect(width/2 - textWidth/2, height/2 - mTextSiz - (mPadding), textWidth + (mPadding*2), height, mRectPaint);
            canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + mTallTextPaint.getTextSize() / 2, mTallTextPaint);

            if(printSourceSize) {
                canvas.drawText(imageView.getDrawable().getIntrinsicHeight()+" x "+imageView.getDrawable().getIntrinsicWidth(), 0, height, mSmallTextPaint);
            }
            if(printPixelSize) {
                canvas.drawText(imageView.getHeight()+" x "+imageView.getWidth(), 0, mSmallTextPaint.getTextSize(), mSmallTextPaint);
            }
        }

    }

    public static class Builder {

        private final Context context;
        private boolean printPixelSize = false;
        private boolean printSourceSize = false;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null.");
            }
            this.context = context.getApplicationContext();
        }

        public Builder printPixelSize(boolean printPixelSize) {
            this.printPixelSize = printPixelSize;
            return this;
        }

        public Builder printSourceSize(boolean printSourceSize) {
            this.printSourceSize = printSourceSize;
            return this;
        }

        public ImageViewInterceptor build() {
            return new ImageViewInterceptor(context, printPixelSize, printSourceSize);
        }

    }
}
