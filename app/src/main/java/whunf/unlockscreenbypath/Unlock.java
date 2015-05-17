package whunf.unlockscreenbypath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wxm on 2015/5/17 in PC
 */
public class Unlock extends View implements View.OnTouchListener {
    Context context;
    int screenWidth = 720 - 400;
    int screenHeight = 1280 - 300;
    int[] colors = {Color.LTGRAY, Color.BLUE, Color.GREEN, Color.RED};
    int bigCircleColors = colors[0];
    float[][][] dotPos = new float[3][3][2];
    float[][][] touchedDotPos = new float[3][3][2];
    Paint paint = new Paint();

    public Unlock(Context context) {
        super(context);
    }

    public Unlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        float widthDiv_3 = screenWidth / 1.7f;
        float heightDiv_3 = screenHeight / 3;
        paint.setAntiAlias(true);
        paint.setDither(true);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                for (int k = 0; k <= 1; k++) {
                    dotPos[i][j][k] = k % 2 == 0 ? widthDiv_3 * (1 + i) : heightDiv_3 * (1 + j);
                }
            }
        }
        setOnTouchListener(this);
    }

    public Unlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSmallCircle(canvas, dotPos);
        drawBigCircle(canvas, dotPos, bigCircleColors);
    }

    private void drawSmallCircle(Canvas canvas, float[][][] floats) {
        int color = colors[1];
        int radium = 10;
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                canvas.drawCircle(floats[i][j][0], floats[i][j][1], radium, paint);
            }
        }
    }

    private void drawBigCircle(Canvas canvas, float[][][] floats, int color) {
        int radium = 50;
        paint.setStrokeWidth(3);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                canvas.drawCircle(floats[i][j][0], floats[i][j][1], radium, paint);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getTouchDot(x, y) != null) {
                    bigCircleColors = colors[2];

                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        float touchX = event.getX();
        float touchY = event.getY();


        return false;
    }

    private void drawLine(Canvas canvas, float[] floats) {
        int color = colors[2];
    }

    private float[] getTouchDot(float x, float y) {
        float[] selectedDotPos = new float[2];
        //定义自动连接的模糊距离
        int distance = 20;

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                if (getDistance(x, y, dotPos[i][j][0], dotPos[i][j][1]) < distance) {
                    selectedDotPos[0] = dotPos[i][j][0];
                    selectedDotPos[1] = dotPos[i][j][1];
                    return selectedDotPos;
                }
            }
        }

        return null;
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }
}
