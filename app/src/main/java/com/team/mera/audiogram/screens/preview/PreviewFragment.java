package com.team.mera.audiogram.screens.preview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.screens.common.BaseFragment;

import butterknife.ButterKnife;

public class PreviewFragment extends BaseFragment {

    public PreviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }
}
