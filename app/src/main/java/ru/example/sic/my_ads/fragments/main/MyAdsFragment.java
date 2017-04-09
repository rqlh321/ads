package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import ru.example.sic.my_ads.activity.CreateAdActivity;
import ru.example.sic.my_ads.adapters.MyAdsAdapter;
import ru.example.sic.my_ads.models.Ad;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MyAdsFragment extends Fragment {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.TITLE, Ad.PHOTO, Ad.COST, Ad.CURRENCY, Ad.AUTHOR_ID};
    private MyAdsAdapter adapter;

    @State
    protected ArrayList<Ad> my;

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;

    @BindView(R.id.progress)
    protected ProgressBar progress;

    @OnClick(R.id.add_ad)
    void createAd() {
        Intent intent = new Intent(getContext(), CreateAdActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        ButterKnife.bind(this, view);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState == null) {
            my = new ArrayList<>();
            onRefresh();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new MyAdsAdapter(this, my);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void onRefresh() {
        progress.setVisibility(View.VISIBLE);
        Observable.just(null)
                .map(new Func1<Object, List<ParseObject>>() {
                    @Override
                    public List<ParseObject> call(Object o) {
                        try {
                            return ParseQuery.getQuery(Ad.class.getSimpleName())
                                    .whereEqualTo(Ad.AUTHOR_ID, ParseUser.getCurrentUser().getObjectId())
                                    .orderByDescending(Ad.CREATED_AT)
                                    .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS))).find();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ParseObject>>() {
                    @Override
                    public void onCompleted() {
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<ParseObject> results) {
                        if (results != null) {
                            my.clear();
                            for (ParseObject parseObject : results) {
                                my.add(new Ad(parseObject));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
