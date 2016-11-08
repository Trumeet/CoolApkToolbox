package kh.android.cool_apk_toolbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XResources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import kh.android.cool_apk_toolbox.ui.MainActivity;

import static kh.android.cool_apk_toolbox.HookEntry.PKG_COOLAPK;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/7.
 * Edited by 宇腾
 */

public class HookClass implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources{
    private static final String TAG = "[CoolAPKToolBox]";

    public XSharedPreferences prefs;
    private static final String PREFS_HIDE_BOTTOM_BAR = "hide_bottom_bar";
    private static final String PREFS_DISABLE_SPLASH = "disable_splash";
    public static final String PREFS_REPLACE_ICON = "replace_icon";
    public static final String PREFS_HIDE_ICON = "hide_icon";
    public static final String PREFS_ICON_SAVE_PATH = "icon_save_path";

    private Resources mResTarget;
    private Context mContextTarget;
    private Resources mRes;
    private Context mContext;
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log(TAG + "Module Loaded");
        prefs = new XSharedPreferences(AppMain.class.getPackage().getName());
        prefs.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 设置激活状态
        if (AppMain.class.getPackage().getName().equals(loadPackageParam.packageName)) {
            XposedHelpers.findAndHookMethod(MainActivity.class.getName(), loadPackageParam.classLoader, "isEnabled", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                }
            });
            return;
        }

        if (!PKG_COOLAPK.equals(loadPackageParam.packageName))
            return;
        XposedBridge.log(TAG + "Handle COOLAPK Pkg");

        // 加载资源和Context
        XposedHelpers.findAndHookMethod(PKG_COOLAPK + HookEntry.CLASS_APPLICATION,  loadPackageParam.classLoader, HookEntry.METHOD_CREATE,  new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mContextTarget = ((Context) param.thisObject).getApplicationContext();
                if (mContextTarget != null) {
                    XposedBridge.log(TAG + "Target Application-Context get!");
                    mResTarget = mContextTarget.getResources();
                    if (mResTarget != null)
                        XposedBridge.log(TAG + "Target Resource get!");

                    mContext = mContextTarget.createPackageContext(
                            AppMain.class.getPackage().getName(), Context.CONTEXT_IGNORE_SECURITY);
                    XposedBridge.log(TAG + "Mine Application-Context get!");
                    mRes = mContext.getResources();
                    if (mRes != null)
                        XposedBridge.log(TAG + "Mine Resource get!");
                } else {
                    XposedBridge.log(TAG + "Target Application-Context get fail, other skipping..");
                }
            }
        });

        // 隐藏底栏
        XposedHelpers.findAndHookMethod(PKG_COOLAPK + HookEntry.CLASS_MAIN_FRAGMENT, loadPackageParam.classLoader, HookEntry.METHOD_CREATE_VIEW, LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log(TAG + "onCreateView called");
                prefs.reload();
                if (prefs.getBoolean(PREFS_HIDE_BOTTOM_BAR, false)) {
                    // 隐藏底栏
                    XposedBridge.log(TAG + "Hiding bottom bar");
                    int id_bottom = mResTarget.getIdentifier("bottom_navigation", "id", PKG_COOLAPK);
                    View view_bottom_bar = ((View)param.getResult()).findViewById(id_bottom);
                    XposedBridge.log(TAG + "Bottom bar id=" + id_bottom);
                    XposedBridge.log(TAG + "Bottom bar view=" + view_bottom_bar);
                    view_bottom_bar.setVisibility(View.GONE);
                    // 添加抽屉
                    /*
                    XposedBridge.log(TAG + "Adding navigation drawer");
                    int id_toolbar = mResTarget.getIdentifier("toolbar", "id", PKG_COOLAPK);
                    XposedBridge.log(TAG + "toolbar id=" + id_toolbar);
                    Toolbar toolbar = (Toolbar) ((View)param.getResult()).findViewById(id_toolbar);
                    XposedBridge.log(TAG + "toolbar view=" + id_toolbar);
                    final DrawerLayout drawerLayout = new DrawerLayout(((Fragment)param.thisObject).getActivity());
                    ((ViewGroup)param.args[2]).addView(drawerLayout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            ((Fragment)param.thisObject).getActivity(),
                            drawerLayout,toolbar,
                            mRes.getIdentifier("nav_open", "id", AppMain.class.getPackage().getName())
                            , mRes.getIdentifier("nav_close", "id", AppMain.class.getPackage().getName()));
                    //toggle.setHomeAsUpIndicator(upArrow);
                    drawerLayout.setDrawerListener(toggle);
                    toggle.syncState();
                    */
                }else {
                    XposedBridge.log(TAG + "Hide bottom bar disabled, skipping");
                }
            }
        });

        // 去启动页
        XposedHelpers.findAndHookMethod(HookEntry.PKG_COOLAPK + HookEntry.CLASS_SPLASH_ACTIVITY,
                loadPackageParam.classLoader, HookEntry.METHOD_CREATE, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + "onCreate called");
                        prefs.reload();
                        if (prefs.getBoolean(PREFS_DISABLE_SPLASH, false)) {
                            Activity activity = (Activity) param.thisObject;
                            activity.startActivity(new Intent(activity, XposedHelpers.findClass(HookEntry.PKG_COOLAPK + HookEntry.CLASS_MAIN_ACTIVITY, loadPackageParam.classLoader)));
                            activity.finish();
                        } else {
                            XposedBridge.log(TAG + "Disable splash disabled, skipping");
                        }
                    }
                });


    }
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
        if (!HookEntry.PKG_COOLAPK.equals(initPackageResourcesParam.packageName))
            return;
        // 替换图标
        final String PATH_CURRENT_ICON = prefs.getString(PREFS_ICON_SAVE_PATH, null);
        if (PATH_CURRENT_ICON == null) {
            XposedBridge.log(TAG + "Replace icon disabled, skipping");
            return;
        }
        final File file = new File(PATH_CURRENT_ICON);
        if (!file.exists()) {
            XposedBridge.log(TAG + "Replace icon disabled, skipping");
        } else {
            XposedBridge.log(TAG + "Replacing icon...");
            initPackageResourcesParam.res.setReplacement(HookEntry.PKG_COOLAPK, "mipmap", "ic_launcher", new XResources.DrawableLoader() {
                @Override
                public Drawable newDrawable(XResources xResources, int i) throws Throwable {
                    return BitmapDrawable.createFromPath(PATH_CURRENT_ICON);
                }
            });
        }
    }
}
