package com.team.mera.audiogram.screens.preview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.team.mera.audiogram.PreviewScreenAdapter;
import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BaseFragment;
import com.team.mera.audiogram.screens.composition.CompositionFragment;
import com.team.mera.audiogram.screens.home.HomeFragment;
import com.team.mera.audiogram.utils.DrawUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import static com.team.mera.audiogram.utils.FileUtils.getAudioList;

public class PreviewFragment extends BaseFragment implements PreviewView, PreviewScreenAdapter.PreviewItemsListener {

    @BindView(R.id.audio_gallery_recycler_view)
    RecyclerView mAudioRecyclerView;

    @BindView(R.id.stop_btn_screen)
    RelativeLayout mStopBtnScreen;

    private ArrayList<TrackDescription> mSamplesList;
    private ArrayList<TrackDescription> mSelectedItems;
    private PreviewScreenAdapter mPreviewScreenAdapter;
    private PreviewPresenter mPresenter;

    public PreviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_preview, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAudioRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mSamplesList = getAudioList(mContext);
        mPreviewScreenAdapter = new PreviewScreenAdapter(mContext, mSamplesList, this);
        mAudioRecyclerView.setAdapter(mPreviewScreenAdapter);
        setHasOptionsMenu(true);

        mPresenter = new PreviewPresenterImpl(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                btnNextClick();
                break;
        }
        return true;
    }

    public void btnNextClick() {
        mSelectedItems = mPreviewScreenAdapter.getCheckedItems();
        if (mSelectedItems != null && !mSelectedItems.isEmpty()) {
            mListener.setDescriptions(mSelectedItems);
            mListener.open(CompositionFragment.newInstance(mListener.getDescriptions()), true);
        } else {
            Toast.makeText(getContext(), "You should select some samples or record new to continue.", Toast.LENGTH_LONG).show();
        }
    }

    @OnTouch(R.id.stop_btn)
    boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                onStopClicked();
                break;
            }
            default: {
                break;
            }
        }
        return false;
    }

    @Override
    public void onPlayClicked(TrackDescription track) {
        mPresenter.play(track);
        track.setPlaying(true);
        mStopBtnScreen.setVisibility(View.VISIBLE);
        mPreviewScreenAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStopClicked() {
        mPresenter.stop();
        mStopBtnScreen.setVisibility(View.GONE);
    }

    @Override
    public void onPlayingCompleted(TrackDescription track) {
        track.setPlaying(false);
        mPreviewScreenAdapter.notifyDataSetChanged();
    }
}
