package com.team.mera.audiogram.screens.home;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.team.mera.audiogram.HomeScreenAdapter;
import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BaseFragment;
import com.team.mera.audiogram.screens.gallery.GalleryFragment;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private HomeScreenAdapter mAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new HomeScreenAdapter(getAudioList());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mListener.open(new GalleryFragment(), true);
                break;
        }
        return true;
    }

    public static ArrayList<TrackDescription> getAudioList() {
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)  + "/Audiogram" + "/samples/";

        File directory = new File(dir);
        File[] files = directory.listFiles();

        ArrayList<TrackDescription> songsList = new ArrayList<>();

        for (File file:
                files) {
            TrackDescription song = new TrackDescription();
            song.setName(file.getName());
            song.setPath(file.getPath());
            songsList.add(song);
        }

        return songsList;
    }
}
