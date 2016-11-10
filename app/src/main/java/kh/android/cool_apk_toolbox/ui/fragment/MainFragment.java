package kh.android.cool_apk_toolbox.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;
import kh.android.cool_apk_toolbox.HookClass;
import kh.android.cool_apk_toolbox.hook.HookEntry;
import kh.android.cool_apk_toolbox.R;
import kh.android.cool_apk_toolbox.ui.activity.LaunchActivity;

import static android.content.Context.MODE_WORLD_READABLE;

/**
 * Project CoolAPKToolBox
 * 设置Fragment
 * Created by 宇腾 on 2016/11/8.
 * Edited by 宇腾
 */

public class MainFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    ArrayList<String> mStrArrayIconListText;
    ArrayList<String> mStrArrayIconListKey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager()
                .setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.settings);
        ListPreference preference_icon = (ListPreference)findPreference(HookClass.PREFS_REPLACE_ICON);
        final R.drawable drawableResources = new R.drawable();
        final Class<R.drawable> c = R.drawable.class;
        final Field[] fields = c.getDeclaredFields();
        mStrArrayIconListKey = new ArrayList<>();
        mStrArrayIconListText = new ArrayList<>();

        mStrArrayIconListText.add(getString(R.string.text_not_change));
        mStrArrayIconListKey.add("");

        for (int i = 0, max = fields.length; i < max; i++) {
            try {
                if (!fields[i].getName().split("_")[0].equals("icon"))
                    continue;
                mStrArrayIconListKey.add(String.valueOf(fields[i].getInt(drawableResources)));
                mStrArrayIconListText.add(fields[i].getName());
            } catch (Exception e) {
                continue;
            }
        }
        String[] strText = new String[mStrArrayIconListText.size()];
        for (int i = 0; i < mStrArrayIconListText.size(); i ++)
            strText[i] = mStrArrayIconListText.get(i);
        preference_icon.setEntries(strText);
        String[] strKey = new String[mStrArrayIconListText.size()];
        for (int i = 0; i < mStrArrayIconListKey.size(); i ++)
            strKey[i] = mStrArrayIconListKey.get(i);
        preference_icon.setEntryValues(strKey);
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    public static final String PATH_CURRENT_ICON = Environment.getExternalStorageDirectory().getPath() + "/Pictures/coolapk_icon.png";
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (!s.equals(HookClass.PREFS_HIDE_ICON)) {
            Toast.makeText(getActivity(), R.string.text_need_restart, Toast.LENGTH_SHORT).show();
        }
        switch (s) {
            case HookClass.PREFS_REPLACE_ICON :
                new File(PATH_CURRENT_ICON).delete();
                if (!sharedPreferences.getString(s, "").equals("")) {
                    try {
                        File f=new File(PATH_CURRENT_ICON);
                        InputStream inputStream = getResources().openRawResource(Integer.parseInt(sharedPreferences.getString(s, "")));
                        OutputStream out=new FileOutputStream(f);
                        byte buf[]=new byte[1024];
                        int len;
                        while((len=inputStream.read(buf))>0)
                            out.write(buf,0,len);
                        out.close();
                        inputStream.close();
                        sharedPreferences.edit().putString(HookClass.PREFS_ICON_SAVE_PATH, PATH_CURRENT_ICON).apply();
                    }
                    catch (final Exception e){
                        Toast.makeText(getActivity(), R.string.err, Toast.LENGTH_SHORT).show();
                        sharedPreferences.edit().putString(HookClass.PREFS_ICON_SAVE_PATH, null).apply();
                    }
                } else {
                    sharedPreferences.edit().putString(HookClass.PREFS_ICON_SAVE_PATH, null).apply();
                }
                break;
            case HookClass.PREFS_HIDE_ICON :
                PackageManager packageManager = getActivity().getPackageManager();
                boolean b = !sharedPreferences.getBoolean(HookClass.PREFS_HIDE_ICON, false);
                int state = b
                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                String settings = LaunchActivity.class.getName();
                ComponentName alias = new ComponentName(getActivity(), settings);
                packageManager.setComponentEnabledSetting(alias, state,
                        PackageManager.DONT_KILL_APP);
                break;
        }
    }
    public static boolean forceStop (Activity context, final boolean restart) {
        if (!Shell.SU.available()) {
            Toast.makeText(context, R.string.err_need_root, Toast.LENGTH_SHORT).show();
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run("am force-stop " + HookEntry.PKG_COOLAPK);
                if (restart)
                    Shell.SU.run("am start -n " + HookEntry.PKG_COOLAPK + "/" + HookEntry.PKG_COOLAPK + HookEntry.CLASS_MAIN_ACTIVITY);
            }
        }).start();
        Toast.makeText(context, R.string.text_killed, Toast.LENGTH_SHORT).show();
        return true;
    }
}
