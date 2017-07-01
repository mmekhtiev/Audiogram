package com.team.mera.audiogram.screens.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.screens.addrecord.RecordFragment;
import com.team.mera.audiogram.screens.common.BasePermissionFragment;
import com.team.mera.audiogram.screens.composition.CompositionFragment;
import com.team.mera.audiogram.screens.preview.PreviewFragment;
import com.team.mera.audiogram.utils.NotificationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleryFragment extends BasePermissionFragment {

    @BindView(R.id.gallery_pager)
    ViewPager mViewPager;

    private GalleryPagerAdapter mPagerAdapter;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                mListener.open(new CompositionFragment(), true);
                break;
        }
        return true;
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] {"android.permission.RECORD_AUDIO", "android.permission.MODIFY_AUDIO_SETTINGS"};
    }

    @Override
    protected void onPermissionDenied() {
        NotificationUtils.showToast(mContext, "Records permissions not granted");
        showPager(1);
    }

    @Override
    protected void onPermissionGranted() {
        showPager(2);
    }

    private void showPager(int count) {
        mPagerAdapter = new GalleryPagerAdapter(getChildFragmentManager(), count);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private static class GalleryPagerAdapter extends FragmentStatePagerAdapter {
        private int mCount;

        public GalleryPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            mCount = count;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PreviewFragment();
                case 1:
                    return new RecordFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "GALLERY";
                case 1:
                    return "RECORD";
            }
            return null;
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
