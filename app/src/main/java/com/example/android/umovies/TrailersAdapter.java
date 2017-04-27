package com.example.android.umovies;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersRVHolder> {
    private static final String YOUTUBE_THUMBNAIL_PREFIX_PATH = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_POSTFIX_PATH = "/0.jpg";
    private ItemClickListener itemClickListener;
    private List<String> trailerKeys;
    private List<String> trailerNames;
    private Context context;

    public TrailersAdapter(Context context, List<String> trailerKeys, List<String> trailerNames, ItemClickListener itemClickListener) {
        this.context = context;
        this.trailerKeys = trailerKeys;
        this.trailerNames = trailerNames;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public TrailersRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.trailers_item, parent, false);
        TrailersRVHolder viewHolder = new TrailersRVHolder(movieView, itemClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TrailersRVHolder holder, int position) {
        if(trailerNames.get(position) != null) {
            holder.trailerName.setText(trailerNames.get(position));
        }

        final String img_url = YOUTUBE_THUMBNAIL_PREFIX_PATH +
                trailerKeys.get(position) +
                YOUTUBE_THUMBNAIL_POSTFIX_PATH;
        holder.trailerThumbnail.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(Build.VERSION.SDK_INT >= 16)
                        holder.trailerThumbnail.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);

                        Picasso.with(context)
                                .load(img_url)
                                .resize(holder.trailerThumbnail.getWidth(), 0)
                                .placeholder(R.mipmap.movie_placeholder)
                                .into(holder.trailerThumbnail);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if(trailerKeys == null)
            return 0;
        return trailerKeys.size();
    }
}
