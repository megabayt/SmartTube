package com.liskovsoft.smartyoutubetv2.tv.ui.browse;

import android.content.res.Configuration;
import android.os.Bundle;

import com.liskovsoft.sharedutils.helpers.MessageHelpers;
import com.liskovsoft.smartyoutubetv2.common.prefs.MainUIData;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.common.LeanbackActivity;

public class BrowseActivity extends LeanbackActivity {
    private static final String TAG = BrowseActivity.class.getSimpleName();
    private int mCurrentOrientation = Configuration.ORIENTATION_UNDEFINED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentOrientation = getResources().getConfiguration().orientation;
        try {
            setContentView(R.layout.fragment_main);
        } catch (NoClassDefFoundError e) {
            // Failed resolution of: Landroidx/lifecycle/ViewTreeLifecycleOwner;
            MessageHelpers.showMessage(this, e.getMessage());
        }
    }

    /**
     * Grids use a different column count per orientation. Applying it to a live grid crashes
     * the leanback layout manager, so rebuild the whole screen instead.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != mCurrentOrientation) {
            mCurrentOrientation = newConfig.orientation;
            recreate();
        }
    }

    @Override
    protected void initTheme() {
        int browseThemeResId = MainUIData.instance(this).getColorScheme().browseThemeResId;
        if (browseThemeResId > 0) {
            setTheme(browseThemeResId);
        }
    }
}
