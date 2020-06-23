package mark.dietzler.mdietzlerlab7;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Canvas;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

public class Clock extends View implements TimeAnimator.TimeListener {

    TimeAnimator mTimer = new TimeAnimator();
    SoundPool soundpool;
    public HourMinSec hourMinSec = new HourMinSec();

    //primitives
    private final static float OVERALL_WIDTH = 100.0f, OVERALL_HEIGHT = 100.0f, M_SECOND_HAND_LENGTH = 38.0f;
    private final static float ASPECT_RATIO = 1f;
    private final static float[] mMinute_Hand_Coordinates = new float[]{-02.5f, 0, 0, 40.25f, 02.5f, 0, 0, -02.5f, -02.5f, 0};
    private final static float[] mHour_Hand_Coordinates = new float[]{-05f, 0, 0, 30.5f, 05f, 0, 0, -05f, -05f, 0};
    private static final float mDEGREES_SECOND = 360/60;
    private int mWidth, mHeight, mStoredSecond;
    private int second_hand_click;
    private int mUpdateDelay = 0;
    private long mElapsedTimer;
    private Boolean mDigital_Clock_24hour_Format = Boolean.FALSE, mPartial_Seconds_Flag = Boolean.FALSE, mPlay_Clock_Tick = false;
    //strings
    private String mClock_Face_String = "";

    public void SetUpdateDelay(int newDelay) {
        mUpdateDelay = newDelay;
    }

    //setters and getters
    public boolean get24HourClock(){
        return mDigital_Clock_24hour_Format;
    }

    public void set24Clock(boolean newSetting) {
        mDigital_Clock_24hour_Format = newSetting;
    }

    public boolean getPartialSeconds() {
        return mPartial_Seconds_Flag;
    }

    public void setPartialSeconds(boolean newState) {
        mPartial_Seconds_Flag = newState;
    }

    public String GetClockFace() {
        return mClock_Face_String;
    }

    public void SetClockFace(String newClockFace) {
        mClock_Face_String = newClockFace;
    }

