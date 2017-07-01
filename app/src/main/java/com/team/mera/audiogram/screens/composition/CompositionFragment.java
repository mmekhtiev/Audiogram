package com.team.mera.audiogram.screens.composition;

import android.animation.Animator;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.Filter;
import com.team.mera.audiogram.models.Track;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BasePermissionFragment;
import com.team.mera.audiogram.screens.common.TrackListener;
import com.team.mera.audiogram.utils.DrawUtils;
import com.team.mera.audiogram.utils.NotificationUtils;
import com.team.mera.audiogram.utils.SoundsManager;
import com.team.mera.audiogram.widgets.TrackAdapter;
import com.team.mera.audiogram.widgets.pinchview.PinchListViewListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CompositionFragment extends BasePermissionFragment implements TrackListener, CompositionView {
    private static final String ARG_DESCRIPTIONS = "descriptions";

    private static final int MAX_AUDIO_DURATION = 45000;

    @BindView(R.id.composition_list)
    ListView mCompositionList;

    @BindView(R.id.composition_progress)
    View mProgress;

    @BindView(R.id.composition_filter)
    RecyclerView mFilterRecycleView;

    private CompositionPresenter mCompositionPresenter;
    @BindView(R.id.composition_play)
    FloatingActionButton mPlayButton;

    @BindView(R.id.slider)
    View mPlayProgressSlider;

    private boolean mIsPlaying;

    private ArrayList<TrackDescription> mDescriptions;
    private TrackAdapter mAdapter;
    private SoundsManager mSoundsManager;

    private ArrayList<TrackTask> mTaskList = new ArrayList<>();

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

        //TODO: add real filters
        ArrayList<Filter> list = new ArrayList<>();

        Filter filter1 = new Filter();
        filter1.setTitle("ECHO");

        Filter filter2 = new Filter();
        filter2.setTitle("REPEAT");

        Filter filter3 = new Filter();
        filter3.setTitle("MIRROW");

        list.add(filter1);
        list.add(filter2);
        list.add(filter3);

        mFilterRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mFilterRecycleView.setAdapter(new FilterAdapter(list));

        mAdapter = new TrackAdapter(mContext, new ArrayList<Track>());

        mCompositionList.setAdapter(mAdapter);
        mCompositionList.setOnTouchListener(new PinchListViewListener(mCompositionList));

        if (mDescriptions != null) {
            for (TrackDescription description : mDescriptions) {
                TrackTask task = new TrackTask(this);
                task.execute(description);

                synchronized (this) {
                    mTaskList.add(task);
                }
            }

            mProgress.setVisibility(View.VISIBLE);
        }

        setHasOptionsMenu(true);

        mCompositionPresenter = new ComposerPresenterImpl(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        synchronized (this) {
            for (TrackTask task : mTaskList) {
                task.cancel(true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.composition_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record:
                mCompositionPresenter.compose(mAdapter.getTracks());
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
            if (mProgress != null) {
                mProgress.setVisibility(View.GONE);
            }
            mAdapter.add(track);
        }
    }

    private ViewPropertyAnimator mAnimator;
    @OnClick(R.id.composition_play)
    public void onPlay() {
        if (!mIsPlaying) {
            mSoundsManager.loadTracks(mAdapter.getDescriptions());
            mSoundsManager.playAll();
            mPlayButton.setImageResource(R.drawable.ic_stop_24dp);
            mIsPlaying = true;

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            mAnimator = mPlayProgressSlider.animate().x(width).setDuration(MAX_AUDIO_DURATION).setListener(new Animator.AnimatorListener() {
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
            if(mAnimator != null) {
                mAnimator.cancel();
            }
        }
    }

    @Override
    public void onSuccess() {
        mListener.openHome();
    }

    @Override
    public void onError() {
        Snackbar.make(mProgress, "Error", Snackbar.LENGTH_SHORT).show();
        mListener.openHome();
    }

    public static class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Holder> implements View.OnClickListener {
        private List<Filter> mFilterList;

        public FilterAdapter(ArrayList<Filter> filterItems) {
            mFilterList = filterItems;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            view.setOnClickListener(this);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Filter filter = mFilterList.get(position);
            holder.setFilter(filter);
        }

        @Override
        public int getItemCount() {
            return mFilterList.size();
        }

        @Override
        public void onClick(View v) {
            NotificationUtils.showToast(v.getContext(), "This awesome feature is not implemented");
        }

        public static class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.imageView1)
            ImageView mImage;

            @BindView(R.id.test_text)
            TextView mTitle;

            @BindView(R.id.checkBox1)
            View mCheck;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setFilter(Filter filter) {
                mImage.setImageResource(filter.getImage());
                mTitle.setText(filter.getTitle());
                mTitle.setCompoundDrawablesWithIntrinsicBounds(0, filter.getImage(), 0, 0);
                mImage.setBackgroundColor(DrawUtils.getGreyColor());
                mCheck.setVisibility(View.GONE);
            }
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
