package com.example.camera_ruler;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public OptionParam op = new OptionParam();

    public int getDrawAlpha() {
        return op.draw_alpha;
    }

    private boolean showRuler = true;          // show ruler

    public boolean isShowRuler() {
        return showRuler;
    }

    public void setShowRuler(boolean showRuler) {
        this.showRuler = showRuler;
    }

    private SideInfo sideLeft = new SideInfo(SideInfo.LEFT_SIDE);
    private SideInfo sideRight = new SideInfo(SideInfo.RIGHT_SIDE);

    public SideInfo getSideLeft() {
        return sideLeft;
    }

    public SideInfo getSideRight() {
        return sideRight;
    }

    private SurfaceHolder helloSurfaceHolder = null;

    /**
     * initialize SurfaceView
     */
    private void Initial() {
        helloSurfaceHolder = this.getHolder();
        helloSurfaceHolder.addCallback(this);
        helloSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

        sideLeft.setP0(new Point(100, 400));
        sideLeft.setP1(new Point(400, 120));
        sideLeft.setDistA(0.5);
        sideLeft.setDistB(0.8);


        sideRight.setP0(new Point(700, 400));
        sideRight.setP1(new Point(500, 120));
        sideRight.setDistA(0.5);
        sideRight.setDistB(0.8);


    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Initial();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initial();
    }

    public MySurfaceView(Context context) {
        super(context);
        Initial();
    }

    /**
     * clear SurfaceView
     *
     * @param canvas
     */
    private void MyClear(Canvas canvas) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        canvas.drawPaint(paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

        invalidate();
    }

    /**
     * draw ruler fill and color
     *
     * @param points
     * @param count
     * @param canvas
     * @param paint
     * @param color
     * @param alpha
     */
    private void MyDrawPath(Point[] points, int count, Canvas canvas, Paint paint, int color, int alpha) {
        if (count < 0)
            count = points.length;

        Path path = new Path();
        path.moveTo(points[0].x, points[0].y);
        for (int i = 1; i < count; ++i)
            path.lineTo(points[i].x, points[i].y);
        path.close();
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawPath(path, paint);

        // show location on every top for debug
//		paint.setColor(Color.BLUE);
//		paint.setAlpha(alpha);
//		char text = 'A';
//		for (int i=0; i<count;++i) {
//			canvas.drawText(String.valueOf(text), points[i].x, points[i].y, paint);
//			text ++;
//		}

    }


    /**
     * draw SurfaceView and display it
     *
     * @param canvas
     */
    public void MyDraw(Canvas canvas) {
        // ���
        MyClear(canvas);
        if (showRuler) {
            if (op.showBaseline) MyDrawBaseLine(canvas);
            MyDrawOneSide(canvas, sideLeft);
            MyDrawOneSide(canvas, sideRight);
        }

    }

    /**
     * show colored line
     *
     * @param canvas
     */
    private void MyDrawBaseLine(Canvas canvas) {
        int cnt = op.gridLineCnt;
        int w = this.getWidth();
        int h = this.getHeight();
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAlpha(op.draw_alpha);

        float offset = h / (cnt + 1);
        float y = offset;
        for (int i = 0; i < cnt; ++i) {
            canvas.drawLine(0, y, w, y, paint);
            y += offset;
        }

        offset = w / (cnt + 1);
        float x = offset;
        for (int i = 0; i < cnt; ++i) {
            canvas.drawLine(x, 0, x, h, paint);
            x += offset;
        }

    }

    /**
     * display textview beside colored line e.g. 0.5m 1.5m 3m without shadow
     *
     * @param canvas
     * @param text
     * @param point
     * @param point
     * @param factor LEFT_SIDE or RIGHT_SIDE
     */
    private void _MyDrawText(Canvas canvas, String text, int Color, Point point, int factor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(50);
        paint.setColor(Color);
        paint.setAlpha(op.draw_alpha);
        FontMetrics fm = paint.getFontMetrics();
        float fontHeight = fm.bottom - fm.top;
        float offY = fontHeight / 2 - 6;

        float newX = point.x;
        float newY = point.y;
        newY += offY - fm.bottom;

        if (factor == SideInfo.LEFT_SIDE) {
            newX -= paint.measureText(text) + 2 * op.lineWidth;
        } else {
            newX += 2 * op.lineWidth;
        }


        canvas.drawText(text, newX, newY, paint);


    }

    /**
     * display textview beside colored line e.g. 0.5m 1.5m 3m with shadow
     *
     * @param canvas
     * @param text
     * @param color
     * @param point
     * @param factor
     */
    private void MyDrawText(Canvas canvas, String text, int color, Point point, int factor) {
        Point p = new Point(point);

        if (op.showShadow) {
            // display shadow
            p.offset(op.shadowDeep, op.shadowDeep);
            _MyDrawText(canvas, text, Color.DKGRAY, p, factor);
        }

        _MyDrawText(canvas, text, color, point, factor);

    }

    /**
     * draw a whole ruler on SurfaceView
     *
     * @param canvas
     * @param //p0
     * @param //p1
     * @param //distA
     * @param //distB
     * @param //factor left: 1, right: -1
     */
    //private void MyDrawOneSide(Canvas canvas, Point p0, Point p1, double distA, double distB, int factor)
    private void MyDrawOneSide(Canvas canvas, SideInfo s) {

        Point[] pts = new Point[8];

        Point pG = new Point();
        Point pH = new Point();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setStyle(Style.STROKE);
        paint.setStyle(Style.FILL);

        Point p0 = s.getP0();
        Point p1 = s.getP1();
        double distA = s.getDistA();
        double distB = s.getDistB();
        int factor = s.getSidePos();

        double totalLen = Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2));
        double cosX = (double) (p1.x - p0.x) / totalLen;
        double sinX = (double) (p1.y - p0.y) / totalLen;

        // compute tan(x)
        //double ratio=(double)(p1.y-p0.y)/(p1.x-p0.x);
        int xOffset = (int) (op.lineWidth / 2.0 * cosX);
        int yOffset = (int) (op.lineWidth / 2.0 * sinX);

        //(1) every piont in 0.5m
        Point pA = new Point();
        pA.x = p0.x + factor * op.scaleWidth - xOffset;
        pA.y = p0.y - yOffset;

        Point pB = new Point();
        pB.x = p0.x + factor * op.scaleWidth + xOffset;
        pB.y = p0.y + yOffset;

        Point pC = new Point();
        pC.x = p0.x + xOffset + factor * op.lineWidth / 2;
        pC.y = p0.y + yOffset;

        Point pD = new Point();
        double len = totalLen * distA * 2 / 3.0 - op.gapDist; // long line segment(hypotenuse)
        Point pTemp = new Point();
        pTemp.x = (int) (p0.x + len * cosX);
        pTemp.y = (int) (p0.y + len * sinX);

        pD.x = pTemp.x + factor * op.lineWidth / 2;
        pD.y = pTemp.y;


        Point pE = new Point();
        pE.x = pTemp.x - factor * op.lineWidth / 2;
        pE.y = pTemp.y;

        Point pF = new Point();
        pF.x = p0.x - xOffset - factor * op.lineWidth / 2;
        pF.y = p0.y - yOffset;


        pts[0] = pA;
        pts[1] = pB;
        pts[2] = pC;
        pts[3] = pD;
        pts[4] = pE;
        pts[5] = pF;
        pts[6] = pG;
        pts[7] = pH;

        MyDrawPath(pts, 6, canvas, paint, Color.RED, op.draw_alpha);
        MyDrawText(canvas, "0.0m", Color.RED, p0, factor);


