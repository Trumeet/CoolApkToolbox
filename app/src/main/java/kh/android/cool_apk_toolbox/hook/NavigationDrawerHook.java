package kh.android.cool_apk_toolbox.hook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import kh.android.cool_apk_toolbox.HookClass;
import kh.android.cool_apk_toolbox.R;


/**
 * Project CoolAPKToolBox
 * <p>
 *     edited from WechatMod
 * Created by 宇腾 on 2016/11/9.
 * Edited by 宇腾
 */

public class NavigationDrawerHook {
    private DrawerLayout drawerLayout;
    private View mDrawer;
    private ClassLoader classLoader;
    private XC_MethodHook.MethodHookParam param;
    public NavigationDrawerHook (ClassLoader classLoader, XC_MethodHook.MethodHookParam param) {
        this.classLoader = classLoader;
        this.param = param;
    }
    public void addNavigationDrawer(Activity activity, Context MOD_Context) throws Throwable {
        drawerLayout = new DrawerLayout(MOD_Context);
        drawerLayout.setFocusable(false);
        drawerLayout.setFocusableInTouchMode(false);

        //Create Drawer
        mDrawer = View.inflate(MOD_Context, R.layout.drawer, null);
        long drawerWidthdip = getDrawerWidthdip(MOD_Context);
        XposedBridge.log(HookClass.TAG + "Navigation drawer width=" + drawerWidthdip);
        DrawerLayout.LayoutParams lp =
                new DrawerLayout.LayoutParams(dipTopx(MOD_Context, drawerWidthdip), DrawerLayout.LayoutParams.MATCH_PARENT);
        HookClass.prefs.reload();
        if (!HookClass.prefs.getBoolean(HookClass.PREFS_NAV_RIGHT, false))
            lp.gravity = Gravity.START;
        else
            lp.gravity = Gravity.END;
        mDrawer.setLayoutParams(lp);
        drawerLayout.setFitsSystemWindows(true);
        drawerLayout.addView(mDrawer);

        //go go go
        activity.addContentView(drawerLayout,
                new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        //don't use activity.addContentView(drawerLayout);  because it causes activity exit.
    }
    public static long getDrawerWidthdip(Context context) {
        final int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        if (widthPixels <= 480) {
            return 260L;
        } else {
            return 296L;
        }
    }
    public static int dipTopx(Context context, long dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }
}
