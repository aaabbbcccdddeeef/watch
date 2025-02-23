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
import com.wisdomin.studentcard.feature.setting.SettingActivity;
import com.wisdomin.studentcard.feature.temperature.TemActivity;

public class MenuItem2Fragment extends Fragment {


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
                Intent intent = new Intent((Context) MenuItem2Fragment.this.getActivity(), SettingActivity.class);
                MenuItem2Fragment.this.startActivity(intent);

            }
        });
        this.layout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                Intent intent = new Intent( MenuItem2Fragment.this.getActivity(), TemActivity.class);
                MenuItem2Fragment.this.startActivity(intent);
            }
        });
        this.layout3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MenuItem2Fragment.this.startActivity(new Intent(MenuItem2Fragment.this.getActivity(), SettingActivity.class));
            }
        });
        this.layout4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MenuItem2Fragment menuItem2Fragment = MenuItem2Fragment.this;
                menuItem2Fragment.startActivity(new Intent((Context) menuItem2Fragment.getActivity(), SettingActivity.class));
            }
        });
    }

    public static MenuItem2Fragment newInstance() {
        Bundle bundle = new Bundle();
        MenuItem2Fragment menuItem2Fragment = new MenuItem2Fragment();
        menuItem2Fragment.setArguments(bundle);
        return menuItem2Fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        View view = layoutInflater.inflate(R.layout.fragment_menu_2, paramViewGroup, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle paramBundle) {
        super.onViewCreated(view, paramBundle);
        initView(view);
    }
}