//		int temp = pA.x - pF.x;
//		Log.i("backcamera",	 String.format("%x", temp));


        /////////////////////////////////////////////////
        //draw a second line segment by 0.5m
        /////////////////////////////////////////////////
        len = len + op.gapDist;
        pTemp.x = (int) (p0.x + len * cosX);
        pTemp.y = (int) (p0.y + len * sinX);

        pA.x = pTemp.x + factor * op.lineWidth / 2;
        pA.y = pTemp.y;

        Point pTemp2 = new Point();
        pTemp2.x = (int) (p0.x + totalLen * distA * cosX);
        pTemp2.y = (int) (p0.y + totalLen * distA * sinX);

        pB.x = pTemp2.x + factor * op.lineWidth / 2 - xOffset;
        pB.y = pTemp2.y - yOffset;

        pC.x = pTemp2.x + factor * op.scaleWidth - xOffset;
        pC.y = pTemp2.y - yOffset;

        pD.x = pTemp2.x + factor * op.scaleWidth + xOffset;
        pD.y = pTemp2.y + yOffset;

        pE.x = pTemp2.x - factor * op.lineWidth / 2 + xOffset;
        pE.y = pTemp2.y + yOffset;

        pF.x = pTemp.x - factor * op.lineWidth / 2;
        pF.y = pTemp.y;

        MyDrawPath(pts, 6, canvas, paint, Color.RED, op.draw_alpha);
        // draw text 0.5m
        MyDrawText(canvas, "0.5m", Color.RED, pTemp2, factor);
        s.setP05(pTemp2);


