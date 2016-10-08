package ru.example.sic.my_ads.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.view.DetailRootFragment;

import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.USER_FAVORITES;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    ArrayList<ParseObject> listParse;
    Fragment fragment;

    public FavoritesAdapter(Fragment fragment, ArrayList<ParseObject> list) {
        this.listParse = list;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expended_favorites_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String url;
        if (listParse.get(position).getParseFile(AD_PHOTO) != null) {
            url = listParse.get(position).getParseFile(AD_PHOTO).getUrl();
            Glide.with(holder.mView.getContext())
                    .load(url)
                    .dontAnimate()
                    .fitCenter()
                    .into(holder.picture);
        }
        final String name = listParse.get(position).getString(AD_TITLE);
        String costValue = listParse.get(position).getNumber(AD_COST).toString() + " "
                + listParse.get(position).getString(AD_CURRENCY);
        holder.cost.setText(costValue);
        holder.location.setText(listParse.get(position).getString(AD_ADDRESS));
        holder.title.setText(name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailRootFragment detailRootFragment = new DetailRootFragment();
                detailRootFragment.ad = listParse.get(position);
                detailRootFragment.ads = listParse;
                fragment.getFragmentManager()
                        .beginTransaction()
                        .hide(fragment)
                        .add(R.id.container_favorite, detailRootFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle(listParse.get(position).getString(Parse.Constants.AD_TITLE));
                TextView tv = new TextView(fragment.getContext());
                tv.setText(fragment.getString(R.string.alert_re_ask_favorite));
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
                        notifyDataSetChanged();
                        ParseRelation<ParseObject> favorites = ParseUser.getCurrentUser().getRelation(USER_FAVORITES);
                        favorites.remove(listParse.get(position));
                        listParse.remove(position);
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                });
                alert.setNegativeButton(fragment.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listParse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView picture;
        ImageView remove;
        TextView cost;
        TextView title;
        TextView location;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            picture = (ImageView) mView.findViewById(R.id.avatar);
            remove = (ImageView) mView.findViewById(R.id.remove);
            cost = (TextView) mView.findViewById(R.id.cost);
            title = (TextView) mView.findViewById(R.id.title);
            location = (TextView) mView.findViewById(R.id.location);

        }

    }

}