package kh.android.cool_apk_toolbox;

import android.app.Application;
import android.util.Log;

import im.fir.sdk.FIR;

/**
 * Project CoolAPKToolBox
 * 应用程序入口
 * Created by 宇腾 on 2016/11/8.
 * Edited by 宇腾
 */

public class AppMain extends Application {
    private static final String TAG = "app";
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.crash_upload) {
            FIR.init(this);
            Log.d(TAG, "onCreate: Bug upload enable");
        } else {
            Log.d(TAG, "onCreate: Bug upload disable");
        }
    }
}
