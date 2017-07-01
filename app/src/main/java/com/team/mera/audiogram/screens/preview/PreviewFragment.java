package com.team.mera.audiogram.screens.preview;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewFragment extends BaseFragment {

    @BindView(R.id.audio_gallery_recycler_view)
    RecyclerView mAudioRecyclerView;

    private ArrayList<TrackDescription> mSamplesList;
    private AudioAdapter mAudioAdapter;

    public PreviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_preview, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mAudioRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mSamplesList = getAudioList();
        mAudioAdapter = new AudioAdapter(mSamplesList);
        mAudioRecyclerView.setAdapter(mAudioAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private class AudioHolder extends RecyclerView.ViewHolder {

        private ImageView mItemSoundView;
        private CheckBox mItemSoundCheckbox;
        private TextView mItemTestText;

        public AudioHolder(View itemView) {
            super(itemView);
            mItemSoundView = (ImageView) itemView.findViewById(R.id.imageView1);
            mItemSoundCheckbox = (CheckBox) itemView.findViewById(R.id.checkBox1);
            mItemTestText = (TextView) itemView.findViewById(R.id.test_text);
        }

        public void bindName(String text) {
            mItemTestText.setText(text);
        }

        public void setBackground(int color) {
            int h = 250;
            Log.d("PreviewFragment", Integer.toString(mItemSoundView.getHeight()));
            ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setShader(new LinearGradient(0, 0, h, h, color, Color.parseColor("#FFFFFF"), Shader.TileMode.REPEAT));
            mItemSoundView.setBackgroundDrawable(mDrawable);
        }
    }

    private class AudioAdapter extends RecyclerView.Adapter<PreviewFragment.AudioHolder> {
        private List<TrackDescription> mAudioGalleryItems;

        public AudioAdapter(ArrayList<TrackDescription> galleryItems) {
            mAudioGalleryItems = galleryItems;
        }

        @Override
        public PreviewFragment.AudioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PreviewFragment.AudioHolder(view);
        }

        @Override
        public void onBindViewHolder(PreviewFragment.AudioHolder holder, int position) {
            TrackDescription galleryItem = mAudioGalleryItems.get(position);
            holder.bindName(galleryItem.getName());
            holder.setBackground(generateRandomColor());
        }

        @Override
        public int getItemCount() {
            return mAudioGalleryItems.size();
        }

        private int generateRandomColor() {
            Random rand = new Random();
            return Color.argb(128, rand.nextInt(128) + 128, rand.nextInt(128) + 128, rand.nextInt(128) + 128);
        }
    }

    private ArrayList<TrackDescription> getAudioList() {
        final Cursor mCursor = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        ArrayList<TrackDescription> songsList = new ArrayList<>();
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                TrackDescription song = new TrackDescription();
                song.setName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                songsList.add(song);
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return songsList;
    }
}
