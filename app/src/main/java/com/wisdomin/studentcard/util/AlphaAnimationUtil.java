package com.wisdomin.studentcard.util;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/*闪烁效果*/
public class AlphaAnimationUtil {

    /**
     * 开启View闪烁效果
     * */
    public static void startFlick(View view ){

        if( null == view ){
            return;
        }

        Animation alphaAnimation = new AlphaAnimation( 1.0f, 0.5f );
        alphaAnimation.setDuration( 300 );
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setInterpolator( new LinearInterpolator( ) );
        alphaAnimation.setRepeatCount( Animation.INFINITE );
        alphaAnimation.setRepeatMode( Animation.REVERSE );
        view.startAnimation( alphaAnimation );
    }


    /**
     * 淡出
     * */
    public static void startAlphaOut(View view ){

        if( null == view ){
            return;
        }
        //属性动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(1000);
        animator.start();
    }

    /**
     * 淡ru
     * */
    public static void startAlphaIn(View view ){

        if( null == view ){
            return;
        }
        //属性动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(1000);
        animator.start();
    }


    /**
     * 平移
     * 3:告诉系统TextView应该怎么移动
     * */
    public static void translationXIn(View view ){

        if( null == view ){
            return;
        }
        float curTranslationX = view.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",240f, 0f);
        animator.setDuration(3000);
        animator.setInterpolator(new DecelerateInterpolator(5));//加速查值器，参数越大，速度越来越慢
        animator.start();
    }


}