    //constructors
    public Clock(Context context) {
        super(context);
        initializeMe(context);
    }

    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeMe(context);
    }

    public Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeMe(context);
    }

    private void initializeMe(Context context){
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mTimer.setTimeListener(this);
        startTimer();
        mElapsedTimer = 0;

        //load the click sound
        soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("click.mp3");
            second_hand_click = soundpool.load(descriptor,0);

        } catch (IOException e) {
            Log.e("error","failed to load sound files");
        }

    }

    //observer class for updating digital clock in main activity
    class HourMinSec extends Observable{
        int mHour, mMin, mSec;

        public int getHour() {
            return mHour;
        }

        public int getMin() {
            return mMin;
        }

        public int getSec() {
            return mSec;
        }

        public void setHMS(int h, int m, int s){
            mHour = h;
            mMin = m;
            mSec = s;
            setChanged();
            notifyObservers("Clock");
        }
    }

    @Override
    public void onDraw(Canvas canvas){

        //pick a clock face back ground image
        if(GetClockFace().equalsIgnoreCase("regular numerals")) {
            Resources resources = getResources();
            Bitmap modernBitmap = BitmapFactory.decodeResource(resources, R.drawable.modern);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0, modernBitmap.getWidth(), modernBitmap.getHeight());
            canvas.drawBitmap(modernBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("roman numerals")){
            Resources resources = getResources();
            Bitmap romanBitmap = BitmapFactory.decodeResource(resources, R.drawable.roman);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0,romanBitmap.getWidth(), romanBitmap.getHeight());
            canvas.drawBitmap(romanBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("crazy face")){
            Resources resources = getResources();
            Bitmap crazyBitmap = BitmapFactory.decodeResource(resources, R.drawable.crazy);
            //Rect dst = new Rect((int)(-overallWidth/2),(int)(overallHeight/2), (int)(overallWidth/2), (int)(overallHeight/2));
            Rect dst = new Rect(0,0, mWidth, mHeight);
            Rect src = new Rect(0,0,crazyBitmap.getWidth(),crazyBitmap.getHeight());
            canvas.drawBitmap(crazyBitmap,null, dst, null);
        }
        else if(GetClockFace().equalsIgnoreCase("math face")){
            Resources resources = getResources();
            Bitmap mathBitmap = BitmapFactory.decodeResource(resources, R.drawable.math);
            Rect dst = new Rect(0,0,mWidth,mHeight);
            Rect src = new Rect(0,0, mathBitmap.getWidth(), mathBitmap.getHeight());
            canvas.drawBitmap(mathBitmap,null,dst,null);
        }


        float width = getWidth();
        float height = getHeight();
        float scaleX = (width/ OVERALL_WIDTH);
        float scaleY = (height/ OVERALL_HEIGHT);
        canvas.save();

        canvas.scale(scaleX , -scaleY);
        canvas.translate(OVERALL_WIDTH /2.0f, -OVERALL_HEIGHT /2.0f);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.RED);


        canvas.save();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        int retrieved_hour = gregorianCalendar.get(Calendar.HOUR);
        int retrieved_minute = gregorianCalendar.get(Calendar.MINUTE);
        int retrieved_second = gregorianCalendar.get(Calendar.SECOND);

        if(mStoredSecond != retrieved_second) {
            mPlay_Clock_Tick = true;
            mStoredSecond = retrieved_second;
        } else {
            //do nothing
        }

        int retrieved_millisecond = gregorianCalendar.get(Calendar.MILLISECOND);

        float hourTick = 30/60 * retrieved_minute;

        //draw hour hand
        canvas.save();
        paint.setColor(Color.RED);
        Path hourPath = new Path();
        hourPath.moveTo(mHour_Hand_Coordinates[0], mHour_Hand_Coordinates[1]);
        hourPath.lineTo(mHour_Hand_Coordinates[2], mHour_Hand_Coordinates[3]);
        hourPath.lineTo(mHour_Hand_Coordinates[4], mHour_Hand_Coordinates[5]);
        hourPath.lineTo(mHour_Hand_Coordinates[6], mHour_Hand_Coordinates[7]);
        hourPath.lineTo(mHour_Hand_Coordinates[8], mHour_Hand_Coordinates[9]);
        hourPath.close();
        canvas.rotate(-(mDEGREES_SECOND * 5) * retrieved_hour + hourTick);
        canvas.drawPath(hourPath, paint);
        canvas.restore();

        //draw minute hand
        canvas.save();
        paint.setColor(Color.BLACK);
        Path minutePath = new Path();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        minutePath.moveTo(mMinute_Hand_Coordinates[0], mMinute_Hand_Coordinates[1]);
        minutePath.lineTo(mMinute_Hand_Coordinates[2], mMinute_Hand_Coordinates[3]);
        minutePath.lineTo(mMinute_Hand_Coordinates[4], mMinute_Hand_Coordinates[5]);
        minutePath.lineTo(mMinute_Hand_Coordinates[6], mMinute_Hand_Coordinates[7]);
        minutePath.lineTo(mMinute_Hand_Coordinates[8], mMinute_Hand_Coordinates[9]);
        minutePath.close();
        canvas.rotate(-mDEGREES_SECOND * retrieved_minute);
        canvas.drawPath(minutePath, paint);
        canvas.restore();

        //draw second hand
        paint.setColor((Color.RED));

        if(mPartial_Seconds_Flag){
            // second_plus_scaled_millisecond is the retrieved second PLUS
            // the retrieved milliseconds divided by 1000 to get a decimal value
            // i.e. 153 ms / 1000 = .153 plus 1 second = 1.153 seconds
            double second_plus_scaledMillisecond = (retrieved_second + (retrieved_millisecond/1000.0));

            canvas.rotate((float)second_plus_scaledMillisecond * -mDEGREES_SECOND);
        } else {
            canvas.rotate(retrieved_second * -mDEGREES_SECOND);
        }
        canvas.drawLine(0, 0,0, M_SECOND_HAND_LENGTH,paint);
        canvas.restore();

        paint.setColor(Color.BLACK);
        //draw big circle ticks
        for(int x=0;x<=12;x++){
            canvas.save();
            canvas.rotate(x*30);
            canvas.drawLine(0,40,0,50, paint);
            canvas.restore();
        }

        //draw little circle ticks
        paint.setStrokeWidth(0.5f);
        for(int i=0;i<=60;i++){
            canvas.save();
            canvas.rotate(i*6);
            canvas.drawLine(0,45, 0, 50, paint);
            canvas.restore();
        }

        canvas.restore();

        if(mDigital_Clock_24hour_Format) {
            hourMinSec.setHMS(retrieved_hour, retrieved_minute, retrieved_second);
        }
        else{
            hourMinSec.setHMS(gregorianCalendar.get(Calendar.HOUR),retrieved_minute,retrieved_second);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        float wSize = View.MeasureSpec.getSize(widthMeasureSpec);
        float hSize = View.MeasureSpec.getSize(heightMeasureSpec);
        float width, height;
        height = wSize / ASPECT_RATIO;
        width = hSize * ASPECT_RATIO;
        if(height>hSize){
            this.setMeasuredDimension((int)width, (int)hSize);
        }
        else {

            this.setMeasuredDimension((int) wSize, (int) height);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
    }

    public void startTimer() {
        mTimer.start();
    }


    @Override
    public void onTimeUpdate(TimeAnimator animation, long total_run_Time, long elapsed_since_last_call) {

        if(mElapsedTimer > mUpdateDelay) { //if cumulative deltaTime is greater than the update delay time
            //set the cumulative deltaTime to zero
            mElapsedTimer = 0;
            // invalidate to invoke onDraw
            invalidate();
        } else {  //animation delay time not elapsed, add current delta time to cumulative delta time and continue timeanimator animation
            mElapsedTimer = mElapsedTimer + elapsed_since_last_call;
        }

        //finally, play the click sound if the second hand has "clicked" (excludes "smooth" second hand animation, obviously)
        if(mPlay_Clock_Tick && !mPartial_Seconds_Flag) {
            //play the click sound
            soundpool.play(second_hand_click,1,1,0,0,1);
            //set the tick to false again
            mPlay_Clock_Tick = false;
        }
    }
}
