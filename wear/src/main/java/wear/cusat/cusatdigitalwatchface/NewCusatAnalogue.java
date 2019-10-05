package wear.cusat.cusatdigitalwatchface;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NewCusatAnalogue  extends CanvasWatchFaceService {


    private  final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final float SECOND_TICK_STROKE_WIDTH = 2f;

    private static final float HAND_END_CAP_RADIUS = 4f;
    private static final float STROKE_WIDTH = 4f;
    private static final int SHADOW_RADIUS = 6;

    private Calendar mCalendar;
    private Paint mTickAndCirclePaint,mHandPaint;
    private Bitmap mBackgroundBitmap;
    private Paint mBackgroundPaint;
    private int mCenterX;
    private int mCenterY;
    private float mHourHandLength;
    private float mMinuteHandLength;
    private float mSecondHandLength;

    int id;
    private long time;
    private boolean isAmbientMode;


    @Override
    public Engine onCreateEngine() {
        return new WatchFaceEngine();
    }

    private class WatchFaceEngine extends Engine{

        @SuppressLint("HandlerLeak")
        private final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (id == msg.what) {
                    invalidate();
                    if(shouldbeRunning()) {
                        long timeMs = System.currentTimeMillis();
                        long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                        Log.d("hai", "delay " + delayMs);
                        handler.sendEmptyMessageDelayed(id, delayMs);
                    }
                }
            }
        };



        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mCalendar = Calendar.getInstance();

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            final int backgroundResId = R.drawable.beautiful_scenery;
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundResId);


            mHandPaint = new Paint();
            mHandPaint.setColor(Color.WHITE);
            mHandPaint.setStrokeWidth(STROKE_WIDTH);
            mHandPaint.setAntiAlias(true);
            mHandPaint.setStrokeCap(Paint.Cap.ROUND);
            mHandPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, Color.BLACK);
            mHandPaint.setStyle(Paint.Style.STROKE);

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor(Color.CYAN);
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);


        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mCenterX =width/2;
            mCenterY =height/2;
            mHourHandLength = mCenterX * 0.4f;
            mMinuteHandLength = mCenterX * 0.6f;
            mSecondHandLength = mCenterX * 0.7f;
            float  mScale = ((float) width) / (float) mBackgroundBitmap.getWidth();

            mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                    (int) (mBackgroundBitmap.getWidth() * mScale),
                    (int) (mBackgroundBitmap.getHeight() * mScale), true);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
           /* if(isAmbientMode){
                canvas.drawColor(Color.BLACK);
            }else{
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
            }*/
            canvas.drawColor(Color.BLACK);

            final float hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f;
            final float minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f;
            final float seconds = (mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f);
            final float secondsRotation = seconds * 6f;
            final float hoursRotation = (mCalendar.get(Calendar.HOUR) * 30) + hourHandOffset;




            float innerSmallTickRadius  ;
            float outerSmallTickRadius = mCenterX-5;
            for (int tickIndex = 0; tickIndex < 60; tickIndex++) {
                if(tickIndex%5==0){
                    innerSmallTickRadius = mCenterX - 20;
                }else{
                    innerSmallTickRadius = mCenterX - 8;
                }
                float tickRot = (float) (tickIndex * Math.PI * 2 / 60);
                float innerX = (float) Math.cos(tickRot) * innerSmallTickRadius;
                float innerY = (float) Math.sin(tickRot) * innerSmallTickRadius;
                float outerX = (float) Math.cos(tickRot) * outerSmallTickRadius;
                float outerY = (float) Math.sin(tickRot) * outerSmallTickRadius;
                canvas.drawLine(
                        mCenterX + innerX,
                        mCenterY + innerY,
                        mCenterX + outerX,
                        mCenterY + outerY,
                        mTickAndCirclePaint);
            }


            canvas.save();

            canvas.rotate(hoursRotation, mCenterX, mCenterY);
            drawHand(canvas, mHourHandLength,false);
            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY);
            drawHand(canvas, mMinuteHandLength,false);
            canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY);
            drawHand(canvas, mSecondHandLength,true);

            canvas.drawCircle(mCenterX, mCenterY, HAND_END_CAP_RADIUS, mHandPaint);


            canvas.restore();


        }



        private void drawHand(Canvas canvas, float handLength ,boolean isSecond) {
            if(isSecond)
                canvas.drawLine(mCenterX, mCenterY+20, mCenterX , mCenterY-handLength , mHandPaint);
            else
                canvas.drawRoundRect(mCenterX - HAND_END_CAP_RADIUS, mCenterY - handLength,
                        mCenterX + HAND_END_CAP_RADIUS, mCenterY + HAND_END_CAP_RADIUS,
                        HAND_END_CAP_RADIUS, HAND_END_CAP_RADIUS, mHandPaint);        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            time=System.currentTimeMillis();
            invalidate();
        }


        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            isAmbientMode=inAmbientMode;
            updateTimer();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        private void updateTimer(){
            handler.removeMessages(id);
            if(shouldbeRunning()) {
                handler.sendEmptyMessage(id);
            }
        }


        private boolean shouldbeRunning(){
            return  isVisible() && !isInAmbientMode();
        }



    }


}
