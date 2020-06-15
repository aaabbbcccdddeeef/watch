package com.ctop.studentcard.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
 
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by DengDongQi on 2019/9/3
 * 解决ImageView  setRotationX....等旋转时出现锯齿问题
 *
 * 原理 : 某大佬研究出锯齿规律会出现为ImageView的边界 1-2 px处,解决办法是:
 * 重写onDraw且不走super方法,自己缩小ImageView内容1-2 px并居中绘制
 *
 * 出处: (FQ)https://medium.com/@elye.project/smoothen-jagged-edges-of-rotated-image-view-1e56f6d8b5e9
 */
public class RotationImageView extends AppCompatImageView {
 
    public RotationImageView(Context context) {
        super(context);
    }
 
    public RotationImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
 
    public RotationImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
 
    @Override
    protected void onDraw(Canvas canvas) {

        BitmapFactory.Options options=new BitmapFactory.Options();

        if (getDrawable() instanceof BitmapDrawable) {
            //super.onDraw(canvas); 不走super
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            BitmapShader shader = new BitmapShader(bitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
 
            Matrix matrix = new Matrix();
            float scale;
            /* Note: this piece of code handling like Centre-Crop scaling */
            if (bitmap.getWidth() > bitmap.getHeight()) {
                scale = (float) canvas.getHeight() / (float) bitmap.getHeight();
                matrix.setScale(scale, scale);
                matrix.postTranslate((canvas.getWidth() - bitmap.getWidth() * scale) * 0.5f, 0);
 
            } else {
                scale = (float) canvas.getWidth() / (float) bitmap.getWidth();
                matrix.setScale(scale, scale);
                matrix.postTranslate(0, (canvas.getHeight() - bitmap.getHeight() * scale) * 0.5f);
            }
            shader.setLocalMatrix(matrix);
 
            paint.setShader(shader);
            /* this is where I shrink the image by 1px each side,
                move it to the center */
            canvas.translate(0, 0);
            canvas.drawRect(0.0f, 0.0f, canvas.getWidth() - 1, canvas.getHeight() - 1, paint);
 
        }
    }
}