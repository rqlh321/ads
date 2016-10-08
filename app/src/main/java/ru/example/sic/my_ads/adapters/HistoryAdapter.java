package ru.example.sic.my_ads.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.view.DetailRootFragment;

import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<ParseObject> list;
    private ArrayList<String> historyCount;
    private Fragment fragment;

    public HistoryAdapter(Fragment fragment, ArrayList<ParseObject> list, ArrayList<String> historyCount) {
        this.fragment = fragment;
        this.historyCount = historyCount;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryAdapter.ViewHolder holder, final int position) {
        holder.mTitle.setText(list.get(position).getString(AD_TITLE));
        int count = 0;
        for (String itemId : historyCount) {
            if (list.get(position).getObjectId().equals(itemId)) {
                count++;
            }
        }
        holder.mCount.setText(String.valueOf(count));
        if (list.get(position).getParseFile(AD_PHOTO) != null) {
            Glide.with(holder.mImageView.getContext())
                    .load(list.get(position).getParseFile(AD_PHOTO).getUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.mImageView);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailRootFragment detailRootFragment = new DetailRootFragment();
                detailRootFragment.ad = list.get(position);
                detailRootFragment.ads = list;
                fragment.getFragmentManager().beginTransaction()
                        .replace(R.id.support_container, detailRootFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Parse.Data.history.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitle;
        public final TextView mCount;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTitle = (TextView) view.findViewById(R.id.text1);
            mCount = (TextView) view.findViewById(R.id.count);
        }


    }
}

