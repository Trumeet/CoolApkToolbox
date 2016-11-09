package kh.android.cool_apk_toolbox.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.github.mrengineer13.snackbar.SnackBar;

import kh.android.cool_apk_toolbox.R;

/**
 * Project CoolAPKToolBox
 * <p>
 * Created by 宇腾 on 2016/11/9.
 * Edited by 宇腾
 */

public class AboutFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);
        PreferenceCategory category_developers = (PreferenceCategory)findPreference("developers");
        for (final String s : getResources().getStringArray(R.array.developers)) {
            Preference preference = new Preference(getActivity());
            preference.setTitle(s.split(">")[0]);
            preference.setSummary(s.split(">")[1]);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startUrl(s.split(">")[2]);
                    return true;
                }
            });
            category_developers.addPreference(preference);
        }
        getPreferenceScreen().addPreference(category_developers);

        PreferenceCategory category_libs = (PreferenceCategory)findPreference("libs");
        for (final String s : getResources().getStringArray(R.array.libs)) {
            Preference preference = new Preference(getActivity());
            preference.setTitle(s.split(">")[0]);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startUrl(s.split(">")[1]);
                    return true;
                }
            });
            category_libs.addPreference(preference);
        }
        getPreferenceScreen().addPreference(category_libs);
    }
    private void startUrl (String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            new SnackBar.Builder(getActivity()).withMessageId(R.string.err_open_url).show();
        }
    }
}
