package kh.android.cool_apk_toolbox.ui;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import kh.android.cool_apk_toolbox.HookEntry;
import kh.android.cool_apk_toolbox.R;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/7.
 * Edited by 宇腾
 */

public class MainActivity extends AppCompatActivity {
    private static final int RC_REQUEST_PERMISSION = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        String subTitle = "";
        if (!isEnabled())
            subTitle += getString(R.string.err_not_active) + " ";
        if (!isPackageInstalled(HookEntry.PKG_COOLAPK))
            subTitle += getString(R.string.err_not_install) + " ";
        else {
            PackageInfo info = getInfo(HookEntry.PKG_COOLAPK);
            int[] support_version = getResources().getIntArray(R.array.support_cool_apk_version_code);
            if (info != null) {
                boolean support = false;
                for (int i : support_version) {
                    if (i == info.versionCode) {
                        support = true;
                        break;
                    }
                }
                if (!support) {
                    subTitle += getString(R.string.err_not_support, info.versionName + " " + info.versionCode ) + " ";
                }
            }
        }
        toolbar.setSubtitle(subTitle);

        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        if (Build.VERSION.SDK_INT >= 23) {
            if (new ContextWrapper(MainActivity.this).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_REQUEST_PERMISSION);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restart :
                return MainFragment.forceStop(MainActivity.this, true);
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean isEnabled () {
        return false;
    }
    private boolean isPackageInstalled (String packagename) {
        try {
            getPackageManager().getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    @Nullable
    private PackageInfo getInfo (String packageName) {
        try {
            return getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}