package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import icepick.Icepick;
import icepick.State;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SplashActivity;
import ru.example.sic.my_ads.adapters.PreviewAdsAdapter;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.PromoAction;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.LANGUAGE;

public class HomeFragment extends Fragment {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.TITLE, Ad.PHOTO, Ad.COST, Ad.CURRENCY, Ad.AUTHOR_ID};

    private FrameLayout frameLayout;
    private PreviewAdsAdapter lastAdapter;
    private PreviewAdsAdapter recommendedAdapter;

    @State
    ArrayList<Ad> recommended;
    @State
    ArrayList<Ad> last;
    @State
    ArrayList<PromoAction> promoActions;

    @BindView(R.id.homeImage)
    public AppCompatImageView bannerHolder;

    @BindView(R.id.lastAds)
    public RecyclerView lastAdsList;

    @BindView(R.id.recommendedAds)
    public RecyclerView recommendedAdsList;

    @BindView(R.id.actions_progress)
    public ProgressBar actionsProgress;

    @BindView(R.id.last_progress)
    public ProgressBar lastProgress;

    @BindView(R.id.recommended_progress)
    public ProgressBar recommendedProgress;

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
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            frameLayout = (FrameLayout) inflater.inflate(R.layout.baner, null);

            recommended = new ArrayList<>();
            LinearLayoutManager linearLayoutManager0 = new LinearLayoutManager(getContext());
            linearLayoutManager0.setOrientation(LinearLayoutManager.HORIZONTAL);
            recommendedAdsList.setLayoutManager(linearLayoutManager0);
            recommendedAdapter = new PreviewAdsAdapter(getContext(), recommended);
            recommendedAdsList.setAdapter(recommendedAdapter);
            getRecommended();

            last = new ArrayList<>();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            lastAdsList.setLayoutManager(linearLayoutManager);
            lastAdapter = new PreviewAdsAdapter(getContext(), last);
            lastAdsList.setAdapter(lastAdapter);
            getLast();

            promoActions = new ArrayList<>();
            getBanners();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void getBanners() {
        actionsProgress.setVisibility(View.VISIBLE);
        Observable.just(null)
                .map(new Func1<Object, ArrayList<PromoAction>>() {
                    @Override
                    public ArrayList<PromoAction> call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(PromoAction.class.getSimpleName())
                                    .find();
                            ArrayList<PromoAction> list = new ArrayList<>();
                            for (ParseObject parseObject : results) {
                                list.add(new PromoAction(parseObject));
                            }
                            return list;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<PromoAction>>() {
                    @Override
                    public void onCompleted() {
                        actionsProgress.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<PromoAction> result) {
                        if (result != null) {
                            promoActions.clear();
                            promoActions.addAll(result);
                            addBanners();
                        }
                    }
                });
    }

    private void getRecommended() {
        recommendedProgress.setVisibility(View.VISIBLE);
        Observable.just(null)
                .map(new Func1<Object, ArrayList<Ad>>() {
                    @Override
                    public ArrayList<Ad> call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(Ad.class.getSimpleName())
                                    .whereEqualTo(Ad.RECOMMENDED, true)
                                    .setLimit(10)
                                    .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS)))
                                    .orderByDescending(Ad.CREATED_AT)
                                    .find();
                            ArrayList<Ad> list = new ArrayList<>();
                            for (ParseObject parseObject : results) {
                                list.add(new Ad(parseObject));
                            }
                            return list;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Ad>>() {
                    @Override
                    public void onCompleted() {
                        recommendedProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(ArrayList<Ad> result) {
                        if (result != null) {
                            recommended.clear();
                            recommended.addAll(result);
                            recommendedAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getLast() {
        lastProgress.setVisibility(View.VISIBLE);
        Observable.just(null)
                .map(new Func1<Object, ArrayList<Ad>>() {
                    @Override
                    public ArrayList<Ad> call(Object o) {
                        try {
                            List<ParseObject> results = ParseQuery.getQuery(Ad.class.getSimpleName())
                                    .orderByDescending(Ad.CREATED_AT)
                                    .setLimit(10)
                                    .find();
                            ArrayList<Ad> list = new ArrayList<>();
                            for (ParseObject parseObject : results) {
                                list.add(new Ad(parseObject));
                            }
                            return list;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Ad>>() {
                    @Override
                    public void onCompleted() {
                        lastProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(ArrayList<Ad> result) {
                        if (result != null) {
                            last.clear();
                            last.addAll(result);
                            lastAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void addBanners() {
        AnimationDrawable animation = new AnimationDrawable();
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

}