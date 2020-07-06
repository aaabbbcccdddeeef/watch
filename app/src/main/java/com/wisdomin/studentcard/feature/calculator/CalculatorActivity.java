package com.wisdomin.studentcard.feature.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.base.BaseActivity;

import java.text.DecimalFormat;

public class CalculatorActivity extends BaseActivity {

    private static final String POINT = ".";

    public static final String THREE_ZERO = ".0000";

    private static final String ZERO = "0";

    private StringBuilder mBuilderOne = new StringBuilder();

    private StringBuilder mBuilderSecond = new StringBuilder();

    private MutableLiveData<String> mOperate = new MutableLiveData();

    private TextView tv_result;
    private ImageView back_top;

    private void appendNumber(String paramString) {
        if (TextUtils.isEmpty((String) this.getOperate().getValue())) {
            this.appenPrevious(paramString);
            return;
        }
        this.appendLater(paramString);
    }

    private void initView() {
        tv_result = (TextView) findViewById(R.id.tv_result);

        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void append0(View paramView) {
        appendNumber("0");
    }

    public void append1(View paramView) {
        appendNumber("1");
    }

    public void append2(View paramView) {
        appendNumber("2");
    }

    public void append3(View paramView) {
        appendNumber("3");
    }

    public void append4(View paramView) {
        appendNumber("4");
    }

    public void append5(View paramView) { appendNumber("5"); }

    public void append6(View paramView) {
        appendNumber("6");
    }

    public void append7(View paramView) {
        appendNumber("7");
    }

    public void append8(View paramView) {
        appendNumber("8");
    }

    public void append9(View paramView) {
        appendNumber("9");
    }

    public void appendPoint(View paramView) {
        appendNumber(".");
    }

    public void calculatorResult(View paramView) {
        this.calculatorResult();
    }

    public void clearAll(View paramView) {
        this.clearAll();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        initView();
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 74) {
            finish();
            return true;
        }
        if (paramInt == 4) {
//            showHome();
            finish();
            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public void operateDivide(View paramView) {
        this.setUpOperate("÷");
    }

    public void operateMinus(View paramView) {
        this.setUpOperate("-");
    }

    public void operateMultiply(View paramView) {
        this.setUpOperate("X");
    }

    public void operatePlus(View paramView) {
        this.setUpOperate("+");
    }


    private boolean checkNumber(String paramString1, String paramString2) {
        if (".".equals(paramString2))
            return (Double.parseDouble(paramString1) <= 9.999999999E9D);
        if (paramString1.contains(".")) {
            if (paramString1.substring(paramString1.lastIndexOf(".")).length() >= 5)
                return false;
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(paramString1);
            stringBuilder1.append(paramString2);
            return (Double.parseDouble(stringBuilder1.toString()) <= 9.999999999E9D);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paramString1);
        stringBuilder.append(paramString2);
        return (Double.parseDouble(stringBuilder.toString()) <= 9.999999999E9D);
    }

    private void display() {
        String str1 = this.mBuilderOne.toString();
        String str2 = this.mBuilderSecond.toString();
        String str3 = (String) this.mOperate.getValue();
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(str1))
            stringBuilder.append(str1);
        if (!TextUtils.isEmpty(str3))
            stringBuilder.append(str3);
        if (!TextUtils.isEmpty(str2))
            stringBuilder.append(str2);
        this.tv_result.setText(stringBuilder.toString());
    }

    public void appenPrevious(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = this.mBuilderOne.toString();
        if (".".equals(paramString)) {//输入是点
            if (str.contains("."))
                return;
            if (TextUtils.isEmpty(str)) {//之前是空
                stringBuilder = this.mBuilderOne;
                stringBuilder.append("0");
                stringBuilder.append(".");
                display();
                return;
            }
        } else if ("0".equals(paramString) && TextUtils.isEmpty(str)) {//输入是0 并且 之前是空
            return;
        }
        if (!checkNumber(this.mBuilderOne.toString(), paramString))
            return;
        this.mBuilderOne.append(paramString);
        display();
    }

    public void appendLater(String paramString) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = this.mBuilderSecond.toString();
        if (".".equals(paramString)) {
            if (str.contains("."))
                return;
            if (TextUtils.isEmpty(str)) {
                stringBuilder = this.mBuilderSecond;
                stringBuilder.append("0");
                stringBuilder.append(".");
                display();
                return;
            }
        } else if ("0".equals(paramString) && TextUtils.isEmpty(str)) {
            return;
        }
        if (!checkNumber(this.mBuilderSecond.toString(), paramString))
            return;
        this.mBuilderSecond.append(paramString);
        display();
    }

    public void calculatorResult() {
        String str = this.mOperate.getValue();
        if (str != null && !TextUtils.isEmpty(str)) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
            String str2 = this.mBuilderOne.toString();
            String str3 = this.mBuilderSecond.toString();
            String str1 = "";
            if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                byte b = -1;
                int i = str.hashCode();
                if (i != 43) {
                    if (i != 45) {
                        if (i != 88) {
                            if (i == 247 && str.equals(("÷")))
                                b = 3;
                        } else if (str.equals("X")) {
                            b = 2;
                        }
                    } else if (str.equals("-")) {
                        b = 1;
                    }
                } else if (str.equals("+")) {
                    b = 0;
                }
                switch (b) {
                    case 3:
                        if ("0".equals(str3))
                            return;
                        str1 = decimalFormat.format(Double.parseDouble(str2) / Double.parseDouble(str3));
                        break;
                    case 2:
                        str1 = decimalFormat.format(Double.parseDouble(str2) * Double.parseDouble(str3));
                        break;
                    case 1:
                        str1 = decimalFormat.format(Double.parseDouble(str2) - Double.parseDouble(str3));
                        break;
                    case 0:
                        str1 = decimalFormat.format(Double.parseDouble(str2) + Double.parseDouble(str3));
                        break;
                }
                if (!TextUtils.isEmpty(str1)) {
                    str = str1;
                    if (str1.endsWith(".0000"))
                        str = str1.replace(".0000", "");
                    clearAll();
                    this.tv_result.setText(str);
                }
            }
        }
    }

    public void clearAll() {
        this.tv_result.setText("");
        this.mOperate.setValue("");
        StringBuilder stringBuilder = this.mBuilderOne;
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder = this.mBuilderSecond;
        stringBuilder.delete(0, stringBuilder.length());
    }

    public MutableLiveData<String> getOperate() {
        return this.mOperate;
    }


    public void setUpOperate(String paramString) {
        if (TextUtils.isEmpty(this.mBuilderOne.toString()))
            this.mBuilderOne.append("0");
        this.mOperate.setValue(paramString);
        display();
    }
}
