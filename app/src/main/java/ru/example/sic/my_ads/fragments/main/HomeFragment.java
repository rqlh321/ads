package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SplashActivity;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.fragments.view.DetailFragment;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;
import static ru.example.sic.my_ads.Constants.LANGUAGE;
import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CREATED_AT;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_IS_RECOMMENDED_BY_ADMIN;
import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_ACTION_IMAGE;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_ACTIVE;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_EN_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_RU_TITLE;

public class HomeFragment extends Fragment {
    @BindView(R.id.homeImage)
    AppCompatImageView bannerHolder;

    @BindView(R.id.linearRecommended)
    LinearLayout linearRecommended;

    @BindView(R.id.linearLastAds)
    LinearLayout linearLastAds;

    @OnClick(R.id.logout)
    public void logout() {
        ParseUser.logOut();
        startActivity(new Intent(getContext(), SplashActivity.class));
        getActivity().finish();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        getBanners();
        getRecommended();
        getLast();

        return view;
    }

    private void getBanners() {
        ParseQuery<ParseObject> queryRow = ParseQuery.getQuery(PROMO_ACTIONS);
        queryRow.whereEqualTo(PROMO_ACTIONS_ACTIVE, true);
        queryRow.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    addBanners(results);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getRecommended() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AD);
        query.whereEqualTo(AD_IS_RECOMMENDED_BY_ADMIN, true);
        query.setLimit(10);
        query.orderByDescending(AD_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    addAds(results, linearRecommended);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getLast() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AD);
        query.orderByDescending(AD_CREATED_AT);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    addAds(results, linearLastAds);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addBanners(List<ParseObject> parseBanners) {
        AnimationDrawable animation = new AnimationDrawable();
        LayoutInflater ltInflater = getActivity().getLayoutInflater();
        FrameLayout frameLayout = (FrameLayout) ltInflater.inflate(R.layout.baner, null);
        ImageView imageView = (ImageView) frameLayout.findViewById(R.id.image);
        TextView textView = (TextView) frameLayout.findViewById(R.id.text);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "cassandra.ttf"));
        for (ParseObject banner : parseBanners) {
            try {
                ParseFile pic = banner.getParseFile(PROMO_ACTIONS_ACTION_IMAGE);
                if (pic != null) {
                    //view flooding
                    byte[] dataPic = pic.getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(dataPic, 0, dataPic.length);
                    imageView.setImageBitmap(bitmap);
                    textView.setText(banner.getString(LANGUAGE.equals("ru") ? PROMO_ACTIONS_RU_TITLE : PROMO_ACTIONS_EN_TITLE));
                    //creation bitmap from view
                    frameLayout.setDrawingCacheEnabled(true);
                    frameLayout.measure(FrameLayout.MeasureSpec.makeMeasureSpec(0, FrameLayout.MeasureSpec.UNSPECIFIED), FrameLayout.MeasureSpec.makeMeasureSpec(0, FrameLayout.MeasureSpec.UNSPECIFIED));
                    frameLayout.layout(0, 0, frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight());
                    frameLayout.buildDrawingCache(true);
                    Bitmap b = Bitmap.createBitmap(frameLayout.getDrawingCache());
                    frameLayout.setDrawingCacheEnabled(false);
                    //add to animation
                    Drawable drawable = new BitmapDrawable(getResources(), b);
                    animation.addFrame(drawable, 5000);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        animation.setOneShot(false);
        bannerHolder.setImageDrawable(animation);
        animation.start();
    }

    private void addAds(final List<ParseObject> ads, final LinearLayout layout) {
        layout.removeAllViews();
        for (final ParseObject ad : ads) {
            LayoutInflater ltInflater = getActivity().getLayoutInflater();
            View preview = ltInflater.inflate(R.layout.preview_ad, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(290, 200);
            preview.setLayoutParams(layoutParams);
            ImageView pic = (ImageView) preview.findViewById(R.id.pic);

            if (ad.getParseFile(AD_PHOTO) != null) {
                Glide.with(getActivity())
                        .load(ad.getParseFile(AD_PHOTO).getUrl())
                        .dontAnimate()
                        .centerCrop()
                        .into(pic);
            }
            TextView cost = (TextView) preview.findViewById(R.id.cost);
            cost.setText(ad.getInt(AD_COST) + " " + ad.getString(AD_CURRENCY));
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), SupportActivity.class);
                    intent.putExtra(EXTRA_SHAPE, DetailFragment.TAG);
                    intent.putExtra("id", ad.getObjectId());
                    startActivity(intent);
                }
            });
            layout.addView(preview);
        }
    }
}