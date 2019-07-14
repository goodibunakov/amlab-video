package ru.goodibunakov.amlabvideo.adapter;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.internal.ViewHelper;
import ru.goodibunakov.amlabvideo.R;
import ru.goodibunakov.amlabvideo.utils.Utils;

public class AdapterList extends UltimateViewAdapter<AdapterList.ViewHolder> {

    private final List<HashMap<String, String>> data;
    private Interpolator interpolator = new LinearInterpolator();
    private int lastPosition = 5;
    private final int ANIMATION_DURATION = 300;
    private Context context;

    public AdapterList(Context context, List<HashMap<String, String>> list) {
        data = list;
    }

    @Override
    public ViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public ViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View itemView = layoutInflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getAdapterItemCount() {
        return data.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position < getItemCount() && (customHeaderView != null ? position <= data.size() :
                position < data.size()) && (customHeaderView == null || position > 0)) {
            HashMap<String, String> item;
            item = data.get(customHeaderView != null ? position - 1 : position);
            // Set data to the view
            holder.bind(item);
        }

        boolean isFirstOnly = true;
        if (!isFirstOnly || position > lastPosition) {
            // Add animation to the item
            for (Animator anim : getAdapterAnimations(holder.itemView,
                    AdapterAnimationType.SlideInLeft)) {
                anim.setDuration(ANIMATION_DURATION).start();
                anim.setInterpolator(interpolator);
            }
            lastPosition = position;
        } else {
            ViewHelper.clear(holder.itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class ViewHolder extends UltimateRecyclerviewViewHolder {

        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtPublishedAt)
        TextView txtPublished;
        @BindView(R.id.txtDuration)
        TextView txtDuration;
        @BindView(R.id.imgThumbnail)
        ImageView ivThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final HashMap<String, String> item) {
            txtTitle.setText(item.get(Utils.KEY_TITLE));
            txtDuration.setText(item.get(Utils.KEY_DURATION));
            txtPublished.setText(item.get(Utils.KEY_PUBLISHEDAT));

            Glide
                    .with(ivThumbnail.getContext())
                    .load(item.get(Utils.KEY_URL_THUMBNAILS))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.empty_photo)
                    .into(ivThumbnail);
        }
    }
}
