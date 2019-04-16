package com.ownchan.miclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * CLass ：   StarkSongView
 *
 * @author： sjc
 * @date： 2019-04-13 18:00
 */
public class StarkSongView extends View {
    private static final String TAG = "StarkSongView======";
    String[] text = {"12", "3", "6", "9"};

    /**
     * 默认宽高
     */
    int defaultSize = 800;

    /**
     * 刻度线长度
     */
    int scaleLineLength;

    /**
     * 刻度线和外圈的间距
     */
    int scaleLineMargin;
    /**
     * 外圈和文字的画笔
     */
    private Paint textAndCirclePaint;
    /**
     * 刻度画笔
     */
    private Paint scalePaint;


    private Paint scaleSweepPaint;
    private Paint secondPaint;
    private Paint minutePaint;
    private Paint hourPaint;
    private RectF circleRectF;
    private RectF scaleRectF;
    private RectF scaleSweepRectF;
    List<Rect> textBounds = new ArrayList<>();
    private int textSize = DensityUtils.sp2px(getContext(), 14);
    private int padding = 0;
    private int backgroundColor = Color.GRAY;
    private int scaleColor = Color.RED;
    private Path secondPath;
    private Path hourPath;
    /**
     * 秒针偏移角度
     */
    private float secondAngle;
    /**
     * 分针偏移角度
     */
    private float minuteAngle;
    /**
     * 时针偏移角度
     */
    private float hourAngle;
    private boolean isSweep = true;
    private float hourRadius;
    private float minuteRadius;
    private float minuteHandWidth;

    public StarkSongView(Context context) {
        super(context);
    }

    public StarkSongView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasureSize(widthMeasureSpec), getMeasureSize(heightMeasureSpec));
    }

    private int getMeasureSize(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(defaultSize, size);
            case MeasureSpec.UNSPECIFIED:
            default:
                return defaultSize;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //刻度长度默认40
        scaleLineLength = Math.min(w, h) * 40 / defaultSize;
        //刻度margin默认30
        scaleLineMargin = Math.min(w, h) * 30 / defaultSize;
        //时针半径默认为150
        hourRadius = Math.min(w, h) * 150f / defaultSize;

        //分针半径默认为180f
        minuteRadius = Math.min(w, h) * 180f / defaultSize;
        //分针线宽默认为10
        minuteHandWidth = Math.min(w, h) * 10f / defaultSize;
        init();
        if (w > h) {
            circleRectF = new RectF((w - h) / 2 + padding, padding, w - ((w - h) / 2 + padding), h - padding);
        } else if (w < h) {
            circleRectF = new RectF(padding, (h - w) / 2 + padding, w - padding, h - padding - (h - w) / 2);
        } else {
            circleRectF = new RectF(padding, padding, w - padding, h - padding);
        }
        int a = scaleLineMargin + scaleLineLength / 2;
        scaleRectF = new RectF(circleRectF.left + a, circleRectF.top + a, circleRectF.right - a, circleRectF.bottom - a);
        scaleSweepRectF = new RectF(-scaleRectF.width() / 2, -scaleRectF.height() / 2, scaleRectF.width() / 2, scaleRectF.height() / 2);
        secondPath = new Path();
        //内圈半径
        float radius = (scaleRectF.width() - scaleLineLength / 2) / 2;

        float j = Math.min(w, h) * 40f / defaultSize;
        float k = Math.min(w, h) * 15f / defaultSize;
        secondPath.moveTo(radius - j, -k);
        secondPath.lineTo(radius - 10, 0);
        secondPath.lineTo(radius - j, k);
        secondPath.close();

        hourPath = new Path();
        hourPath.addArc(new RectF(-minuteHandWidth * 2, -minuteHandWidth * 2, minuteHandWidth * 2, minuteHandWidth * 2), 20, -50);
        hourPath.arcTo(new RectF(hourRadius - 4, -4, hourRadius + 4, 4), -90, 180);
        hourPath.close();

    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        textAndCirclePaint = new Paint();
        textAndCirclePaint.setColor(Color.GRAY);
        textAndCirclePaint.setStrokeWidth(2);
        textAndCirclePaint.setTextSize(textSize);
        textAndCirclePaint.setAntiAlias(true);
        for (int i = 0; i < text.length; i++) {
            Rect e = new Rect();
            textAndCirclePaint.getTextBounds(text[i], 0, text[i].length(), e);
            padding = Math.max(padding, (i == 0 || i == 3) ? e.height() / 2 : e.width() / 2);
            textBounds.add(e);
        }

        scalePaint = new Paint();
        scalePaint.setColor(scaleColor);
        scalePaint.setStrokeWidth(scaleLineLength);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setAntiAlias(true);

        scaleSweepPaint = new Paint();
        scaleSweepPaint.setStrokeWidth(scaleLineLength);
        scaleSweepPaint.setStyle(Paint.Style.STROKE);
        scaleSweepPaint.setShader(new SweepGradient(0, 0, new int[]{Color.TRANSPARENT, Color.WHITE}, new float[]{0.75f, 1}));
        scaleSweepPaint.setAntiAlias(true);

        secondPaint = new Paint();
        secondPaint.setColor(Color.WHITE);
        secondPaint.setStyle(Paint.Style.FILL);
        secondPaint.setAntiAlias(true);

        minutePaint = new Paint();
        minutePaint.setColor(Color.WHITE);
        minutePaint.setStyle(Paint.Style.STROKE);
        minutePaint.setStrokeWidth(minuteHandWidth);
        minutePaint.setStrokeCap(Paint.Cap.ROUND);
        minutePaint.setAntiAlias(true);

        hourPaint = new Paint();
        hourPaint.setColor(Color.GRAY);
        hourPaint.setStyle(Paint.Style.FILL);
        hourPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: start");
        getCurrentTime();
        drawTextAndCircle(canvas);
        drawScale(canvas);
        drawHourHand(canvas);
        drawMinuteHand(canvas);
        drawSecondHand(canvas);
        invalidate();
        Log.i(TAG, "onDraw: end");
    }


    /**
     * 获取当前时间
     */
    private void getCurrentTime() {
        Calendar instance = Calendar.getInstance();
        float millisecond = instance.get(Calendar.MILLISECOND);
        float second = instance.get(Calendar.SECOND) + (isSweep ? millisecond / 1000f : 0);
        float minute = instance.get(Calendar.MINUTE) + (isSweep ? second / 60f : 0);
        float hour = instance.get(Calendar.HOUR) + (isSweep ? minute / 60f : 0);

        hourAngle = hour * 360 / 12 - 90;
        minuteAngle = minute * 360 / 60 - 90;
        secondAngle = (second * 360f / 60) - 90;
    }


    /**
     * 画外圈和文字
     *
     * @param canvas 画布
     */
    private void drawTextAndCircle(Canvas canvas) {
        textAndCirclePaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < 4; i++) {
            canvas.drawArc(circleRectF, 90 * i + 5, 80, false, textAndCirclePaint);
        }
