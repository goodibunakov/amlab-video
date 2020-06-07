package ru.goodibunakov.amlabvideo.presentation.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.WindowManager;

/**
 * Class responsible for changing the view from full screen to non-full screen and vice versa.
 *
 * @author Pierfrancesco Soffritti
 */
public class FullScreenHelper {

    private Activity context;
    private View[] views;

    /**
     * @param context
     * @param views   to hide/show
     */
    public FullScreenHelper(Activity context, View... views) {
        this.context = context;
        this.views = views;
    }

    /**
     * call this method to enter full screen
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void enterFullScreen() {
        hideSystemUi();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        for (View view : views) {
            view.setVisibility(View.GONE);
            view.invalidate();
        }
    }

    /**
     * call this method to exit full screen
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void exitFullScreen() {
        showSystemUi();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
            view.invalidate();
        }
    }

    private void hideSystemUi() {
        context.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void showSystemUi() {
        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}