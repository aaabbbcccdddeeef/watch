package com.wisdomin.studentcard.feature.menu;

import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.viewpager.widget.ViewPager;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.adapter.MenuPageAdapter;
import com.wisdomin.studentcard.base.BaseActivity;

public class MenuIndexActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


  private ViewPager pagerMenu;

  private View viewIndicator0;

  private View viewIndicator1;

  float x1 = 0.0F;
  
  float x2 = 0.0F;
  
  float y1 = 0.0F;
  
  float y2 = 0.0F;


  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);
    initView();
  }

  private void initView() {
    pagerMenu =  findViewById(R.id.pager_menu);
    viewIndicator0 =  findViewById(R.id.view_indicator_0);
    viewIndicator1 =  findViewById(R.id.view_indicator_1);
    pagerMenu.setAdapter(new MenuPageAdapter(getSupportFragmentManager()));
    pagerMenu.setOffscreenPageLimit(3);
    pagerMenu.addOnPageChangeListener(this);
  }
  
  public boolean dispatchTouchEvent(MotionEvent motionEvent) {
    StringBuilder stringBuilder;
    if (this.pagerMenu.getCurrentItem() == 0) {
      if (motionEvent.getAction() == 0) {
        this.x1 = motionEvent.getX();
        this.y1 = motionEvent.getY();
        return super.dispatchTouchEvent(motionEvent);
      } 
      if (motionEvent.getAction() == 1) {
        this.x2 = motionEvent.getX();
        this.y2 = motionEvent.getY();
        int i = ViewConfiguration.get((Context)this).getScaledTouchSlop() * 10;
        if (this.x2 - this.x1 > i) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("touchSlop >> ");
          stringBuilder.append(i);
          finish();
          return false;
        } 
        return super.dispatchTouchEvent(motionEvent);
      } 
      return super.dispatchTouchEvent(motionEvent);
    } 
    return super.dispatchTouchEvent(motionEvent);
  }

  
  protected void onDestroy() {
    super.onDestroy();
    this.pagerMenu.removeOnPageChangeListener(this);
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
    if (paramInt == 74) {
      finish();
      return true;
    } 
    if (paramInt == 4) {
//      showHome();
      finish();
      return true;
    } 
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public void onPageScrollStateChanged(int paramInt) {}
  
  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
  
  public void onPageSelected(int paramInt) {
    if (paramInt == 0) {
      this.viewIndicator0.setBackgroundResource(R.drawable.shape_indicator);
      this.viewIndicator1.setBackgroundResource(R.drawable.shape_indicator_in);
      return;
    } 
    this.viewIndicator1.setBackgroundResource(R.drawable.shape_indicator);
    this.viewIndicator0.setBackgroundResource(R.drawable.shape_indicator_in);
  }
}
