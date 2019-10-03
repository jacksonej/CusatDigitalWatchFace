package wear.cusat.cusatdigitalwatchface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.TextPaint;
import android.view.SurfaceHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CanvasDigitalWatchFace extends CanvasWatchFaceService {


    Calendar mCalendar;
    SimpleDateFormat timeFormat,dateFormat;
    TextPaint timePaint,datePaint;
    long time;
    @Override
    public Engine onCreateEngine() {
        return new CanvasEngine();
    }


    private class CanvasEngine extends CanvasWatchFaceService.Engine {
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mCalendar= Calendar.getInstance();

            timeFormat = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault());
            dateFormat = new SimpleDateFormat("EEE MMMM dd", Locale.getDefault());


            timePaint = new TextPaint();
            timePaint.setColor(Color.WHITE);
            timePaint.setAntiAlias(true);
            // timePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            timePaint.setTextSize(20);

            datePaint = new TextPaint();
            datePaint.setColor(Color.WHITE);
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
        public void onTimeTick() {
            super.onTimeTick();
            time=System.currentTimeMillis();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
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



          /*  if(isAmbientMode){
                canvas.drawColor(Color.BLACK);

            }else{
                canvas.drawColor(Color.parseColor("#a64dff"));
            }*/
            canvas.drawText(timeText+"",timeX,timeY,timePaint);
            canvas.drawText(dateText+"",dateX,dateY,datePaint);        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */
        }
    }
}
