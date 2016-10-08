package ru.example.sic.my_ads.fragments.main.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;
import ru.example.sic.my_ads.fragments.view.DetailRootFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.LANGUAGE;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_ACTION_IMAGE;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_EN_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_RU_TITLE;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.signIn)
    Button singIn;
    @BindView(R.id.homeImage)
    AppCompatImageView bannerHolder;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.linearRecommended)
    LinearLayout linearRecommended;
    @BindView(R.id.linearLastAds)
    LinearLayout linearLastAds;

    @OnClick(R.id.signIn)
    void onSignInClick() {
        MainActivity.logIn(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        addBanners(Parse.Data.banners);
        addAds(Parse.Data.recommended, linearRecommended);
        addAds(Parse.Data.last, linearLastAds);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (Parse.Data.recommended.size() == 0 || Parse.Data.last.size() == 0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Parse.Data.currentUser != null) {
            singIn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        getBanners();
        getRecommended();
        getLast();
    }

    private void getBanners() {
        Observable.just(LANGUAGE)
                .map(new Func1<String, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(String lang) {
                        return Parse.Request.getBanners();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ArrayList<ParseObject> parseBanners) {
                        Parse.Data.banners = parseBanners;
                        addBanners(Parse.Data.banners);
                    }
                });
    }

    private void getRecommended() {
        Observable.just(10)
                .map(new Func1<Integer, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Integer integer) {
                        return Parse.Request.getRecommended(integer);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ParseObject> result) {
                        Parse.Data.recommended = result;
                        addAds(Parse.Data.recommended, linearRecommended);
                    }
                });
    }

    private void getLast() {
        Observable.just(10)
                .map(new Func1<Integer, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Integer integer) {
                        return Parse.Request.getAllAds(integer);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<ParseObject> result) {
                        Parse.Data.last = result;
                        addAds(Parse.Data.last, linearLastAds);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addBanners(ArrayList<ParseObject> parseBanners) {
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
                    textView.setText(banner.getString(LANGUAGE.equals("en") ? PROMO_ACTIONS_EN_TITLE : PROMO_ACTIONS_RU_TITLE));
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
        bannerHolder.setBackgroundDrawable(animation);
        animation.start();
    }

    private void addAds(final ArrayList<ParseObject> ads, final LinearLayout layout) {
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
                    DetailRootFragment detailRootFragment = new DetailRootFragment();
                    detailRootFragment.ad = ad;
                    detailRootFragment.ads = ads;
                    getFragmentManager().beginTransaction()
                            .hide(HomeFragment.this)
                            .add(R.id.container_home, detailRootFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            layout.addView(preview);
        }
    }
}