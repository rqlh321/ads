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
import com.parse.ParseQuery;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ExpandableRecyclerViewAdapter;
import ru.example.sic.my_ads.models.Catalog;
import ru.example.sic.my_ads.models.Category;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Parse.Constants.CATEGORY;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_EN_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_IS_ROOT;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_PARENT;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_RU_TITLE;
import static ru.example.sic.my_ads.Constants.LANGUAGE;

public class CatalogFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ExpandableRecyclerViewAdapter adapter;
    @BindView(R.id.expendeble_recycler_view)
    RecyclerView expandableRecyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, view);
        addCatalog(Parse.Data.categoryList);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        if(Parse.Data.categoryList.size()==0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        }
        return view;
    }

    public void getCategory() {
        Observable.just(LANGUAGE)
                .map(new Func1<String, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(String language) {
                        try {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(CATEGORY);
                            query.orderByDescending(CATEGORY_IS_ROOT);
                            return (ArrayList<ParseObject>) query.find();
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                        return new ArrayList<>();
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
                        Parse.Data.categoryList.addAll(objects);
                        addCatalog(objects);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void addCatalog(ArrayList<ParseObject> objects) {
        ArrayList<Catalog> catalogs = compileCatalogs(objects);
        adapter = new ExpandableRecyclerViewAdapter(CatalogFragment.this, catalogs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        expandableRecyclerView.setLayoutManager(linearLayoutManager);
        expandableRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Catalog> compileCatalogs(ArrayList<ParseObject> objects) {
        ArrayList<Catalog> catalogs = new ArrayList<>();
        for (ParseObject object : objects) {
            if (object.getBoolean(CATEGORY_IS_ROOT)) {
                String ENTitle = object.getString(CATEGORY_EN_TITLE);
                String RUTitle = object.getString(CATEGORY_RU_TITLE);
                ArrayList<Category> subCatalog = new ArrayList<>();
                Catalog catalog = new Catalog(new Category(ENTitle, RUTitle), subCatalog);
                catalogs.add(catalog);
            } else {
                String ENTitle = object.getString(CATEGORY_EN_TITLE);
                String RUTitle = object.getString(CATEGORY_RU_TITLE);
                for (Catalog catalog : catalogs) {
                    String parent = object.getString(CATEGORY_PARENT);
                    if (catalog.getCategory().getEn().equals(parent)) {
                        catalog.getChildItemList().add(new Category(ENTitle, RUTitle));
                        break;
                    }
                }
            }
        }
        return catalogs;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        Parse.Data.categoryList.clear();
        getCategory();
    }
}
