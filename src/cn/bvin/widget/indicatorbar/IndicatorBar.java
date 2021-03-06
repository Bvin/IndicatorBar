package cn.bvin.widget.indicatorbar;

import java.util.Arrays;

import cn.bvin.widget.seekbarindicator.R;
import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class IndicatorBar extends View{

    private static final float DEFAULT_BAR_WEIGHT_PX = 2;
    private static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
    private static final int DEFAULT_POSITION_COUNT = 8;
    private static final int DEFAULT_POSITION_CURRENT = 4;
    private static final int DEFAULT_INDICATOR_TEXT_SIZE = 12;
    private static final int DEFAULT_CUR_HIGHLIGHT_INDICATOR_TEXT_SIZE = 17;
    private static final int DEFAULT_INDICATOR_TEXT_COLOR = DEFAULT_BAR_COLOR;
    private static final int DEFAULT_HIGHLIGHT_INDICATOR_TEXT_COLOR = Color.YELLOW;
    
    private int mDefaultWidth = 500;
    private int mDefaultHeight = 100;
    
    private float mLeftX = 0;//x
    private float mAvailableWidth = 0;//w
    
    private float mTrackThickness = DEFAULT_BAR_WEIGHT_PX;
    
    private Paint mTrackPaint;
    private Paint mNormalIndicatorPaint;
    private Paint mHighlightIndicatorPaint;
    private Paint mCurHighlightIndicatorPaint;
    
    private int mIndicatorTextColor = DEFAULT_INDICATOR_TEXT_COLOR;//指示器文字颜色
    private int mHighlightIndicatorTextColor = DEFAULT_HIGHLIGHT_INDICATOR_TEXT_COLOR;//高亮指示器文字颜色
    private float mIndicatorTextSize = DEFAULT_INDICATOR_TEXT_SIZE;//指示器文字大小
    private float mCurHighlightIndicatorTextSize = DEFAULT_CUR_HIGHLIGHT_INDICATOR_TEXT_SIZE;//当前高亮指示器文字大小
    
    private Bitmap mThumbNormal;
    private Bitmap mThumbHighlight;
    private float mThumbX;
    private int mCount = DEFAULT_POSITION_COUNT;
    private int mCurrentPosition = DEFAULT_POSITION_CURRENT;
    private int mIndicatorOffset = 0;//indicator和position的偏差
    private int[] mHighlightIndicators;
    
    private OnIndicatorChangeListener mListener;
    
    private boolean showTicks;//是否需要显示刻度
    private String mLowlightSelectedText;//非高亮选中的indicator需要显示的文字
    private String mMaxHighlightSelectedText;//高亮indicator中最大值选中时的文字
    
    public IndicatorBar(Context context) {
        super(context);
    }

    public IndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        initPaints();
    }

    public IndicatorBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
        initPaints();
    }

    
    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorBar, 0, 0);
        //Track厚度
        mTrackThickness = ta.getDimension(R.styleable.IndicatorBar_trackThickness, DEFAULT_BAR_WEIGHT_PX);
        mCount = ta.getInteger(R.styleable.IndicatorBar_maxPosition, 0);
        mCurrentPosition = ta.getInteger(R.styleable.IndicatorBar_currentPosition, 0);
        mIndicatorTextColor = ta.getColor(R.styleable.IndicatorBar_themeColor, 0);
        mHighlightIndicatorTextColor = ta.getColor(R.styleable.IndicatorBar_highlightColor, 0);
        int mThumbNormalResId = ta.getResourceId(R.styleable.IndicatorBar_thumb, 0);
        mThumbNormal = BitmapFactory.decodeResource(getResources(), mThumbNormalResId);
        int mThumbHighlightResId = ta.getResourceId(R.styleable.IndicatorBar_highlightThumb, 0);
        mThumbHighlight = BitmapFactory.decodeResource(getResources(), mThumbHighlightResId);
        mIndicatorTextSize = ta.getDimension(R.styleable.IndicatorBar_textSize, 0);
        mCurHighlightIndicatorTextSize = ta.getDimension(R.styleable.IndicatorBar_highlightTextSize, 0);
        mIndicatorOffset = ta.getInteger(R.styleable.IndicatorBar_indicatorOffset, 0);
        mLowlightSelectedText = ta.getString(R.styleable.IndicatorBar_lowlightSelectedText);
        mMaxHighlightSelectedText = ta.getString(R.styleable.IndicatorBar_maxHighlightSelectedText);
        showTicks = ta.getBoolean(R.styleable.IndicatorBar_showTicks, false);
        ta.recycle();
    }
    
    void initPaints(){
        mTrackPaint = new Paint();
        mTrackPaint.setColor(DEFAULT_BAR_COLOR);
        mTrackPaint.setStrokeWidth(mTrackThickness);
        mTrackPaint.setAntiAlias(true);
        
        DisplayMetrics dm = getResources().getDisplayMetrics();
        
        mNormalIndicatorPaint = new Paint();
        mNormalIndicatorPaint.setColor(mIndicatorTextColor);
        mNormalIndicatorPaint.setTextSize(mIndicatorTextSize);
        mNormalIndicatorPaint.setAntiAlias(true);
        
        mHighlightIndicatorPaint = new Paint();
        mHighlightIndicatorPaint.setColor(mHighlightIndicatorTextColor);
        mHighlightIndicatorPaint.setTextSize(mIndicatorTextSize);
        mHighlightIndicatorPaint.setAntiAlias(true);
        
        mCurHighlightIndicatorPaint = new Paint();
        mCurHighlightIndicatorPaint.setColor(mHighlightIndicatorTextColor);
        mCurHighlightIndicatorPaint.setTextSize(mCurHighlightIndicatorTextSize);
        mCurHighlightIndicatorPaint.setAntiAlias(true);
    }
    
    /**
     * 设置Thumb
     * @param normalThumbResId
     * @param highlightThumbResId
     */
    public void setThumbs(int normalThumbResId,int highlightThumbResId) {
        mThumbNormal = BitmapFactory.decodeResource(getResources(),normalThumbResId);
        mThumbHighlight = BitmapFactory.decodeResource(getResources(),highlightThumbResId);
    }
    
    /**
     * 设置主题颜色
     * @param normalColorResId 
     * @param highlightColorResId
     */
    public void setThemeColors(int normalColorResId,int highlightColorResId) {
        mIndicatorTextColor = getResources().getColor(normalColorResId,null);
        mHighlightIndicatorTextColor = getResources().getColor(highlightColorResId,null);
    }
    
    /**
     * 设置文字大小（单位是sp）
     * @param normalTextSize 普通文字大小
     * @param highlightTextSize 高亮文字大小
     */
    public void setTextSize(int normalTextSize,int highlightTextSize) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int unit = TypedValue.COMPLEX_UNIT_SP;
        mIndicatorTextSize = TypedValue.applyDimension(unit, normalTextSize, dm);
        mCurHighlightIndicatorTextSize = TypedValue.applyDimension(unit, highlightTextSize, dm);
    }
    
    /**
     * 
     * @param normalSelectedText 普通模式下选中的文字
     * @param maxHighlightSelectedText 高亮选中的最大值显示文字
     */
    public void setDescUnderThumb(String normalSelectedText,String maxHighlightSelectedText) {
        mLowlightSelectedText  = normalSelectedText;
        mMaxHighlightSelectedText = maxHighlightSelectedText;
    }
    
    /**
     * 设置高亮的Indicator，默认不排序
     * @param hightlightIndicators 
     */
    public void setHightlightIndicators(int[] hightlightIndicators){
        setHightlightIndicators(hightlightIndicators, false);
    }
    
    /**
     * 设置高亮的Indicator集合
     * @param hightlightIndicators
     * @param sort 是否排序
     */
    public void setHightlightIndicators(int[] hightlightIndicators,boolean sort) {
        mHighlightIndicators = hightlightIndicators;
        if (sort) Arrays.sort(mHighlightIndicators);
    }
    
    /**
     * 是否绘制刻度
     * @param isShowTicks
     */
    public void setShowTicks(boolean isShowTicks) {
        showTicks = isShowTicks;
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
     * 设置当前Indicator
     * @param indicator
     */
    public void setCurrentIndicator(int indicator) {
        setCurrentPosition(indicator-mIndicatorOffset);
    }
    
    /**
     * 设置Indicator最大值
     * @param max 
     */
    public void setMaxIndicator(int max) {
        mCount = max-mIndicatorOffset;
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

        int thumbHeight = 0;//默认为0
        if (mThumbNormal!=null&&mThumbHighlight!=null) {
            thumbHeight =  Math.max(mThumbNormal.getHeight(), mThumbHighlight.getHeight());
        }else if (mThumbNormal!=null) {
            thumbHeight = mThumbNormal.getHeight();
        }else if (mThumbHighlight!=null) {
            thumbHeight = mThumbHighlight.getHeight();
        }
        thumbHeight = thumbHeight*3;//三倍thumb高度
        
        // The RangeBar height should be as small as possible.
        if (measureHeightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDefaultHeight, measureHeight);
        } else if (measureHeightMode == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = mDefaultHeight;
        }
        height = thumbHeight>height?thumbHeight:height;
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
        if(showTicks)drawTicks(canvas);
        drawIndicatorTexts(canvas);
        drawThumb(canvas);
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
        for (int i = 0; i <= count; i++) {
            final float x = mAvailableWidth/count*i+mLeftX;
            canvas.drawLine(x, getTop()+getPaddingTop(), x, getHeight()/2, mTrackPaint);
        }
        //canvas.drawLine(mLeftX+mAvailableWidth, 0, mLeftX+mAvailableWidth, getHeight()/2, mTrackPaint);
    }
    
    private void drawIndicatorTexts(Canvas canvas) {
        int count = mCount;
        for (int i = 0; i <= count; i++) {
            final float x = mAvailableWidth/count*i+mLeftX;
            Paint paint;
            if (isPositionOnHighlight(i)) {//当前选中的position
                if (i == mCurrentPosition) {
                    paint = mCurHighlightIndicatorPaint;
                    //高亮的indicator被选中时绘制
                    if (!TextUtils.isEmpty(mMaxHighlightSelectedText)&&isHighlightMaxPosition(i)) {
                        drawTextUnderThumb(canvas, mMaxHighlightSelectedText, x, paint);
                    }
                }else {
                    paint = mHighlightIndicatorPaint;
                }
            } else {//没选中的position
                paint = mNormalIndicatorPaint;
                //非高亮的indicator被选中时绘制
                if (!TextUtils.isEmpty(mLowlightSelectedText)&&i == mCurrentPosition) {
                    drawTextUnderThumb(canvas, mLowlightSelectedText, x, paint);
                }
            }
            String text = String.valueOf(i+mIndicatorOffset);
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);
            //Text.x = (Pos.x - Text.w/2),Text.y = getTop+Text.h/3
            //绘制track上方的indicator文字
            canvas.drawText(text, x-textRect.width()/2, getTop()+textRect.height()/3, paint);
        }
    }
    
    /**
     * 是否是最大的position
     * @param position start with 0
     * @return true if equals
     */
    private boolean isHighlightMaxPosition(int position) {
        if (mHighlightIndicators!=null&&mHighlightIndicators.length>0) {
            //position参数是从0计数的，mHighlightPositions是外部设置的从0开始的
            //mHighlightPositions必须是有序的，否则就不准了
            return position+mIndicatorOffset == mHighlightIndicators[mHighlightIndicators.length-1];
        }
        return false;
    }
    
    /**
     * 在Thumb下绘制文字
     * @param canvas
     * @param text
     * @param x
     * @param paint
     */
    private void drawTextUnderThumb(Canvas canvas,String text, float x,Paint paint) {
        drawTextOnCenterX(canvas, text, x, getBottom() - getPaddingBottom() - getTop(), paint);
    }
    
    /**
     * 绘制Text,Text横向以x坐标居中
     * @param canvas The canvas on which the background will be drawn
     * @param text The text to be drawn
     * @param x TextX坐标
     * @param y TextY坐标
     * @param paint The paint used for the text (e.g. color, size, style)
     */
    private void drawTextOnCenterX(Canvas canvas,String text, float x, float y, Paint paint) {
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        canvas.drawText(text, x-textRect.width()/2, y, paint);
    }
    
    private boolean isPositionOnHighlight(int position) {
        if (mHighlightIndicators!=null&&mHighlightIndicators.length>0) {
            for (int i : mHighlightIndicators) {
                if (position+mIndicatorOffset==i) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isCurrentPositionHighlight() {
        return isPositionOnHighlight(mCurrentPosition);
    }
    
    private void drawThumb(Canvas canvas) {
        final Bitmap bitmap = isCurrentPositionHighlight() ? mThumbHighlight : mThumbNormal;
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
