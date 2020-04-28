package com.ctop.studentcard.adapter;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ctop.studentcard.feature.menu.item.MenuItem1Fragment;
import com.ctop.studentcard.feature.menu.item.MenuItem2Fragment;

public class MenuPageAdapter extends FragmentPagerAdapter {

    public MenuPageAdapter(FragmentManager paramFragmentManager) {
        super(paramFragmentManager);
    }

    public int getCount() {
        return 2;
    }

    public Fragment getItem(int paramInt) {
        switch (paramInt) {
            default:
                return (Fragment) MenuItem1Fragment.newInstance();
            case 2:
                return (Fragment) MenuItem1Fragment.newInstance();
            case 1:
                return (Fragment) MenuItem2Fragment.newInstance();
            case 0:
                break;
        }
        return (Fragment) MenuItem1Fragment.newInstance();
    }
}
