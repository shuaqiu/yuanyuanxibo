/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SettingsActivity extends PreferenceActivity {

    // private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Intent intent = getIntent();
    }

}
