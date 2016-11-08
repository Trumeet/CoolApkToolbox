package kh.android.cool_apk_toolbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/8.
 * Edited by 宇腾
 */

public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
