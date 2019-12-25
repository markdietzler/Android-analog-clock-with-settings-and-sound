package mark.dietzler.mdietzlerlab7;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AnalogClock extends View {

    final static float overallWidth = 100.0f;
    final static float overallHeight = 100.0f;
    final static float clockRadius = 50.0f;
    final static float handLength = 40.0f;
    final static float tailLength = 10.0f;
    final static float ASPECT_RATIO = 1f;

    private int mHeight;
    private int mWidth;

    private Path mHourHand;
    private Path mMinuteHand;
    private Paint paint;

    private static long TIMER_MSEC = 100;
    long mLastTimne = System.currentTimeMillis();

    private TimeAnimator mTimer = new TimeAnimator();

    public ObservableTimeTransfer timeTransfer = new ObservableTimeTransfer();

    private static final int mBOXHEIGHT = 320;
    private static final int mBOXWIDTH = 320;

    private static final float mSECONMD_DEGREES = 360/-60;

    final int mMinuteHandColor = Color.argb(255,216,49,94);
    final int mHourHandColor = Color.argb(255,0,9,246);

    private static final float[] mHourHandCoordinates = {0,-15,-10,-5,-10,5,0,60,10,5,10,-5};
    private static final float[] mMintueHandCoordinates = {0,-10,-5,-5,-5,5,0,135,5,5,5,-5};

    public AnalogClock(Context context) {
        super(context);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public AnalogClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        paint = new Paint();
        mHourHand = new Path();
        mMinuteHand = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRGB(118,113,192);


        GregorianCalendar clock = new GregorianCalendar();

        float scaleWidth = mWidth/mBOXWIDTH; //box is 320 wide
        float scaleHeight = -1 * mHeight/mBOXHEIGHT; //box is 320 tall
        canvas.scale(scaleWidth,scaleHeight);

        canvas.translate(160,-160);

        canvas.save();
        int second = clock.get(Calendar.SECOND);
        canvas.rotate(mSECONMD_DEGREES * second);
        drawSecondHand(canvas);
        canvas.restore();


        canvas.save();
        int hour = clock.get(Calendar.HOUR);
        float hourTick = 30/60 * clock.get(Calendar.MINUTE);
        canvas.rotate((mSECONMD_DEGREES * 5) * hour + hourTick);
        drawHourHand(canvas);
        canvas.restore();

        canvas.save();
        int minute = clock.get(Calendar.MINUTE);
        canvas.rotate(mSECONMD_DEGREES * minute);
        drawMinuteHand(canvas);
        canvas.restore();

        canvas.save();
        drawClockCircle(canvas);
        canvas.restore();

        canvas.save();
        drawClockTicks(canvas);
        canvas.restore();

        int milliseconds = clock.get(Calendar.MILLISECOND);

        int ampm = clock.get(Calendar.PM);

        if(ampm == 1) {
            hour += 12;
        } else  {

        }

        timeTransfer.setTime(hour, minute, second, milliseconds);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldHeight, oldHeight);
        mHeight = height;
        mWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        Log.v("[View name] onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("[View name] onMeasure h", MeasureSpec.toString(heightMeasureSpec));

        int desiredHeight = 320;
        int desiredWidth = 320;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        setMeasuredDimension(width,height);
    }

    private void drawMinuteHand(Canvas canvas) {
        paint.reset();
        paint.setColor(mMinuteHandColor);
        paint.setAntiAlias(true);
        mMinuteHand.moveTo(0,0);
        mMinuteHand.lineTo(mMintueHandCoordinates[0],mMintueHandCoordinates[1]);
        mMinuteHand.lineTo(mMintueHandCoordinates[2],mMintueHandCoordinates[3]);
        mMinuteHand.lineTo(mMintueHandCoordinates[4],mMintueHandCoordinates[5]);
        mMinuteHand.lineTo(mMintueHandCoordinates[6],mMintueHandCoordinates[7]);
        mMinuteHand.lineTo(mMintueHandCoordinates[8],mMintueHandCoordinates[9]);
        mMinuteHand.lineTo(mMintueHandCoordinates[10],mMintueHandCoordinates[11]);
        mMinuteHand.lineTo(mMintueHandCoordinates[0],mMintueHandCoordinates[1]);
        mMinuteHand.close();
        canvas.drawPath(mMinuteHand, paint);
    }

    private void drawHourHand(Canvas canvas) {
        paint.reset();
        paint.setColor(mHourHandColor);
        paint.setAntiAlias(true);
        mHourHand.moveTo(0,0);
        mHourHand.lineTo(mHourHandCoordinates[0],mHourHandCoordinates[1]);   //1
        mHourHand.lineTo(mHourHandCoordinates[2],mHourHandCoordinates[3]);   //2
        mHourHand.lineTo(mHourHandCoordinates[4],mHourHandCoordinates[5]);   //3
        mHourHand.lineTo(mHourHandCoordinates[6],mHourHandCoordinates[7]);   //4
        mHourHand.lineTo(mHourHandCoordinates[8],mHourHandCoordinates[9]);   //5
        mHourHand.lineTo(mHourHandCoordinates[10],mHourHandCoordinates[11]);   //6
        mHourHand.lineTo(mHourHandCoordinates[0],mHourHandCoordinates[1]); //15
        mHourHand.close();
        canvas.drawPath(mHourHand, paint);
    }

    private void drawSecondHand(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawLine(0,0,0,145,paint);
    }

    private void drawClockCircle(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        int radius = 158;
        canvas.drawCircle(0,0,radius,paint);
    }

    private void drawClockTicks(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.WHITE);
        //paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.save();
        float hourTicker = 0;
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,152,0,158,paint);
        canvas.rotate(mSECONMD_DEGREES);
        canvas.drawLine(0,147,0,152,paint);
        canvas.drawLine(0,152,0,158,paint);

        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTimer.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                long now = System.currentTimeMillis();
                if((now-mLastTimne)<TIMER_MSEC)
                    return;
                mLastTimne = now;
                postInvalidate();
            }
        });
        mTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimer.cancel();
        mTimer.setTimeListener(null);
        mTimer.removeAllListeners();
        mTimer = null;
    }

}
