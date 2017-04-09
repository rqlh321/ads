package ru.example.sic.my_ads.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.EndlessRecyclerOnScrollListener;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ListAdsAdapter;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.Category;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.EXTRA_SELECTED_CATEGORY;

public class ListAdsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;


    private Category category;
    private ListAdsAdapter adapter;
    private ArrayList<Ad> categoryAds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ads);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            categoryAds = savedInstanceState.getParcelableArrayList("categoryAds");
        } else {
            categoryAds = new ArrayList<>();
        }

        category = (Category) getIntent().getSerializableExtra(EXTRA_SELECTED_CATEGORY);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ab.setTitle(Constants.LANGUAGE.equals("ru") ? category.ru : category.en);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListAdsAdapter(this, categoryAds);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                getAdsList();
            }
        });
        onRefresh();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("categoryAds", categoryAds);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void onRefresh() {
        categoryAds.clear();
        adapter.notifyDataSetChanged();
        getAdsList();
    }

    private void getAdsList() {
        Observable.just(categoryAds.size())
                .map(new Func1<Integer, ArrayList<Ad>>() {
                    @Override
                    public ArrayList<Ad> call(Integer integer) {
                        try {
                            List<ParseObject> list = ParseQuery.getQuery(Ad.class.getSimpleName())
                                    .whereEqualTo(Ad.SUBCATEGORY, category.id)
                                    .orderByDescending(Ad.CREATED_AT)
                                    .setSkip(categoryAds.size())
                                    .setLimit(10)
                                    .find();
                            ArrayList<Ad> result = new ArrayList<>();
                            for (ParseObject parseObject : list) {
                                result.add(new Ad(parseObject));
                            }
                            return result;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Ad>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ArrayList<Ad> objects) {
                        categoryAds.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
