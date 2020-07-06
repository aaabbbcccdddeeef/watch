package com.wisdomin.studentcard.feature.menu.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.feature.calculator.CalculatorActivity;
import com.wisdomin.studentcard.feature.message.MessageIndexActivity;
import com.wisdomin.studentcard.feature.phone.record.CallRecordActivity;
import com.wisdomin.studentcard.util.UIUtil;

public class MenuItem1Fragment extends Fragment {

    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;

    private void initView(View view) {
        layout1 = view.findViewById(R.id.layout_1);
        layout2 = view.findViewById(R.id.layout_2);
        layout3 = view.findViewById(R.id.layout_3);
        layout4 = view.findViewById(R.id.layout_4);
        this.layout1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent((Context) MenuItem1Fragment.this.getActivity(), MessageIndexActivity.class);
                MenuItem1Fragment.this.startActivity(intent);
            }
        });
        this.layout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent((Context) MenuItem1Fragment.this.getActivity(), CallRecordActivity.class);
                MenuItem1Fragment.this.startActivity(intent);
            }
        });
        this.layout3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent((Context) MenuItem1Fragment.this.getActivity(), CalculatorActivity.class);
                MenuItem1Fragment.this.startActivity(intent);
            }
        });
        this.layout4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                UIUtil.startAPP(MenuItem1Fragment.this.getActivity(), "cn.xxt.dailyappraise", "cn.xxt.dailyappraise.MainActivity");
            }
        });
    }

    public static MenuItem1Fragment newInstance() {
        Bundle bundle = new Bundle();
        MenuItem1Fragment menuItem1Fragment = new MenuItem1Fragment();
        menuItem1Fragment.setArguments(bundle);
        return menuItem1Fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        View view = layoutInflater.inflate(R.layout.fragment_menu_1, paramViewGroup, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
}
