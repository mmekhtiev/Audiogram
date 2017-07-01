package com.team.mera.audiogram.screens.addrecord;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.R;

import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements RecordView {

    private Unbinder mUnbinder;

    private RecordPresenter mRecordPresenter;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecordPresenter = new RecordPresenterImpl(this);
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
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
        return true;
    }

}
