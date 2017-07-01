package com.team.mera.audiogram;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GalleryPreviewFragment extends Fragment {
    private RecyclerView mAudioRecyclerView;
    public ArrayList<HashMap<String, String>> samplesList = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_preview, container, false);
        mAudioRecyclerView = (RecyclerView) view.findViewById(R.id.audio_gallery_recycler_view);

        mAudioRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        for (int i = 0; i < 20; i++) {
//            HashMap<String, String> testItem = new HashMap<>();
//            testItem.put("sampleTitle", "sample title" + i);
//            testItem.put("samplePath", "sample path");
//
//            samplesList.add(testItem);
//        }

        samplesList = getAudioList();
        mAudioRecyclerView.setAdapter(new AudioAdapter(samplesList));
        return view;
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
            Log.d("GalleryPreviewFragment", Integer.toString(mItemSoundView.getHeight()));
            ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setShader(new LinearGradient(0, 0, h, h, color, Color.parseColor("#FFFFFF"), Shader.TileMode.REPEAT));
            mItemSoundView.setBackgroundDrawable(mDrawable);
        }
    }

    private class AudioAdapter extends RecyclerView.Adapter<AudioHolder> {
        private List<HashMap<String, String>> mAudioGalleryItems;

        public AudioAdapter(ArrayList<HashMap<String, String>> galleryItems) {
            mAudioGalleryItems = galleryItems;
        }

        @Override
        public AudioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new AudioHolder(view);
        }

        @Override
        public void onBindViewHolder(AudioHolder holder, int position) {
            HashMap<String, String> galleryItem = mAudioGalleryItems.get(position);
            holder.bindName(galleryItem.get("sampleTitle"));
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

    private ArrayList<HashMap<String, String>> getAudioList() {
        final Cursor mCursor = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
        ArrayList<HashMap<String, String>> songsList = new ArrayList<>();
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                HashMap<String, String> song = new HashMap<>();
                song.put("sampleTitle", mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.put("samplePath", mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                songsList.add(song);
                i++;
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return songsList;
    }
}
