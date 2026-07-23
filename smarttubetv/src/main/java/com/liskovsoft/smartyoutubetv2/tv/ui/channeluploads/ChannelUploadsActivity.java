package com.liskovsoft.smartyoutubetv2.tv.ui.channeluploads;

import android.content.res.Configuration;
import android.os.Bundle;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.common.LeanbackActivity;

public class ChannelUploadsActivity extends LeanbackActivity {
    private static final String TAG = ChannelUploadsActivity.class.getSimpleName();
    private ChannelUploadsFragment mFragment;
    private int mCurrentOrientation = Configuration.ORIENTATION_UNDEFINED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentOrientation = getResources().getConfiguration().orientation;
        setContentView(R.layout.fragment_channel_uploads);
        mFragment = (ChannelUploadsFragment) getSupportFragmentManager().findFragmentById(R.id.channel_uploads_fragment);
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
    public void finishReally() {
        super.finishReally();

        mFragment.onFinish();
    }
}
