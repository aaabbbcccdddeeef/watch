package com.wisdomin.studentcard.feature.setting.sound;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeText extends AppCompatTextView {
  public MarqueeText(Context paramContext) { super(paramContext); }
  
  public MarqueeText(Context paramContext, AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  public MarqueeText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) { super(paramContext, paramAttributeSet, paramInt); }
  
  public boolean isFocused() { return true; }
}
