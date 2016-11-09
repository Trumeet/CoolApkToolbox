package kh.android.cool_apk_toolbox.hook;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import kh.android.cool_apk_toolbox.R;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/9.
 * Edited by 宇腾
 */

public class NavigationDrawerHook {
    private DrawerLayout drawerLayout;
    private View mDrawer;
    public void addNavigationDrawer(Activity activity, Context MOD_Context) throws Throwable {
        drawerLayout = new DrawerLayout(MOD_Context);
        drawerLayout.setFocusable(true);
        drawerLayout.setFocusableInTouchMode(true);

        //Create Drawer
        mDrawer = View.inflate(MOD_Context, R.layout.drawer, null);
        long drawerWidthdip = getDrawerWidthdip(MOD_Context);
        DrawerLayout.LayoutParams lp =
                new DrawerLayout.LayoutParams(dipTopx(MOD_Context, drawerWidthdip), DrawerLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.START;
        mDrawer.setLayoutParams(lp);

        //TODO:Drawer List
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