//        int width = getWidth();
//        int height = getHeight();
//        canvas.drawLine(0, height / 2, width, height / 2, textAndCirclePaint);
//        canvas.drawLine(width / 2, 0, width / 2, height, textAndCirclePaint);
//
//        canvas.drawRect(circleRectF, textAndCirclePaint);
//        canvas.drawRect(scaleRectF, textAndCirclePaint);

        textAndCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text[0], getWidth() / 2 - textBounds.get(0).width() / 2, circleRectF.top + textBounds.get(0).height() / 2, textAndCirclePaint);
        canvas.drawText(text[1], circleRectF.right - textBounds.get(1).width() / 2, getHeight() / 2 + textBounds.get(1).height() / 2, textAndCirclePaint);
        canvas.drawText(text[2], getWidth() / 2 - textBounds.get(2).width() / 2, circleRectF.bottom + textBounds.get(2).height() / 2, textAndCirclePaint);
        canvas.drawText(text[3], circleRectF.left - textBounds.get(3).width() / 2, getHeight() / 2 + textBounds.get(3).height() / 2, textAndCirclePaint);
    }

    /**
     * 画刻度
     *
     * @param canvas 画布
     */
    private void drawScale(Canvas canvas) {
        canvas.save();
        scalePaint.setColor(backgroundColor);
        canvas.drawArc(scaleRectF, 0, 360, false, scalePaint);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(secondAngle);
        canvas.drawArc(scaleSweepRectF, 0, 360, false, scaleSweepPaint);
        canvas.restore();
        scalePaint.setColor(scaleColor);
        for (int i = 0; i < 180; i++) {
            canvas.drawArc(scaleRectF, -0.5f + 2 * i, 1, false, scalePaint);
        }
    }

    /**
     * 时针
     *
     * @param canvas 画布
     */
    private void drawHourHand(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(hourAngle);
        canvas.drawPath(hourPath, hourPaint);
        hourPaint.setStyle(Paint.Style.STROKE);
        hourPaint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    /**
     * 分针
     *
     * @param canvas 画布
     */
    private void drawMinuteHand(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.drawCircle(0, 0, minuteHandWidth * 1.5f, minutePaint);
        canvas.rotate(minuteAngle);
        canvas.drawLine(minuteHandWidth * 2, 0, minuteRadius + (minuteHandWidth * 2), 0, minutePaint);
        canvas.restore();
    }

    /**
     * 秒针
     *
     * @param canvas 画布
     */
    private void drawSecondHand(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(secondAngle);
        canvas.drawPath(secondPath, secondPaint);
        canvas.restore();
    }
}
