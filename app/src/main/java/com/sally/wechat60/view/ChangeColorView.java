package com.sally.wechat60.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sally.wechat60.R;

/**
 * Created by sally on 16/4/6.
 */
public class ChangeColorView extends View {

    // 存储view原本的状态
    public static final String INSTANCE_STATUS = "instance_status";
    // 回复我们自己的alpha
    public static final String INSTANCE_ALPHA = "instance_alpha";

    private int mColor = 0x45c041A;
    private String mText = "微信";
    private Bitmap mIcon;
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private float mAlpha;
    // icon的区域
    private Rect mIconRect;
    // 文字的区域
    private Rect mTextBound;
    private Paint mTextPaint;

    public ChangeColorView(Context context) {
        this(context, null);
    }

    public ChangeColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChangeColorView);
        int count = ta.getIndexCount();
        for(int i=0; i<count; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.ChangeColorView_my_text:
                    mText = ta.getString(attr);
                    break;
                case R.styleable.ChangeColorView_my_color:
                    mColor = ta.getColor(attr, 0xFF45C01A);
                    break;
                case R.styleable.ChangeColorView_my_text_size:
                    mTextSize = (int) ta.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ChangeColorView_my_icon:
                    BitmapDrawable drawable = (BitmapDrawable) ta.getDrawable(attr);
                    mIcon = drawable.getBitmap();
                    break;
            }
        }
        ta.recycle();

        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);

        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());
        int left = (getMeasuredWidth() - iconWidth)/2;
        int top = (getMeasuredHeight() - mTextBound.height() - iconWidth)/2;
        mIconRect = new Rect(left, top, left+iconWidth, top+iconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1. 绘制原始不带颜色的icon
        canvas.drawBitmap(mIcon, null, mIconRect, null);
        int alpha = (int) Math.ceil(255 * mAlpha);
        // 2. 内存中准备带色的mBitmap(icon)，1. setAlpha, 2.设置icon纯色，3.设置xfermode模式（获取颜色的方式）, 4.绘制图标
        setupTargetBitmap(alpha);

        // 1.绘制文本， 2。绘制变色的文本
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);

        // 绘制带有颜色的icon
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * 绘制目标文本
     * @param canvas
     * @param alpha
     */
    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        int x = (getMeasuredWidth() - mTextBound.width()) / 2;
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 绘制原文本
     * @param canvas
     * @param alpha
     */
    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255-alpha);
        int x = (getMeasuredWidth() - mTextBound.width()) / 2;
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 在内存中绘制可变色的icon
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIcon, null, mIconRect, mPaint);
    }

    /**
     * 设置alpha值
      */
    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if(Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    /**
     * 当屏幕旋转的时候，tabIndicator保持当前选中状态，可以通过这种保存状态方法；也可以通过禁止屏幕旋转实现。
     * 当后台activity被销毁，只能通过这种方法实现
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(INSTANCE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}

