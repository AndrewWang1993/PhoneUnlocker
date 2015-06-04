package com.example.whunf.phonelock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodSubtype;
import android.webkit.WebHistoryItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by whunf on 2015/5/20 in PC
 */
public class MyPhoneLocker extends View implements View.OnTouchListener {

    //屏幕的宽和高
    int screen_width = 720;
    int screen_height = 1080;

    //定义颜色数组
    int[] colors = {Color.DKGRAY, Color.GREEN, Color.BLUE, Color.RED};

    //使用三重数组记录九个点的位置（x，y坐标信息）和颜色信息
    int[][][] dotPos = new int[3][3][3];

    //定义大圆小圆的半径
    int small_radium = 8;
    int big_radium = 40;

    //定义画笔对象
    Paint paint = new Paint();

    //定义一个模糊距离
    int connect_distance = 30;

    //定义一个HashSet集合类，帮助手指已经移动过的点
    ArrayList<Integer> arrayList = new ArrayList();

    //新建一个二重数组记录手指到点的线段
    int[][] touchPot = new int[2][2];


    //新建二维数组储存正确的解锁路径
    int[][] correctPath = {
            {0, 1},
            {1, 0},
            {1, 1},
            {1, 2}
    };


    //新建二维数组存储用户触摸的路径
    int[][] userPath = new int[4][2];

    //定义一个变量用于记录用户已经点击的点
    int iTouch = 0;

    //新建两个变量判断这个点是否已经被滑过
    int _x = 0;
    int _y = 0;

    //定义变量表示手机是否解锁成功
    boolean isUnlock = false;


    //构造函数
    public MyPhoneLocker(Context context) {
        super(context);
        Log.i("TAG", "main");
        int m_width_div = (screen_width - 50) / 4;
        int m_height_div = (screen_height - 400) / 4;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                dotPos[i][j][0] = m_width_div * (j + 1);
                dotPos[i][j][1] = m_height_div * (i + 1);
                dotPos[i][j][2] = colors[0];
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                touchPot[i][j] = 0;
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                userPath[i][j] = -1;
            }
        }

        //初始化画笔对象
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        //设置触摸事件的监听器
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSmallCircle(canvas);
        drawBigCircle(canvas);
        drawLine(canvas);
        drawFinger2Dot(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x_pos = (int) event.getX();
        int y_pos = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isToucheDot(x_pos, y_pos)) {
                    invalidate();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isToucheDot(x_pos, y_pos)) {
                    Log.i("userPath", userPath[0][0] + " " + userPath[0][1] + " " + userPath[1][0] + " " +
                            " " + userPath[1][1] + " " + userPath[2][0] + " " +
                            " " + userPath[2][1] + " " + userPath[3][0] + " " +
                            " " + userPath[3][1]);
                    if (checkPath()) {
                        Log.i("TAG", "手机已经解锁");
                        isUnlock=true;
                    }
                    invalidate();
                } else {
                    touchPot[1][0] = x_pos;
                    touchPot[1][1] = y_pos;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setBigCircleToGrey();
                arrayList.clear();
                clearFinger2Dot();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    //画大圆
    private void drawBigCircle(Canvas canvas) {
        paint.setStrokeWidth(5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                paint.setColor(dotPos[i][j][2]);
                canvas.drawCircle(dotPos[i][j][0], dotPos[i][j][1], big_radium, paint);
            }
        }
    }

    //花小圆
    private void drawSmallCircle(Canvas canvas) {
        paint.setStrokeWidth(3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                paint.setColor(dotPos[i][j][2]);
                canvas.drawCircle(dotPos[i][j][0], dotPos[i][j][1], small_radium, paint);
            }
        }
    }


    //判断手指是否模糊触碰到小圆点,如果手指触碰到小圆点，将大圆的颜色设置为绿色
    private boolean isToucheDot(int touche_x, int touche_y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x_pos = dotPos[i][j][0];
                int y_pos = dotPos[i][j][1];
                if (get2PointDistance(x_pos, y_pos, touche_x, touche_y) < connect_distance) {
                    dotPos[i][j][2] = colors[1];
                    arrayList.add(x_pos);
                    arrayList.add(y_pos);

                    touchPot[0][0] = x_pos;
                    touchPot[0][1] = y_pos;

                    Log.i("iTouch", iTouch + "");

                    if (!(i == _x && j == _y)) {
                        userPath[iTouch][0] = i;
                        userPath[iTouch++][1] = j;
                    }
                    _x = i;
                    _y = j;


                    return true;
                }
            }
        }
        return false;
    }

    //手指抬起时将大圆的颜色全部设置为默认的灰色
    private void setBigCircleToGrey() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                dotPos[i][j][2] = colors[0];
            }
        }
    }

    //获得两点距离
    private int get2PointDistance(int x1, int y1, int x2, int y2) {
        int x_distance = Math.abs(x1 - x2);
        int y_distance = Math.abs(y1 - y2);
        return (int) Math.sqrt(Math.pow(x_distance, 2) + Math.pow(y_distance, 2));
    }

    //画线
    private void drawLine(Canvas canvas) {
        if (hashSet2FloatArray(arrayList) != null) {
            float[] dotLines = hashSet2FloatArray(arrayList);
            paint.setStrokeWidth(8);
            paint.setColor(colors[2]);
            canvas.drawLines(dotLines, paint);
        }
    }

    //转化ArrayList到Float数组
    public float[] hashSet2FloatArray(ArrayList<Integer> input_hashList) {
        if (input_hashList.size() == 0 || input_hashList.size() == 2) {
            return null;
        }
        int input_size = input_hashList.size();
        int output_size = (input_size - 4) * 2 + 4;
        float[] output_ints = new float[output_size];
        Iterator iterator = input_hashList.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            if (count == 0 || count == output_size - 2) {
                output_ints[count++] = (int) iterator.next();
                output_ints[count++] = (int) iterator.next();
            } else {
                int i = (int) iterator.next();
                int j = (int) iterator.next();
                output_ints[count++] = i;
                output_ints[count++] = j;
                output_ints[count++] = i;
                output_ints[count++] = j;
            }
        }
        return output_ints;
    }

    //画手指到点的线段
    private void drawFinger2Dot(Canvas canvas) {
        paint.setStrokeWidth(8);
        paint.setColor(colors[2]);
        if ((touchPot[0][0] + touchPot[0][1]) > 1f && (touchPot[1][0] + touchPot[1][1] > 1f)) {
            canvas.drawLine(touchPot[0][0], touchPot[0][1], touchPot[1][0], touchPot[1][1], paint);
        }
    }

    //清除手指到点的线段
    private void clearFinger2Dot() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                touchPot[i][j] = 0;
            }
        }
    }


    //判断用户已经滑动的轨迹和存储的轨迹是否一致
    private boolean checkPath() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (correctPath[i][j] != userPath[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void setListener(final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isUnlock) {
                        callBack.shutDown();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

}
