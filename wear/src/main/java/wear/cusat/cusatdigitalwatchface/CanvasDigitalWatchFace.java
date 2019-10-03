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
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CanvasDigitalWatchFace extends CanvasWatchFaceService {


    Calendar mCalendar;
    SimpleDateFormat timeFormat,dateFormat;
    TextPaint timePaint,datePaint;
    long time;
    boolean isAmbientMode;
    Bitmap mBackgroundBitmap;
    int id=100001;
    private  final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private Paint mBackgroundPaint;


    @Override
    public Engine onCreateEngine() {
        return new CanvasEngine();
    }


    private class CanvasEngine extends CanvasWatchFaceService.Engine {


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

            mCalendar= Calendar.getInstance();
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);
            timeFormat = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault());
            dateFormat = new SimpleDateFormat("EEE MMMM dd", Locale.getDefault());
            final int backgroundResId = R.drawable.beautiful_scenery;
            mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundResId);


            timePaint = new TextPaint();
            timePaint.setColor(Color.BLUE);
            timePaint.setAntiAlias(true);
            // timePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            timePaint.setTextSize(20);

            datePaint = new TextPaint();
            datePaint.setColor(Color.BLUE);
            datePaint.setAntiAlias(true);
            // datePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            datePaint.setTextSize(15);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            float  mScale = ((float) width) / (float) mBackgroundBitmap.getWidth();
            mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, (int) (mBackgroundBitmap.getWidth() * mScale), (int) (mBackgroundBitmap.getHeight() * mScale), true);
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
        public void onDraw(Canvas canvas, Rect bounds) {

            time=System.currentTimeMillis();
            // Log.d("hai","time  "+time);

            String timeText=timeFormat.format(time);
            Rect timeBounds=new Rect();

            String dateText=dateFormat.format(time);
            Rect dateBounds=new Rect();

            timePaint.getTextBounds(timeText, 0, timeText.length(), timeBounds);
            int timeX = Math.abs(bounds.centerX() - timeBounds.centerX());
            int timeY = Math.round((Math.abs(bounds.centerY())) - (bounds.height() * 0.08f));

            datePaint.getTextBounds(dateText, 0, dateText.length(), dateBounds);
            int dateX = Math.abs(bounds.centerX() - dateBounds.centerX());
            int dateY = Math.round((Math.abs(bounds.centerY())) - (bounds.height() * 0.02f));

            canvas.save();

           if(isAmbientMode){
                canvas.drawColor(Color.BLACK);
               timePaint.setColor(Color.WHITE);
               datePaint.setColor(Color.WHITE);
            }else{
               canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
            }
            canvas.drawText(timeText+"",timeX,timeY,timePaint);
            canvas.drawText(dateText+"",dateX,dateY,datePaint);


            canvas.restore();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateTimer();
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
