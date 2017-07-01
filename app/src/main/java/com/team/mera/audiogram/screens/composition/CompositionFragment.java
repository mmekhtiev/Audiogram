package com.team.mera.audiogram.screens.composition;

import android.animation.Animator;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BasePermissionFragment;
import com.team.mera.audiogram.screens.common.TrackListener;
import com.team.mera.audiogram.utils.SoundsManager;
import com.team.mera.audiogram.widgets.TrackAdapter;
import com.team.mera.audiogram.widgets.pinchview.PinchListViewListener;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CompositionFragment extends BasePermissionFragment implements TrackListener {
    private static final String ARG_DESCRIPTIONS = "descriptions";

    @BindView(R.id.composition_list)
    ListView mCompositionList;

    @BindView(R.id.composition_progress)
    View mProgress;

    @BindView(R.id.composition_play)
    FloatingActionButton mPlayButton;

    @BindView(R.id.slider)
    View mPlayProgressSlider;

    private boolean mIsPlaying;

    private ArrayList<TrackDescription> mDescriptions;
    private TrackAdapter mAdapter;
    private SoundsManager mSoundsManager;

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
        mSoundsManager = new SoundsManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_composition, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mAdapter = new TrackAdapter(mContext, new ArrayList<Track>());

        mCompositionList.setAdapter(mAdapter);
        mCompositionList.setOnTouchListener(new PinchListViewListener(mCompositionList));

        if (mDescriptions != null) {
            for (TrackDescription description : mDescriptions) {
                new TrackTask(this).execute(description);
            }

            mProgress.setVisibility(View.VISIBLE);
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.composition_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record:
                mListener.openHome();
                break;
        }
        return true;
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    }

    @Override
    protected void onPermissionDenied() {
        mListener.back();
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public boolean onProgress(double progress) {
        return isAdded();
    }

    @Override
    public void onSuccess(Track track) {
        if (track != null) {
            mProgress.setVisibility(View.GONE);
            mAdapter.add(track);
        }
    }

    @OnClick(R.id.composition_play)
    public void onPlay() {
        if(!mIsPlaying) {
            mSoundsManager.loadTracks(mAdapter.getDescriptions());
            mSoundsManager.playAll();
            mPlayButton.setImageResource(R.drawable.ic_stop_24dp);
            mIsPlaying = true;

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            mPlayProgressSlider.animate().x(width).setDuration(43000).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPlayButton.setImageResource(R.drawable.ic_play_arrow);
                    mPlayProgressSlider.setX(0);
                    mIsPlaying = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            mSoundsManager.stop();
            mPlayButton.setImageResource(R.drawable.ic_play_arrow);
            mIsPlaying = false;
    }
    }

    private static class TrackTask extends AsyncTask<TrackDescription, Void, Track> {
        TrackListener mListener;

        public TrackTask(TrackListener listener) {
            super();
            mListener = listener;
        }

        @Override
        protected Track doInBackground(TrackDescription... params) {
            TrackDescription description = params[0];

            Track track = null;
            try {
                track = Track.create(description.getPath(), mListener);
                track.setColor(description.getColor());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return track;
        }

        @Override
        protected void onPostExecute(Track track) {
            mListener.onSuccess(track);
        }

        @Override
        protected void onCancelled() {
            mListener.onSuccess(null);
        }
    }
}
