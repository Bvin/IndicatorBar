package cn.bvin.widget.indicatorbar;

import cn.bvin.widget.seekbarindicator.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class IndicatorBar extends View{

    private static final float DEFAULT_BAR_WEIGHT_PX = 2;
    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
    private static final int DEFAULT_POSITION_COUNT = 8;
    private static final int DEFAULT_POSITION_CURRENT = 4;
    
    private int mDefaultWidth = 500;
    private int mDefaultHeight = 100;
    
    private float mLeftX = 0;//x
    private float mAvailableWidth = 0;//w
    
    private Paint mTrackPaint;
    
    private Bitmap mThumbNormal;
    private Bitmap mThumbHightlight;
    private float mThumbX;
    private int mCount = DEFAULT_POSITION_COUNT;
    private int mCurrentPosition = DEFAULT_POSITION_CURRENT;
    
    private int[] mHightlightPositions = {1,3,5};
    
    private OnIndicatorChangeListener mListener;
    
    public IndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTrackPaint();
        mThumbNormal = BitmapFactory.decodeResource(getResources(), R.drawable.range_seek_thumb);
        mThumbHightlight = BitmapFactory.decodeResource(getResources(), R.drawable.range_seek_thumb_focus);
    }
    
    /**
     * 设置高亮的position
     * @param hightlightPositions must don't out of internal range that start at 0 and end at maxPosition. 
     */
    public void setHightlightPositions(int[] hightlightPositions){
        mHightlightPositions = hightlightPositions;
    }
    
    /**
     * 设置切换监听器
     * @param listener first time not be call
     */
    public void setOnIndicatorChangeListener(OnIndicatorChangeListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置当前的Position
     * @param position if small than 0 or large than maxPosition will do nothing
     */
    public void setCurrentPosition(int position) {
        if (position<0||position>mCount+1) {
            return;
        }
        mCurrentPosition = position;
    }
    
    /**
     * 设置最大position
     * @param maxPosition start at 1
     */
    public void setMaxPosition(int maxPosition) {
        mCount = maxPosition-1;
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
        mAvailableWidth = w - getPaddingLeft() - getPaddingRight();
        if (mCurrentPosition!=0) {
            mThumbX = xOfPostion(mCurrentPosition);
            Log.e(mCurrentPosition+"", ""+mThumbX);
        }else {
            mThumbX = mLeftX;
            Log.e("mCurrentPosition not be set", ""+mThumbX);
        }
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
    
    /**
     * 移动Thumb
     * @param x screen x of the finger moved position
     */
    private void moveThumb(float x){
        int pos = getNearestTickPos(x);
        float thumbX = xOfPostion(pos);
        setThumbX(thumbX);
        if (mListener!=null) {
            if (mCurrentPosition!=pos) 
                mListener.onIndicatorChanged(this, pos, thumbX);
        }
        mCurrentPosition = pos;
    }
    
    /**
     * 根据position获取它所在的x坐标值
     * @param position
     * @return x坐标
     */
    float xOfPostion(int position){
        float tickDistance = mAvailableWidth/mCount;
        float nearestTickPosX = mLeftX+tickDistance*position;//粘近position
        return nearestTickPosX;
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
    
    /**
     * 设置thumb的横向位置并更新绘制
     * @param x 横向坐标
     */
    private void setThumbX(float x){
        mThumbX = x;
        invalidate();
    }
    
    private void drawTrack(Canvas canvas) {
        canvas.drawLine(mLeftX-getPaddingLeft(), getHeight()/2, mLeftX+mAvailableWidth+getPaddingRight(), getHeight()/2, mTrackPaint);
    }
    
    private void drawTicks(Canvas canvas) {
        int count = mCount;
        for (int i = 0; i < count; i++) {
            final float x = mAvailableWidth/count*i+mLeftX;
            canvas.drawLine(x, 0, x, getHeight()/2, mTrackPaint);
        }
        canvas.drawLine(mLeftX+mAvailableWidth, 0, mLeftX+mAvailableWidth, getHeight()/2, mTrackPaint);
    }
    
    private boolean isCurrentPositionHighlight() {
        if (mHightlightPositions!=null&&mHightlightPositions.length>0) {
            for (int i : mHightlightPositions) {
                if (mCurrentPosition+1==i) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void drawThumb(Canvas canvas) {
        final Bitmap bitmap = isCurrentPositionHighlight() ? mThumbHightlight : mThumbNormal;
        if (bitmap!=null) {
            canvas.drawBitmap(bitmap, mThumbX - bitmap.getWidth()/2, getHeight()/2-bitmap.getHeight()/2, null);
        }else {
            canvas.drawCircle(mThumbX, getHeight()/2, 10, mTrackPaint);
        }
    }
    
    public interface OnIndicatorChangeListener{
        public void onIndicatorChanged(IndicatorBar indicatorBar,int position,float xAtPosition);
    }
}
