package net.rodrigobrito.firetodolist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewTaskActivityFragment extends Fragment {

    private int id;
    private FrameLayout mAdsFrame;

    public ViewTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_view_task, container, false);

        mAdsFrame = (FrameLayout) rootView.findViewById(R.id.ads_frame);
        AdView mAdView = new AdView(getActivity());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mAdsFrame.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.phone_test_unique_id))
                .build();
        mAdView.loadAd(adRequest);

        return rootView;
    }
}
