package com.team.mera.audiogram;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.mera.audiogram.models.TrackDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PreviewScreenAdapter extends RecyclerView.Adapter<PreviewScreenAdapter.AudioHolder> {
    private List<TrackDescription> mAudioGalleryItems;
    SparseBooleanArray mSelectedItems;
    Context context;


    public PreviewScreenAdapter(Context context, ArrayList<TrackDescription> galleryItems) {
        this.context = context;
        mAudioGalleryItems = galleryItems;
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public AudioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gallery_item, parent, false);
        return new AudioHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioHolder holder, int position) {
        TrackDescription galleryItem = mAudioGalleryItems.get(position);
        holder.bindName(galleryItem.getName());
        holder.setBackground(generateRandomColor());
        holder.mItemSoundCheckbox.setTag(position);
        holder.mItemSoundCheckbox.setChecked(mSelectedItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mAudioGalleryItems.size();
    }

    private int generateRandomColor() {
        Random rand = new Random();
        int greyColor = rand.nextInt(85) + 171;
        return Color.argb(128, greyColor, greyColor, greyColor);
    }

    public ArrayList<TrackDescription> getCheckedItems() {
        ArrayList<TrackDescription> mTempArray = new ArrayList<>();
        for(int i=0;i < mAudioGalleryItems.size();i++) {
            if(mSelectedItems.get(i)) {
                mTempArray.add(mAudioGalleryItems.get(i));
            }
        }
        return mTempArray;
    }

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<> (mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); ++i) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public class AudioHolder extends RecyclerView.ViewHolder {
        ImageView mItemSoundView;
        CheckBox mItemSoundCheckbox;
        TextView mItemTestText;

        public AudioHolder(View itemView) {
            super(itemView);
            mItemSoundView = (ImageView) itemView.findViewById(R.id.imageView1);
            mItemTestText = (TextView) itemView.findViewById(R.id.test_text);
            mItemSoundCheckbox = (CheckBox) itemView.findViewById(R.id.checkBox1);
            mItemSoundCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleSelection(getAdapterPosition());
                }
            });
        }

        public void bindName(String text) {
            mItemTestText.setText(text);
        }

        public void setBackground(int color) {
            mItemSoundView.setBackgroundColor(color);
        }
    }
}
