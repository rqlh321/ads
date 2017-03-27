package ru.example.sic.my_ads.fragments.main.catalog;

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
import ru.example.sic.my_ads.EndlessRecyclerOnScrollListener;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ListAdsAdapter;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.Category;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.EXTRA_IS_CATEGORY;
import static ru.example.sic.my_ads.Constants.EXTRA_SELECTED_CATEGORY;

public class ListAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Category category;
    private boolean isCategory;
    private ListAdsAdapter adapter;
    private ArrayList<ParseObject> subcategoryObjects = new ArrayList<>();
    private ArrayList<Ad> categoryAds;

    @OnClick(R.id.show_on_map)
    void onMapClick() {
        getFragmentManager().beginTransaction()
                .hide(this)
                .add(R.id.container_catalog, new MapFragment())
                .addToBackStack(null)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_ads, container, false);
        ButterKnife.bind(this, view);
        category = (Category) getArguments().getSerializable(EXTRA_SELECTED_CATEGORY);
        isCategory = getArguments().getBoolean(EXTRA_IS_CATEGORY, false);

//        categoryAds = ((MainActivity) getActivity()).categoryAds;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListAdsAdapter(this, categoryAds, R.id.container_catalog);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                getAdsList();
            }
        });
        return view;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        categoryAds.clear();
        adapter.notifyDataSetChanged();
        getAdsList();
    }

    private void getAdsList() {
        Observable.just(categoryAds.size())
                .map(new Func1<Integer, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Integer integer) {
                        if (subcategoryObjects.size() == 0) {
                            subcategoryObjects = Parse.Request.getSubGroupsList(category.en, isCategory);
                        }
                        return Parse.Request.getAdsBySubcategorys(subcategoryObjects, integer);

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
//                        categoryAds.addAll(objects);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}
