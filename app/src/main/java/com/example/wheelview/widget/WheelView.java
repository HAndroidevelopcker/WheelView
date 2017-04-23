package com.example.wheelview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by wx on 2017/3/9.
 */

public class WheelView extends View {
    WheelClickListener listener;
    int pressFlag ;
    public static final int CLICK_NONE = -1;
    public static final int CLICK_RIGHT_DOWN = 0, CLICK_BOTTOM_DOWN = 1, CLICK_LEFT_DOWN = 2, CLICK_TOP_DOWN = 3;
    public static final int CLICK_RIGHT_UP = 4, CLICK_BOTTOM_UP = 5, CLICK_LEFT_UP = 6, CLICK_TOP_UP = 7;

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pressFlag = CLICK_NONE;
    }

    public void setWheelClickListener(WheelClickListener listener) {
        this.listener = listener;
    }

    int upClr = 0xff25b3f1, downClr = 0xff088fcb, borderClr = 0xffa1e2ff, innerClr = 0xff109fde, arrowClr = 0xfff0f0f0, r1, r2;

    @Override
    protected void onDraw(Canvas canvas) {
        int w = canvas.getWidth();
        r1 = w / 2 - 2;
        r2 = r1 / 3;
        float density = getResources().getDisplayMetrics().density;
        Paint p = new Paint();
        p.setColor(upClr);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w / 2, w / 2, r1, p);
        if (pressFlag >= CLICK_RIGHT_DOWN && pressFlag <= CLICK_TOP_DOWN) {
            p.setColor(innerClr);
            canvas.drawArc(new RectF(0, 0, w, w), pressFlag * 90 - 45, 90, true, p);
        }
        p.setColor(borderClr);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(density * 2f);
        canvas.drawCircle(w / 2, w / 2, r1, p);
        float factor = (float) sqrt(2);
        p.setColor(borderClr);
        canvas.drawLine(r1 - r1 / factor, r1 - r1 / factor, r1 + r1 / factor, r1 + r1 / factor, p);
        canvas.drawLine(r1 - r1 / factor, r1 + r1 / factor, r1 + r1 / factor, r1 - r1 / factor, p);
        p.setColor(arrowClr);
        p.setStrokeJoin(Paint.Join.MITER);
        canvas.drawLine(w / 8 + 12 * density, w / 2 - 12 * density, w / 8, w / 2, p);
        canvas.drawLine(w / 8, w / 2, w / 8 + 12 * density, w / 2 + 12 * density, p);
        canvas.drawLine(w * 7 / 8 - 12 * density, w / 2 - 12 * density, w * 7 / 8, w / 2, p);
        canvas.drawLine(w * 7 / 8 - 12 * density, w / 2 + 12 * density, w * 7 / 8, w / 2, p);
        canvas.drawLine(w / 2 - 12 * density, w / 8 + 12 * density, w / 2, w / 8, p);
        canvas.drawLine(w / 2 + 12 * density, w / 8 + 12 * density, w / 2, w / 8, p);
        canvas.drawLine(w / 2 - 12 * density, w * 7 / 8 - 12 * density, w / 2, w * 7 / 8, p);
        canvas.drawLine(w / 2 + 12 * density, w * 7 / 8 - 12 * density, w / 2, w * 7 / 8, p);
        p.setColor(innerClr);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w / 2, w / 2, r2, p);
        p.setColor(borderClr);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(w / 2, w / 2, r2, p);
    }

    public interface WheelClickListener {
        void onWheelClick(int type);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (sqrt(r1 * r1) > hypot(r1 - x, r1 - y) && sqrt(r2 * r2) < hypot(r1 - x, r1 - y)) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                float translatedX = x - r1;
                float translatedY = y - r1;
                float rotaedX = (float) (translatedX * cos(PI / 4) - translatedY * sin(PI / 4));
                float rotaedY = (float) (translatedX * sin(PI / 4) + translatedY * cos(PI / 4));
                if (rotaedX > 0 && rotaedY > 0) {
                    pressFlag = CLICK_RIGHT_DOWN;
                } else if (rotaedX < 0 && rotaedY > 0) {
                    pressFlag = CLICK_BOTTOM_DOWN;
                } else if (rotaedX < 0 && rotaedY < 0) {
                    pressFlag = CLICK_LEFT_DOWN;
                } else {
                    pressFlag = CLICK_TOP_DOWN;
                }
                Log.e("click", "" + pressFlag);
                listener.onWheelClick(pressFlag);
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                float translatedX = x - r1;
                float translatedY = y - r1;
                float rotaedX = (float) (translatedX * cos(PI / 4) - translatedY * sin(PI / 4));
                float rotaedY = (float) (translatedX * sin(PI / 4) + translatedY * cos(PI / 4));
                if (rotaedX > 0 && rotaedY > 0) {
                    pressFlag = CLICK_RIGHT_UP;
                } else if (rotaedX < 0 && rotaedY > 0) {
                    pressFlag = CLICK_BOTTOM_UP;
                } else if (rotaedX < 0 && rotaedY < 0) {
                    pressFlag = CLICK_LEFT_UP;
                } else {
                    pressFlag = CLICK_TOP_UP;
                }
                Log.e("click", "" + pressFlag);
                listener.onWheelClick(pressFlag);
            }
            invalidate();
        } else {
            //滑动越界并处理事件
            if (pressFlag == CLICK_BOTTOM_DOWN){
                pressFlag = CLICK_BOTTOM_UP;
            }else if (pressFlag == CLICK_LEFT_DOWN){
                pressFlag = CLICK_LEFT_UP;
            }else if (pressFlag == CLICK_TOP_DOWN){
                pressFlag = CLICK_TOP_UP;
            }else if (pressFlag == CLICK_RIGHT_DOWN){
                pressFlag = CLICK_RIGHT_UP;
            }
            listener.onWheelClick(pressFlag);
            invalidate();
            pressFlag = CLICK_NONE;
        }
        return true;
    }
}
