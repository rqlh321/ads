package ru.example.sic.my_ads.fragments.main.favorite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.FavoritesAdapter;
import ru.example.sic.my_ads.fragments.MapFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Parse.Constants.USER_FAVORITES;

public class FavoritesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.empty_favorite)
    LinearLayout emptyHint;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.show_on_map)
    FloatingActionButton showOnMap;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private FavoritesAdapter adapter;

    @OnClick(R.id.show_on_map)
    void onFabClick() {
        Parse.Data.map = Parse.Data.favorite;
        getFragmentManager().beginTransaction()
                .hide(this)
                .add(R.id.container_favorite, new MapFragment())
                .addToBackStack(null)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FavoritesAdapter(this, Parse.Data.favorite);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (Parse.Data.favorite.size() == 0) {
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
        Parse.Data.favorite.clear();
        adapter.notifyDataSetChanged();
        getAdsList();
    }

    private void getAdsList() {
        Observable.just(true)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean b) {
                        return Parse.Request.getObjectRelationUser(USER_FAVORITES);
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
                        if (objects.size() > 0) {
                            showOnMap.setVisibility(View.VISIBLE);
                            emptyHint.setVisibility(View.GONE);

                        } else {
                            emptyHint.setVisibility(View.VISIBLE);
                            showOnMap.setVisibility(View.GONE);
                        }
                        Parse.Data.favorite.addAll(objects);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
