package ru.example.sic.my_ads.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.fragments.view.DetailFragment;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;
import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;

public class ListAdsAdapter extends RecyclerView.Adapter<ListAdsAdapter.ViewHolder> {

    private ArrayList<ParseObject> adList;
    private Fragment fragment;
    int containerId;

    public ListAdsAdapter(Fragment fragment, ArrayList<ParseObject> list, int containerId) {
        this.adList = list;
        this.fragment = fragment;
        this.containerId = containerId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (adList.get(position).getParseFile(AD_PHOTO) != null) {
            Glide.with(holder.mImageView.getContext())
                    .load(adList.get(position).getParseFile(AD_PHOTO).getUrl())
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.mImageView);
        }
        holder.mLocation.setText(adList.get(position).getString(AD_ADDRESS));
        String cost = adList.get(position).getNumber(AD_COST).toString() + adList.get(position).getString(AD_CURRENCY);
        holder.mTextViewCost.setText(cost);
        holder.mTextView.setText(adList.get(position).getString(AD_TITLE));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(), SupportActivity.class);
                intent.putExtra(EXTRA_SHAPE, DetailFragment.TAG);
                intent.putExtra("id", adList.get(position).getObjectId());
                fragment.startActivity(intent);
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