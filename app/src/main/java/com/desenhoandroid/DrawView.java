package com.desenhoandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by guilherme on 23/12/14.
 */
public class DrawView extends View {

    public static final int TOOL_BRUSH = 0; //Ferramenta de pincel
    public static final int TOOL_LINE = 1;  //Ferramenta de Linha

    private float brushSize; //Tamanho do pincel/linha
    private float lastBrushSize; //Ultimo Tamanho do pincel/linha

    private Path drawPath; //classe que define pontos e caminhos

    private Paint drawPaint,canvasPaint; //Classe que define as configurações do desenho

    private int paintColor = 0xFF660000; //Cor do desenho

    private Canvas drawCanvas;

    private Bitmap canvasBitmap;

    private int tool = TOOL_LINE; //ferramenta que estamos utilizando

    private boolean erase=false; //Está apagando

    private float touchXDown = 0f;
    private float touchYDown = 0f;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    private void init(){
        drawPath = new Path();
        drawPaint = new Paint();
        canvasPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(drawPath, drawPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    private boolean line(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchXDown = event.getX();
                touchYDown = event.getY();
                drawPath.reset();
                drawPath.moveTo(touchXDown, touchYDown);
                //drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.reset();
                drawPath.moveTo(touchXDown, touchYDown);
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                touchXDown = 0f;
                touchYDown = 0f;
                drawCanvas.drawPath(drawPath, drawPaint);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    private boolean brush(MotionEvent event){
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX+0.1f, touchY+0.1f);
                drawCanvas.drawPath(drawPath, drawPaint);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (tool == TOOL_BRUSH)
            return brush(event);
        else
            return line(event);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public int getTool() {
        return tool;
    }

    public void setTool(int tool) {
        this.tool = tool;
    }
}
