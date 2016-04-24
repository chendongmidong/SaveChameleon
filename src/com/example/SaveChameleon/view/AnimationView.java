package com.example.SaveChameleon.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import com.example.SaveChameleon.Animal;
import com.example.SaveChameleon.R;
import com.example.SaveChameleon.ShapeHolder;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by zhao on 2016/4/16.
 */
public class AnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    AnimatorSet animation = new AnimatorSet();
    Bitmap snakeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snake);
    Bitmap frogBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.frog);
    int bitmap_w,bitmap_h;
    Matrix m = new Matrix();

    Animal snake;
    Animal frog;

    float preX,preY;

    ObjectAnimator animatorX,animatorY;

    float moveDirection = 100;

    int preColor = 0xFFFFFFFF;

    Paint vPaint = new Paint();
    public int alph = 200;

    public AnimationView(Context context,float x) {
        super(context);
        initView(x);
    }
    public void initView(float x){
        snake = Animal.createAnimal(400,400,x-400,50,snakeBitmap);
        frog = Animal.createAnimal(200,200,50,700,frogBitmap);
        bitmap_w = frog.bitmap.getWidth();
        bitmap_h = frog.bitmap.getHeight();
        preX = snake.getX();
        preY = snake.getY();
    }


    private void moveSnake() {
        animatorX = ObjectAnimator.ofFloat(snake,"x",preX,preX-moveDirection).setDuration(1000);
        preX -= moveDirection;
        animatorY = ObjectAnimator.ofFloat(snake,"y",preY,preY+moveDirection).setDuration(1000);
        preY += moveDirection;
        animatorX.addUpdateListener(this);
        animatorY.addUpdateListener(this);
        animation.playTogether(animatorX,animatorY);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(snake.getX(), snake.getY());
        canvas.drawBitmap(snake.bitmap,m,null);
        canvas.restore();

        canvas.save();
        canvas.translate(frog.getX(), frog.getY());
        canvas.drawBitmap(frog.bitmap,m,vPaint);
        canvas.restore();
    }

    public void startAnimation() {
        moveSnake();
        animation.start();
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    public void changFrogColor(int color){
        alph = 200;
        vPaint.setAlpha(alph);
        for(int i=0;i<bitmap_h;i++){
            for(int j=0;j<bitmap_w;j++){
                if (frog.bitmap.getPixel(j,i) != 0)
                    frog.bitmap.setPixel(j,i,color);
            }
        }
        invalidate();
    }

    public void decreaseAlpha(){
        vPaint.setAlpha(alph--);
        invalidate();
    }
}

