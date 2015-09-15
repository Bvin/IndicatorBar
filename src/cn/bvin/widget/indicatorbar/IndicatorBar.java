package cn.bvin.widget.indicatorbar;

import cn.bvin.widget.seekbarindicator.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class IndicatorBar extends View{

    private static final float DEFAULT_BAR_WEIGHT_PX = 2;
    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
    
    private int mDefaultWidth = 500;
    private int mDefaultHeight = 100;
    
    private float mLeftX = 0;
    private float mRightX = 0;
    private float mAvailableWidth = 0;
    
    private Paint mTrackPaint;
    
    private Bitmap mThumb;
    private float mThumbX;
    private int mCount = 6;
    private int mCurrentPosition = -1;
    
    private OnIndicatorChangeListener mListener;
    
    public IndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTrackPaint();
        mThumb = BitmapFactory.decodeResource(getResources(), R.drawable.range_seek_thumb);
    }
    
    public void setOnIndicatorChangeListener(OnIndicatorChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        
        // Get measureSpec mode and size values.
        final int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
    
        // The RangeBar width should be as large as possible.
        if (measureWidthMode == MeasureSpec.AT_MOST) {
            width = measureWidth;
        } else if (measureWidthMode == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = mDefaultWidth;
        }

        // The RangeBar height should be as small as possible.
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDefaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = mDefaultHeight;
        }
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLeftX = getPaddingLeft();
        mThumbX = mLeftX;
        mRightX = getRight() - getPaddingRight() - getPaddingLeft();
        //mRightX = getRight();
        mAvailableWidth = w - getPaddingLeft() - getPaddingRight();
    }
    
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If this View is not enabled, don't allow for touch interactions.
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                //onActionDown(event.getX(), event.getY());
                return true;
    
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                moveThumb(event.getX());
                return true;
    
            case MotionEvent.ACTION_MOVE:
                moveThumb(event.getX());
                this.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
    
            default:
                return false;
        }
    }
    
    private void moveThumb(float x){
        float tickDistance = mAvailableWidth/mCount;//每一段距离
        int pos = getNearestTickPos(x);
        float nearestTickPosX = mLeftX+tickDistance*pos;//粘近position
        //if (pos==mCount) nearestTickPosX -= getPaddingRight();
        setThumbX(nearestTickPosX);
        if (mListener!=null) {
            if (mCurrentPosition!=pos) 
                mListener.onIndicatorChanged(this, pos, nearestTickPosX);
        }
        mCurrentPosition = pos;
    }
    
    int getNearestTickPos(float x){
        float tickDistance = mAvailableWidth/mCount;
        float originX = (x-mLeftX+tickDistance/2f)/tickDistance;
        //Log.e(mThumbX/tickDistance+"", (int)(mThumbX/tickDistance)+"");
        //Log.e(originX+"", (int)originX+"");
        return (int) originX;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas);
        drawTicks(canvas);
        drawThumb(canvas);
    }
    
    void initTrackPaint(){
        mTrackPaint = new Paint();
        mTrackPaint.setColor(DEFAULT_BAR_COLOR);
        mTrackPaint.setStrokeWidth(DEFAULT_BAR_WEIGHT_PX);
        mTrackPaint.setAntiAlias(true);
    }
    
    private void setThumbX(float x){
        mThumbX = x;
        invalidate();
    }
    
    private void drawTrack(Canvas canvas) {
        canvas.drawLine(mLeftX-getPaddingLeft(), getHeight()/2, mLeftX+mAvailableWidth+getPaddingRight(), getHeight()/2, mTrackPaint);
    }
    
    private void drawTicks(Canvas canvas) {
        int count = mCount;
        //int availableLength = getWidth() - getPaddingLeft() - getPaddingRight();
        for (int i = 0; i < count; i++) {
            final float x = mAvailableWidth/count*i+mLeftX;
            canvas.drawLine(x, 0, x, getHeight()/2, mTrackPaint);
        }
        canvas.drawLine(mLeftX+mAvailableWidth, 0, mLeftX+mAvailableWidth, getHeight()/2, mTrackPaint);
    }
    
    private void drawThumb(Canvas canvas) {
        if (mThumb!=null) {
            canvas.drawBitmap(mThumb, mThumbX - mThumb.getWidth()/2, getHeight()/2-mThumb.getHeight()/2, null);
        }else {
            canvas.drawCircle(mThumbX, getHeight()/2, 10, mTrackPaint);
        }
    }
    
    public interface OnIndicatorChangeListener{
        public void onIndicatorChanged(IndicatorBar indicatorBar,int position,float xAtPosition);
    }
}
