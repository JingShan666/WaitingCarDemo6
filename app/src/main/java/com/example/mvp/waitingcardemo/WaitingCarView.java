package com.example.mvp.waitingcardemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by Liu on 2017/10/14 0014.
 * 一个模仿滴滴打车等车的自定义view
 *
 * 1,当然还是先写三个构造方法
 * 2，画最外层灰色的圆
 */

public class WaitingCarView extends View{

    private int mWidth;//宽

    private int mHeight;//高

    private String unit = "辆";

    private String notification = "已通知出租车";

    private int sweepAngle = 0;//圆弧扫过的角度
    private RectF rectF;

    private float[] pos;     // 当前点在画布上的坐标值，有两个值，分别为x,y坐标
    private float[] tan;    // 当前点的正切值,用于计算图片所需旋转的角度

    /**
     画最外层的圆画笔
     */
    private Paint mOuterCirclePaint;
    /**
     画文字的画笔
     */
    private Paint mTextPaint;
    /**
     画车数量的画笔
     */
    private Paint mCarCountPaint;
    /**
     画车单位的画笔
     */
    private Paint mCarUnitPaint;

    /**
     画圆弧的画笔
     */
    private Paint mDrawArcPaint;

    //圆弧设置的颜色渐变
    int colors[] = {Color.parseColor("#F1C200"),Color.parseColor("#FF9244")};

    private Bitmap mBitmap;             // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作




    public WaitingCarView(Context context) {
        this(context,null);
    }

    public WaitingCarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaitingCarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //创建最外层灰色圆画笔
        mOuterCirclePaint= new Paint();
        mOuterCirclePaint.setStrokeWidth(5);//设置画笔宽度
        mOuterCirclePaint.setColor(Color.parseColor("#E3E4E7"));//设置画笔颜色
        mOuterCirclePaint.setAntiAlias(true);//抗锯齿
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);//设置风格，描边，不填充


        //创建文字画笔
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setColor(Color.parseColor("#8B8C8F"));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(40);
        mTextPaint.setStyle(Paint.Style.FILL);

        //创建车数量画笔
        mCarCountPaint = new Paint();
        mCarCountPaint.setStrokeWidth(1);
        mCarCountPaint.setColor(Color.parseColor("#EC9B70"));
        mCarCountPaint.setAntiAlias(true);
        mCarCountPaint.setTextSize(35);
        mCarCountPaint.setStyle(Paint.Style.FILL);

        //创建车单位画笔
        mCarUnitPaint = new Paint();
        mCarUnitPaint.setStrokeWidth(1);
        mCarUnitPaint.setColor(Color.parseColor("#EC9B70"));
        mCarUnitPaint.setAntiAlias(true);
        mCarUnitPaint.setTextSize(30);
        mCarUnitPaint.setStyle(Paint.Style.FILL);

        //绘制圆弧的画笔
        mDrawArcPaint = new Paint();
        mDrawArcPaint.setStrokeWidth(5);
        mDrawArcPaint.setAntiAlias(true);
        mDrawArcPaint.setStyle(Paint.Style.STROKE);
        SweepGradient sweepGradient = new SweepGradient(0,0,colors,null);
//        Matrix matrix = new Matrix();
//        matrix.setRotate(-180,0,0);
//        sweepGradient.setLocalMatrix(matrix);
        mDrawArcPaint.setShader(sweepGradient);



        pos= new float[2];
        tan= new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();//通过bitmapFactory获取图片资源

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.point,options);//获取资源

        mMatrix = new Matrix();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth= MeasureSpec.getSize(widthMeasureSpec);
        mHeight= MeasureSpec.getSize(heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth/2,mHeight/2);//将画布移动到屏幕中心位置


        /**
         * 通过path绘制一个圆
         * 参数：圆心纵横坐标，半径，方向
         * 在此处采用这种方式来绘制圆，因为后面绘制圆形图片的时候要用到圆上每一个点的坐标
         *
         */

        Path path= new Path();
        path.addCircle(0,0,(mWidth-100)/2,Path.Direction.CW);//cw是顺时针
        canvas.drawPath(path,mOuterCirclePaint);

        PathMeasure measure= new PathMeasure(path,false);

        BigDecimal bigDecimal1 = BigDecimal.valueOf(sweepAngle);
        BigDecimal b2 = BigDecimal.valueOf(360);
        float rad = bigDecimal1.divide(b2, MathContext.DECIMAL32).floatValue();//通过bigdecimal来获取黄线在灰色圆上的位置，在放黄色的小球
        measure.getPosTan(measure.getLength() * rad , pos, tan);// 获取当前位置的坐标以及趋势

        //measure.getPosTan(measure.getLength() , pos, tan);// 获取当前位置的坐标以及趋势
        mMatrix.reset();
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI); // 计算图片旋转角度
        //mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);   // 旋转图片

        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合


        /**
         * 直接绘制一个圆
         * 参数：圆心纵横坐标，半径，画笔
         *
         * 这两种方式在效果上是一样的
         */
        //canvas.drawCircle(0,0,(mWidth-100)/2,mOuterCirclePaint);



        //绘制文字
        Rect rect = new Rect();
        mTextPaint.getTextBounds(notification,0,notification.length(),rect);
        canvas.drawText(notification,-(rect.left+rect.right)/2,rect.bottom,mTextPaint);//写通知的字

        //绘制数量
        Rect rectCount = new Rect();
        mCarCountPaint.getTextBounds(String.valueOf(sweepAngle),0,String.valueOf(sweepAngle).length(),rectCount);
        canvas.drawText(String.valueOf(sweepAngle),-(rect.left+rect.right)/2+40,rect.bottom-rect.top-rect.bottom-rectCount.top/*-rectCount.bottom*/,mCarCountPaint);//写车的数量

        //绘制单位
        canvas.drawText(unit,(rect.left-rect.right)/2+140,rect.bottom-rect.top-rect.bottom-rectCount.top/*-rectCount.bottom*/,mCarUnitPaint);//写车的单位

        //绘制圆弧
        rectF = new RectF(-mWidth/2+50,-mWidth/2+50,mWidth/2-50,mWidth/2-50);
        canvas.drawArc(rectF,-90,sweepAngle,false, mDrawArcPaint);




        //绘制圆形图片
        mMatrix.postRotate(-90);
        canvas.drawBitmap(mBitmap, mMatrix, mDrawArcPaint);


    }




    public void setData(int sweepAngle){
        this.sweepAngle = sweepAngle;
        invalidate();
    }
}
