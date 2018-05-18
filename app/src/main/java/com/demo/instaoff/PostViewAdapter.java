package com.demo.instaoff;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    public RecentPost[] recentPosts;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView caption,time,likeCount,commentCount;
        public ImageView mainImage;
        @BindView(R.id.likesImage)
        public ImageView likeImage;
        @BindView(R.id.commentsImage)
        public ImageView commentsImage;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            caption = (TextView)view.findViewById(R.id.caption);
            time = (TextView)view.findViewById(R.id.time);
            likeCount = (TextView)view.findViewById(R.id.likeCount);
            commentCount = (TextView)view.findViewById(R.id.commentCount);
            mainImage = (ImageView) view.findViewById(R.id.mainImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostViewAdapter(RecentPost[] myDataset, RequestManager glide) {
        this.recentPosts = myDataset;
        this.glide = glide;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_view_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    private final RequestManager glide;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.caption.setText(recentPosts[position].caption);
        holder.time.setText(recentPosts[position].time);
        holder.likeCount.setText(recentPosts[position].likeCount);
        holder.commentCount.setText(recentPosts[position].commentCount);

        glide.load(recentPosts[position].imageURL).into(holder.mainImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recentPosts.length;
    }
}