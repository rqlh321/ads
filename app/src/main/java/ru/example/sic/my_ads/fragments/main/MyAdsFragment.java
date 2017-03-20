package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.adapters.MyAdsAdapter;
import ru.example.sic.my_ads.fragments.CreateAdFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_ADS_OF_USER;

public class MyAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    MyAdsAdapter adapter;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.add_ad)
    void createAd() {
        Intent intent = new Intent(getContext(), SupportActivity.class);
        intent.putExtra(EXTRA_SHAPE, CreateAdFragment.TAG);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyAdsAdapter(this, Parse.Data.my);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (Parse.Data.my.size() == 0) {
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
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        Parse.Data.my.clear();
        adapter.notifyDataSetChanged();
        getAdsList();
    }

    private void getAdsList() {
        Observable.just(false)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean b) {
                        return Parse.Request.getObjectRelationUser(USER_ADS_OF_USER);
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
                    public void onNext(final ArrayList<ParseObject> objects) {
                        Parse.Data.my.addAll(objects);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
