package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;
import ru.example.sic.my_ads.activity.SplashActivity;
import ru.example.sic.my_ads.activity.ViewActivity;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.PromoAction;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.LANGUAGE;
import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.AD_CREATED_AT;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_ACTIVE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.TITLE, Ad.PHOTO, Ad.COST, Ad.CURRENCY, Ad.AUTHOR_ID};
    private ArrayList<Ad> recommended;
    private ArrayList<Ad> last;
    private ArrayList<PromoAction> promoActions;
    @BindView(R.id.homeImage)
    public AppCompatImageView bannerHolder;

    @BindView(R.id.linearRecommended)
    public LinearLayout linearRecommended;

    @BindView(R.id.linearLastAds)
    public LinearLayout linearLastAds;

    @BindView(R.id.refresh)
    public SwipeRefreshLayout refresh;

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
        refresh.setOnRefreshListener(this);

        recommended = ((MainActivity) getActivity()).recommended;
        if (recommended.size() != 0) {
            addAds(recommended, linearRecommended);
        } else {
            getRecommended();
        }

        last = ((MainActivity) getActivity()).last;
        if (last.size() != 0) {
            addAds(last, linearLastAds);
        } else {
            getLast();
        }

        promoActions = ((MainActivity) getActivity()).promoActions;
        if (promoActions.size() != 0) {
            addBanners();
        } else {
            getBanners();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        getBanners();
        getRecommended();
        getLast();
    }

    private void getBanners() {
        refresh.setRefreshing(true);
        Observable.just(null)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(PROMO_ACTIONS)
                                    .whereEqualTo(PROMO_ACTIONS_ACTIVE, true)
                                    .find();
                            promoActions.clear();
                            for (ParseObject parseObject : results) {
                                promoActions.add(new PromoAction(parseObject));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        refresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(Object o) {
                        addBanners();
                    }
                });
    }

    private void getRecommended() {
        refresh.setRefreshing(true);
        Observable.just(null)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(AD)
                                    .whereEqualTo(Ad.RECOMMENDED, true)
                                    .setLimit(10)
                                    .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS)))
                                    .orderByDescending(AD_CREATED_AT)
                                    .find();
                            recommended.clear();
                            for (ParseObject parseObject : results) {
                                recommended.add(new Ad(parseObject));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(Object o) {
                        addAds(recommended, linearRecommended);
                    }
                });
    }

    private void getLast() {
        refresh.setRefreshing(true);
        Observable.just(null)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(AD)
                                    .orderByDescending(AD_CREATED_AT)
                                    .setLimit(10)
                                    .find();
                            last.clear();
                            for (ParseObject parseObject : results) {
                                last.add(new Ad(parseObject));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        refresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(Object o) {
                        addAds(last, linearLastAds);
                    }
                });
    }

    private void addBanners() {
        AnimationDrawable animation = new AnimationDrawable();
        LayoutInflater ltInflater = getActivity().getLayoutInflater();
        FrameLayout frameLayout = (FrameLayout) ltInflater.inflate(R.layout.baner, null);
        ImageView imageView = (ImageView) frameLayout.findViewById(R.id.image);
        TextView textView = (TextView) frameLayout.findViewById(R.id.text);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "cassandra.ttf"));
        for (PromoAction banner : promoActions) {
            //view flooding
            imageView.setImageBitmap(banner.bitmap);
            textView.setText(LANGUAGE.equals("ru") ? banner.ru : banner.en);
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
        animation.setOneShot(false);
        bannerHolder.setImageDrawable(animation);
        animation.start();
    }

    private void addAds(final List<Ad> ads, final LinearLayout layout) {
        layout.removeAllViews();
        for (final Ad ad : ads) {
            LayoutInflater ltInflater = getActivity().getLayoutInflater();
            View preview = ltInflater.inflate(R.layout.preview_ad, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(290, 200);
            preview.setLayoutParams(layoutParams);
            ImageView pic = (ImageView) preview.findViewById(R.id.pic);
            Glide.with(getActivity())
                    .load(ad.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(pic);
            TextView cost = (TextView) preview.findViewById(R.id.cost);
            cost.setText(ad.cost + " " + ad.currency);
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ViewActivity.class);
                    intent.putExtra("ad", ad);
                    startActivity(intent);
                }
            });
            layout.addView(preview);
        }
    }

}