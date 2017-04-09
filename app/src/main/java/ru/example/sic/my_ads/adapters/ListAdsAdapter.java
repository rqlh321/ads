package ru.example.sic.my_ads.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.ViewActivity;
import ru.example.sic.my_ads.models.Ad;

public class ListAdsAdapter extends RecyclerView.Adapter<ListAdsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Ad> adList;

    public ListAdsAdapter(Context context, ArrayList<Ad> list) {
        this.adList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context)
                .load(adList.get(position).photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.mImageView);
        holder.mLocation.setText(adList.get(position).address);
        String cost = adList.get(position).cost + " " + adList.get(position).currency;
        holder.mTextViewCost.setText(cost);
        holder.mTextView.setText(adList.get(position).title);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra("ad", adList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageButton mDeleteImageView;
        public final ImageView mImageView;
        public final TextView mTextView;
        public final TextView mTextViewCost;
        public final TextView mLocation;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(R.id.text1);
            mLocation = (TextView) view.findViewById(R.id.location);
            mDeleteImageView = (ImageButton) view.findViewById(R.id.delete);
            mTextViewCost = (TextView) view.findViewById(R.id.cost);
        }

    }
}