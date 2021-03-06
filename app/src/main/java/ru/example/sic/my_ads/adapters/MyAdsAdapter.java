package ru.example.sic.my_ads.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.main.my.MyAdsFragment;
import ru.example.sic.my_ads.fragments.view.DetailRootFragment;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    private ArrayList<ParseObject> ads;
    private MyAdsFragment fragment;

    public MyAdsAdapter(MyAdsFragment fragment, ArrayList<ParseObject> ads) {
        this.ads = ads;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_delete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdsAdapter.ViewHolder holder, final int position) {
        if (ads.get(position).getParseFile(Parse.Constants.AD_PHOTO) != null) {
            Glide.with(holder.mImageView.getContext())
                    .load(ads.get(position).getParseFile(Parse.Constants.AD_PHOTO).getUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.mImageView);
        }
        holder.mTextView.setText(ads.get(position).getString(Parse.Constants.AD_TITLE));
        holder.mDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle(ads.get(position).getString(Parse.Constants.AD_TITLE));
                TextView tv = new TextView(fragment.getContext());
                tv.setText(fragment.getString(R.string.alert_re_ask_your));
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(20, 50, 20, 50); // llp.setMargins(left, top, right, bottom);
                tv.setTextSize(18);
                tv.setLayoutParams(llp);
                LinearLayout layout = new LinearLayout(fragment.getContext());
                layout.addView(tv);
                alert.setView(layout);
                alert.setPositiveButton(fragment.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Parse.Request.deleteAd(ads.get(position).getObjectId());
                        ads.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton(fragment.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailRootFragment detailRootFragment = new DetailRootFragment();
                detailRootFragment.ad = Parse.Data.my.get(position);
                detailRootFragment.ads = Parse.Data.my;
                fragment.getFragmentManager().beginTransaction()
                        .hide(fragment)
                        .add(R.id.container_my_ads, detailRootFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageButton mDeleteImageView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(R.id.text1);
            mDeleteImageView = (ImageButton) view.findViewById(R.id.delete);
        }
    }
}