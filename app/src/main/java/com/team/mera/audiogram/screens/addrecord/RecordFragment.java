package com.team.mera.audiogram.screens.addrecord;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.screens.common.BasePermissionFragment;
import com.team.mera.audiogram.screens.composition.CompositionFragment;
import com.team.mera.audiogram.utils.NotificationUtils;

import butterknife.ButterKnife;
import butterknife.OnTouch;


public class RecordFragment extends BasePermissionFragment implements RecordView {

    private RecordPresenter mRecordPresenter;

    public RecordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
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
                mListener.open(CompositionFragment.newInstance(mListener.getDescriptions()), true);
                break;
        }
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecordPresenter = new RecordPresenterImpl(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRecordPresenter.stopRecord();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnTouch(R.id.microphone_btn)
    boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mRecordPresenter.startRecord();
                break;
            }
            case MotionEvent.ACTION_UP: {
                mRecordPresenter.stopRecord();
                break;
            }
            default: {
                break;
            }
        }
        return false;
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] {"android.permission.RECORD_AUDIO", "android.permission.MODIFY_AUDIO_SETTINGS"};
    }

    @Override
    protected void onPermissionDenied() {
        NotificationUtils.showToast(mContext, "Record permissions not granted");
        mListener.back();
    }

    @Override
    protected void onPermissionGranted() {

    }
}
