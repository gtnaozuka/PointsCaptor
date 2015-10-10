package com.captor.points.gtnaozuka.pointscaptor;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;

import com.captor.points.gtnaozuka.adapter.FullScreenPhotoAdapter;
import com.captor.points.gtnaozuka.util.image.ImageCache;
import com.captor.points.gtnaozuka.util.image.ImageResizer;
import com.captor.points.gtnaozuka.util.Versions;
import com.captor.points.gtnaozuka.util.Constants;
import com.captor.points.gtnaozuka.util.operations.FileOperations;

public class FullScreenViewActivity extends FragmentActivity implements View.OnClickListener {

    private ImageResizer imageResizer;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, FileOperations.IMAGES_CACHE_FOLDER);
        cacheParams.setMemCacheSizePercent(0.25f);

        imageResizer = new ImageResizer(this, longest);
        imageResizer.addImageCache(getSupportFragmentManager(), cacheParams);
        imageResizer.setImageFadeIn(false);

        String[] photos = getIntent().getStringArrayExtra(Constants.PHOTOS_MSG);
        FullScreenPhotoAdapter adapter = new FullScreenPhotoAdapter(getSupportFragmentManager(), photos);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        pager.setOffscreenPageLimit(2);

        if (Versions.hasHoneycomb()) {
            pager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

        final int extraCurrentItem = getIntent().getIntExtra(Constants.POSITION_MSG, -1);
        if (extraCurrentItem != -1) {
            pager.setCurrentItem(extraCurrentItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        imageResizer.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageResizer.setExitTasksEarly(true);
        imageResizer.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageResizer.closeCache();
    }

    public ImageResizer getImageResizer() {
        return imageResizer;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {
        final int vis = pager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            pager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            pager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
