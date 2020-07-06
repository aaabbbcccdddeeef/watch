package com.wisdomin.studentcard.feature.temperature;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.wisdomin.studentcard.R;


public class PercentCircle extends View {

    /**
     * 绘制百分比的圆，一共有三部分，分别是里面的文字、背景圆、圆环；
     * 思路：首先需要三支画笔, 设置画笔对应的属性等；
     */

    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private Paint mRingPaintWhite;
    private Paint mRingPaint;

    private int mCircleX;
    private int mCircleY;

    private float mCurrentAngle;
    private RectF mArcRectF;
    private float mStartSweepValue;

    private float mTargetPercent;
    private int mCurrentPercent;

    private int mDefaultRadius = 80;
    private int mRadius;

    private int mDefaultBackgroundColor = 0xffafb4db;
    private int mBackgroundColor;

    private int mDefaultRingColor = 0xff6950a1;
    private int mRingColor;

    private int mDefaultTextSize;
    private int mTextSize;


    private int mDefaultTextColor = 0xffffffff;
    private int mTextColor;
    private Handler mHandler;
    private boolean alphaout = false;

    public PercentCircle(Context context) {
        super(context);
        init();
    }

    public PercentCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 自定义属性，attrs
        // 使用TypedArray
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PercentCircle);

        // 背景圆的半径
        mRadius = typedArray.getInt(R.styleable.PercentCircle_radius, mDefaultRadius);

        // 背景圆的颜色
        mBackgroundColor = typedArray.getColor(R.styleable.PercentCircle_circleBackground, mDefaultBackgroundColor);

        // 文字的颜色 默认白色
        mTextColor = typedArray.getColor(R.styleable.PercentCircle_textColor, mDefaultTextColor);

        // 外圆环的颜色
        mRingColor = typedArray.getColor(R.styleable.PercentCircle_ringColor, mDefaultRingColor);

        // Be sure to call recycle() when done with them
        typedArray.recycle();
        init();
    }

    public PercentCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    float strokeWidth = 8;

    public void init() {
        alphaout = false;
        //圆环开始角度 -90° 正北方向
        mStartSweepValue = -90;
        //当前角度
        mCurrentAngle = 0;
        //当前百分比
        mCurrentPercent = 0;

        //设置对勾的画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);

        //设置百分比文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(20);
        mTextPaint.setTextSize(46);   //文字大小为半径的一半
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);

        //设置白色环的画笔
        mRingPaintWhite = new Paint();
        mRingPaintWhite.setAntiAlias(true);
        mRingPaintWhite.setColor(Color.WHITE);
        mRingPaintWhite.setStyle(Paint.Style.STROKE);
        mRingPaintWhite.setStrokeWidth(strokeWidth);
        mRingPaintWhite.setStrokeCap(Paint.Cap.ROUND);
        mRingPaintWhite.setStrokeJoin(Paint.Join.ROUND);

        //设置蓝色圆环的画笔
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(strokeWidth);
        mRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mRingPaint.setStrokeJoin(Paint.Join.ROUND);

        //获得文字的字号 因为要设置文字在圆的中心位置
        mTextSize = (int) mTextPaint.getTextSize();


        //画对钩的笔
        rightPaint = new Paint();
        //设置画笔颜色
        rightPaint.setColor(mRingColor);
        //设置圆弧的宽度
        rightPaint.setStrokeWidth(lineThick);
        //设置圆弧为空心
        rightPaint.setStyle(Paint.Style.STROKE);
        //消除锯齿
        rightPaint.setAntiAlias(true);
        rightPaint.setStrokeCap(Paint.Cap.ROUND);
        rightPaint.setStrokeJoin(Paint.Join.ROUND);
        //获取圆心的x坐标
        center = (int) (240 / 2);
        //圆弧半径
        radius = (int) (240 / 2 - lineThick - step);
        checkStartX = (int) (center - 240 / 5);
        rectF = new RectF(center - radius,
                center - radius,
                center + radius,
                center + radius);
    }

    // 主要是测量wrap_content时候的宽和高，因为宽高一样，只需要测量一次宽即可，高等于宽
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(widthMeasureSpec));
    }

    // 当wrap_content的时候，view的大小根据半径大小改变，但最大不会超过屏幕
    private int measure(int measureSpec) {
        int result = 0;
        //1、先获取测量模式 和 测量大小
        //2、如果测量模式是MatchParent 或者精确值，则宽为测量的宽
        //3、如果测量模式是WrapContent ，则宽为 直径值 与 测量宽中的较小值；否则当直径大于测量宽时，会绘制到屏幕之外；
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //result = 2*mRadius;
            //result =(int) (1.075*mRadius*2);
            result = (int) (mRadius * 2 + mRingPaint.getStrokeWidth() * 2);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //1、如果半径大于圆心的横坐标，需要手动缩小半径的值，否则画到屏幕之外；
        //2、改变了半径，则需要重新设置字体大小；
        //3、改变了半径，则需要重新设置外圆环的宽度
        //4、画背景圆的外接矩形，用来画圆环；
        mCircleX = getMeasuredWidth() / 2;
        mCircleY = getMeasuredHeight() / 2;
        if (mRadius > mCircleX) {
            mRadius = mCircleX;
            mRadius = (int) (mCircleX - 0.075 * mRadius);
            mTextSize = (int) mTextPaint.getTextSize();
        }
        mArcRectF = new RectF(mCircleX - mRadius, mCircleY - mRadius, mCircleX + mRadius, mCircleY + mRadius);
    }


    //绘制圆弧的进度值
    private int progress = 0;
    //打勾的起点
    int checkStartX;
    //线1的x轴增量
    private int line1X = 0;
    //线1的y轴增量
    private int line1Y = 0;
    //线2的x轴增量
    private int line2X = 0;
    //线2的y轴增量
    private int line2Y = 0;
    //增量值
    int step = 3;
    //线的宽度
    private int lineThick = 12;
    //获取圆心的x坐标
    int center;
    //圆弧半径
    int radius;
    //定义的圆弧的形状和大小的界限
    RectF rectF;
    Paint rightPaint;
    boolean secLineInited = false;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1、画中间背景圆
        //2、画文字
        //3、画圆环
        //4、判断进度，重新绘制
