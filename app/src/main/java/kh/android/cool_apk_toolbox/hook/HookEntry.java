package kh.android.cool_apk_toolbox.hook;

import android.os.Environment;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/8.
 * Edited by 宇腾
 */

public class HookEntry {
    public static final String PKG_COOLAPK = "com.coolapk.market";

    public static final String CLASS_APPLICATION = ".CoolMarketApplication";
    public static final String CLASS_MAIN_ACTIVITY = ".view.main.MainActivity";
    public static final String CLASS_MAIN_FRAGMENT = ".view.main.MainFragment";
    public static final String CLASS_SPLASH_ACTIVITY = ".view.SplashActivity";

    public static final String METHOD_CREATE_VIEW = "onCreateView";
    public static final String METHOD_CREATE = "onCreate";
}
