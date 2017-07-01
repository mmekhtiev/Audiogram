package com.team.mera.audiogram.screens.composition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BasePermissionFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class CompositionFragment extends BasePermissionFragment {
    private static final String ARG_DESCRIPTIONS = "descriptions";

    private ArrayList<TrackDescription> mDescriptions;

    public CompositionFragment() {
    }

    public static CompositionFragment newInstance(ArrayList<TrackDescription> descriptions) {
        CompositionFragment fragment = new CompositionFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DESCRIPTIONS, descriptions);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDescriptions = getArguments().getParcelableArrayList(ARG_DESCRIPTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_composition, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
    }

    @Override
    protected void onPermissionDenied() {
        mListener.back();
    }

    @Override
    protected void onPermissionGranted() {

    }
}
