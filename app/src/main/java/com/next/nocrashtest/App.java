package com.next.nocrashtest;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.next.nocrashtest.applib.Cockroach;
import com.next.nocrashtest.applib.ExceptionHandler;
import com.next.nocrashtest.support.CrashLog;
import com.next.nocrashtest.support.DebugSafeModeTipActivity;
import com.next.nocrashtest.support.DebugSafeModeUI;

/**
 * Created by wanjian on 2018/5/19.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        install();
    }


    private void install() {
        final Thread.UncaughtExceptionHandler sysExceptHandler = Thread.getDefaultUncaughtExceptionHandler();
        final Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        DebugSafeModeUI.init(this);
        Cockroach.install(this, new ExceptionHandler() {
            @Override
            protected void onUncaughtExceptionHappened(Thread thread, Throwable throwable) {
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", throwable);
                CrashLog.saveCrashLog(getApplicationContext(), throwable);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        toast.setText(R.string.safe_mode_excep_tips);
                        toast.show();
                    }
                });
            }

            @Override
            protected void onBandageExceptionHappened(Throwable throwable) {
                throwable.printStackTrace();//打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
                toast.setText("Cockroach Worked");
                toast.show();
            }

            @Override
            protected void onEnterSafeMode() {
                int tips = R.string.safe_mode_tips;
                Toast.makeText(App.this, getResources().getString(tips), Toast.LENGTH_LONG).show();
                DebugSafeModeUI.showSafeModeUI();

                if (BuildConfig.DEBUG) {
                    Intent intent = new Intent(App.this, DebugSafeModeTipActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            protected void onMayBeBlackScreen(Throwable e) {
                Thread thread = Looper.getMainLooper().getThread();
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", e);
                //黑屏时建议直接杀死app
                sysExceptHandler.uncaughtException(thread, new RuntimeException("black screen"));
            }
        });
    }
}
