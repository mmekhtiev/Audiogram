package com.team.mera.audiogram;

import android.content.Context;
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

public class PreviewScreenAdapter extends RecyclerView.Adapter<PreviewScreenAdapter.AudioHolder> {
    private List<TrackDescription> mAudioGalleryItems;
    SparseBooleanArray mSelectedItems;
    private PreviewItemsListener mListener;
    Context context;

    public interface PreviewItemsListener {
        void onPlayClicked(TrackDescription track);
        void onStopClicked();
    }

    public PreviewScreenAdapter(Context context, ArrayList<TrackDescription> galleryItems, PreviewItemsListener listener) {
        this.context = context;
        mAudioGalleryItems = galleryItems;
        mSelectedItems = new SparseBooleanArray();
        mListener = listener;
    }

    @Override
    public AudioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gallery_item, parent, false);
        return new AudioHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioHolder holder, int position) {
        final TrackDescription galleryItem = mAudioGalleryItems.get(position);
        holder.bindName(galleryItem.getName());
        holder.setColorCode(galleryItem.getColor());
        holder.mItemSoundCheckbox.setTag(position);
        holder.mItemSoundCheckbox.setChecked(mSelectedItems.get(position));

        holder.mItemSoundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!galleryItem.isPlaying()) {
                    mListener.onPlayClicked(galleryItem);
                } else {
                    mListener.onStopClicked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudioGalleryItems.size();
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
        ImageView mItemColorCode;
        TextView mItemTestText;

        public AudioHolder(View itemView) {
            super(itemView);
            mItemSoundView = (ImageView) itemView.findViewById(R.id.imageView1);
            mItemTestText = (TextView) itemView.findViewById(R.id.test_text);
            mItemColorCode = (ImageView) itemView.findViewById(R.id.color_code);
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

        public void setColorCode(int color) {
            mItemColorCode.setBackgroundColor(color);
        }
    }
}
