package com.ctop.studentcard.feature.setting.wallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ctop.studentcard.R;
import com.ctop.studentcard.api.OnReceiveListener;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.feature.setting.SettingActivity;
import com.ctop.studentcard.feature.setting.about.AboutActivity;
import com.ctop.studentcard.feature.setting.sound.SoundActivity;
import com.ctop.studentcard.util.PreferencesUtils;

public class WallpaperActivity extends BaseActivity {
    public static int[] WALL_PAPER_ITEM_ICON = new int[]{R.drawable.wallpaper01, R.drawable.wallpaper02};

    private Context context;

    private ViewPager viewPager;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.wallpaper_layout);
        this.context = this;
        this.viewPager = findViewById(R.id.vp_wall);
        ImageAdapter imageAdapter = new ImageAdapter();
        viewPager.setAdapter(imageAdapter);


    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        try {
            this.viewPager.setCurrentItem(0);
            PreferencesUtils.getInstance(WallpaperActivity.this).setInt("wall_selected_index", 0);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    public class ImageAdapter extends PagerAdapter {
        public int getCount() {
            return WallpaperActivity.this.WALL_PAPER_ITEM_ICON.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            ImageView imageView = new ImageView(WallpaperActivity.this.context);
            imageView.setImageResource(WallpaperActivity.this.WALL_PAPER_ITEM_ICON[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperActivity.this.context);
                    try {
                        PreferencesUtils.getInstance(WallpaperActivity.this).setInt("wall_selected_index", position);
                        wallpaperManager.setResource(WallpaperActivity.this.WALL_PAPER_ITEM_ICON[position]);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    WallpaperActivity.this.finish();
                }
            });
            container.addView(imageView);
            return imageView;
        }
    }
}
