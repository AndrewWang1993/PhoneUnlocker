package whunf.unlockscreenbypath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wxm on 2015/5/17 in PC
 */
public class Unlock extends View implements View.OnTouchListener {
    Context context;
    int screenWidth = 720 - 400;
    int screenHeight = 1280 - 300;
    int[] colors = {Color.LTGRAY, Color.BLUE, Color.GREEN, Color.RED};
    float[][][] dotPos = new float[3][3][3];
    float[][][] touchedDotPos = new float[3][3][3];
    ArrayList linePoint = new ArrayList();
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
                dotPos[i][j][0] = widthDiv_3 * (1 + i);
                dotPos[i][j][1] = heightDiv_3 * (1 + j);
                dotPos[i][j][2] = colors[0];
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
//        drawSmallCircle(canvas, dotPos);
//        drawBigCircle(canvas, dotPos);
        linePoint.add(100f);
        linePoint.add(100f);

        linePoint.add(200f);
        linePoint.add(200f);

        linePoint.add(400f);
        linePoint.add(700f);

        linePoint.add(500f);
        linePoint.add(200f);
        drawLine(canvas, linePoint);

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

    private void drawBigCircle(Canvas canvas, float[][][] floats) {
        paint.setStyle(Paint.Style.STROKE);
        int radium = 50;
        paint.setStrokeWidth(3);
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                paint.setColor((int) floats[i][j][2]);
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
                    int[] touched_pos = getTouchDot(x, y);
                    setBigCircleColor(touched_pos);
                    linePoint.add(dotPos[touched_pos[0]][touched_pos[1]][0]);
                    linePoint.add(dotPos[touched_pos[0]][touched_pos[1]][1]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (getTouchDot(x, y) != null) {
                    int[] touched_pos = getTouchDot(x, y);
                    setBigCircleColor(touched_pos);
                    linePoint.add(dotPos[touched_pos[0]][touched_pos[1]][0]);
                    linePoint.add(dotPos[touched_pos[0]][touched_pos[1]][1]);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                clearBigCircleColor();
                linePoint.clear();
                invalidate();
                break;
        }
        return true;
    }

    private void drawLine(Canvas canvas, ArrayList<Float> arrayList_float) {
        int color = colors[2];
        paint.setColor(color);
        paint.setStrokeWidth(3);
        int size=arrayList_float.size();
        float[] line_point = new float[2 + (size - 4) * 2 + 2];
        Iterator iterator = arrayList_float.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            float valueX = (float) iterator.next();
            float valueY = (float) iterator.next();
            if (i == 0 || i==(size-3)*2) {
                line_point[i++] = valueX;
                line_point[i++] = valueY;
            } else {
                line_point[i++] = valueX;
                line_point[i++] = valueY;
                line_point[i++] = valueX;
                line_point[i++] = valueY;
            }

        }
        canvas.drawLines(line_point, paint);
    }

    public int[] getTouchDot(float x, float y) {
        int[] selectedDot = new int[2];
        int distance = 40;

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                if (getDistance(x, y, dotPos[i][j][0], dotPos[i][j][1]) < distance) {
                    selectedDot[0] = i;
                    selectedDot[1] = j;
                    return selectedDot;
                }
            }
        }
        return null;
    }

    private void setBigCircleColor(int[] pos) {
        dotPos[pos[0]][pos[1]][2] = colors[1];
    }

    private void clearBigCircleColor() {
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                dotPos[i][j][2] = colors[0];
            }
        }
    }


    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }
}