//		temp = pD.x - pE.x;
//		Log.i("backcamera",	 String.format("%x", temp));


        //(3)  first part of 1.5m (after 0.5m's part)
        pA.x = pE.x + factor * op.lineWidth;
        pA.y = pD.y;

        len = totalLen * distB - totalLen * (distB - distA) * 1 / 3.0 - op.gapDist;
        pB.x = (int) (p0.x + len * cosX + factor * op.lineWidth / 2);
        pB.y = (int) (p0.y + len * sinX);

        pC.x = pB.x - factor * op.lineWidth;
        pC.y = pB.y;


        pD.x = pE.x;
        pD.y = pE.y;

        MyDrawPath(pts, 4, canvas, paint, Color.YELLOW, op.draw_alpha);

        // (4) draw 1.5m' part
        len += op.gapDist;

        pTemp.x = (int) (p0.x + len * cosX);
        pTemp.y = (int) (p0.y + len * sinX);

        pA.x = pTemp.x + factor * op.lineWidth / 2;
        pA.y = pTemp.y;

        int len2 = (int) (totalLen * distB);
        pTemp2.x = (int) (p0.x + len2 * cosX);
        pTemp2.y = (int) (p0.y + len2 * sinX);

        pB.x = pTemp2.x + factor * op.lineWidth / 2 - xOffset;
        pB.y = pTemp2.y - yOffset;

        pC.x = pTemp2.x + factor * op.scaleWidth - xOffset;
        pC.y = pTemp2.y - yOffset;

        pD.x = pTemp2.x + factor * op.scaleWidth + xOffset;
        pD.y = pTemp2.y + yOffset;

        pE.x = pTemp2.x + factor * op.lineWidth / 2 + xOffset;
        pE.y = pD.y;

        int len3 = (int) (totalLen * distB + (totalLen - totalLen * distB) * 2 / 3.0) - op.gapDist;
        pF.x = (int) (p0.x + len3 * cosX) + factor * op.lineWidth / 2;
        pF.y = (int) (p0.y + len3 * sinX);

        pG.x = pF.x - factor * op.lineWidth;
        pG.y = pF.y;

        pH.x = pA.x - factor * op.lineWidth;
        pH.y = pA.y;


        MyDrawPath(pts, 8, canvas, paint, Color.YELLOW, op.draw_alpha);

        MyDrawText(canvas, "1.5m", Color.YELLOW, pTemp2, factor);
        s.setP15(pTemp2);

        // (5) top part

        len3 += op.gapDist;
        pTemp.x = (int) (p0.x + len3 * cosX);
        pTemp.y = (int) (p0.y + len3 * sinX);


        pA.x = pTemp.x + factor * op.lineWidth / 2;
        pA.y = pTemp.y;

        pTemp2.x = p1.x; //(int)(p0.x+totalLen*cosX); // p1.x
        pTemp2.y = p1.y; //(int)(p0.y+totalLen*sinX); // p1.y

        pB.x = pTemp2.x + factor * op.lineWidth / 2 - xOffset;
        pB.y = pTemp2.y - yOffset;

        pC.x = pTemp2.x + factor * op.scaleWidth - xOffset;
        pC.y = pB.y;

        pD.x = pTemp2.x + factor * op.scaleWidth + xOffset;
        pD.y = pTemp2.y + yOffset;

        pE.x = pTemp2.x - factor * op.lineWidth / 2 + xOffset;
        pE.y = pD.y;

        pF.x = pTemp.x - factor * op.lineWidth / 2;
        pF.y = pA.y;

        MyDrawPath(pts, 6, canvas, paint, Color.YELLOW, op.draw_alpha);
        MyDrawText(canvas, "3.0m", Color.YELLOW, pTemp2, factor);

		/*
        canvas.drawText("0", s.getP0().x, s.getP0().y, paint);
		canvas.drawText("1", s.getP05().x, s.getP05().y, paint);
		canvas.drawText("2", s.getP15().x, s.getP15().y, paint);
		canvas.drawText("3", s.getP1().x, s.getP1().y, paint);
		*/

    }

    /**
     * draw ruler function for call outside (thread safety)
     */
    public void draw() {
        synchronized (helloSurfaceHolder) {
            Canvas canvas = null;
            if (null != (canvas = helloSurfaceHolder.lockCanvas())) {

                MyDraw(canvas);

                helloSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void clear() {
        synchronized (helloSurfaceHolder) {
            Canvas canvas = null;
            if (null != (canvas = helloSurfaceHolder.lockCanvas())) {
                Paint paint = new Paint();
                paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                canvas.drawPaint(paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

                invalidate();

                helloSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }


}
