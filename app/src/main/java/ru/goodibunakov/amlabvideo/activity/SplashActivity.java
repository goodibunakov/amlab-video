package ru.goodibunakov.amlabvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import ru.goodibunakov.amlabvideo.R;

public class SplashActivity extends AppCompatActivity implements interfaces.AsyncTaskListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Loading(this).execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    public void taskFinished() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public static class Loading extends AsyncTask<Void, Void, Void> {

        private interfaces.AsyncTaskListener listener;

        Loading(Context context) {
            listener = (interfaces.AsyncTaskListener) context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.taskFinished();
        }
    }
}