//        canvas.drawCircle(mCircleX, mCircleY, mRadius, mBackgroundPaint);
//        canvas.drawArc(mArcRectF,0,360, false, mBackgroundPaint);
        canvas.drawText(String.valueOf(mCurrentPercent), mCircleX, mCircleY + mTextSize / 4, mTextPaint);
        canvas.drawArc(mArcRectF, mStartSweepValue, 360, false, mRingPaintWhite);
        canvas.drawArc(mArcRectF, mStartSweepValue, mCurrentAngle, false, mRingPaint);

        if (mCurrentPercent < mTargetPercent) {
            //当前百分比+1
            mCurrentPercent += 1;
            //当前角度+360
            mCurrentAngle += 3.6;
            //每10ms重画一次
            postInvalidateDelayed(40);
        }

//        canvas.drawRect(mArcRectF, mRingPaint);

        if (mCurrentPercent == 100) {

//            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawCircle(mCircleX, mCircleY, mRadius - (strokeWidth / 2), mBackgroundPaint);
            if (line1X < radius / 3) {
                line1X += step;
                line1Y += step;
            }
            //画第一根线
            Path path = new Path();
            path.moveTo(checkStartX, center);
            path.lineTo(checkStartX + line1X, center + line1Y);
            canvas.drawPath(path, rightPaint);
//            canvas.drawLine(checkStartX, center, checkStartX + line1X, center + line1Y, rightPaint);
            if (line1X >= radius / 3) {
                if (!secLineInited) {
                    line2X = line1X;
                    line2Y = line1Y;
                    secLineInited = true;
                }
                line2X += step;
                line2Y -= step;
                //画第二根线
                path.lineTo(checkStartX + line2X, center + line2Y);
                canvas.drawPath(path, rightPaint);
//                canvas.drawLine(checkStartX + line1X - lineThick / 2,
//                        center + line1Y, checkStartX + line2X, center + line2Y, rightPaint);
            }
            //每隔6毫秒界面刷新
            if (line2X <= radius){
                postInvalidateDelayed(1);
            }else {
                if(!alphaout){
                    mHandler.sendEmptyMessage(1);
                    alphaout = true;
                }
            }

        }

    }

    public void setTargetPercent(float targetPercent, Handler handler) {
        mTargetPercent = targetPercent;
        mHandler = handler;
    }

}