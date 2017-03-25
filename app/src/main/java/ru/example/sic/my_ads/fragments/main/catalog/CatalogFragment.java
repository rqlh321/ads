package ru.example.sic.my_ads.fragments.main.catalog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

import static ru.example.sic.my_ads.Constants.LANGUAGE;

public class CatalogFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.expendeble_recycler_view)
    public RecyclerView expandableRecyclerView;

    @BindView(R.id.refresh)
    public SwipeRefreshLayout refresh;

    @OnClick(R.id.add)
    public void addCategory() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getContext());
        final EditText en = new EditText(getContext());
        linearLayout.addView(en);
        final EditText ru = new EditText(getContext());
        linearLayout.addView(ru);

        builder.setView(linearLayout)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseObject category = new ParseObject(Catalog.class.getSimpleName());
                        category.put(Catalog.EN_TITLE, en.getText().toString());
                        category.put(Catalog.RU_TITLE, ru.getText().toString());
                        category.put(Catalog.PARENT, "");
                        category.saveInBackground();
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, view);
        addCatalog(Parse.Data.categoryList);
        refresh.setOnRefreshListener(this);
        if (Parse.Data.categoryList.size() == 0) {
            refresh.post(new Runnable() {
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
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(Catalog.class.getSimpleName());
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
                        refresh.setRefreshing(false);
                    }
                });
    }

    private void addCatalog(ArrayList<ParseObject> objects) {
        ArrayList<Catalog> catalogs = compileCatalogs(objects);
        ExpandableRecyclerViewAdapter adapter = new ExpandableRecyclerViewAdapter(CatalogFragment.this, catalogs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        expandableRecyclerView.setLayoutManager(linearLayoutManager);
        expandableRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Catalog> compileCatalogs(ArrayList<ParseObject> objects) {
        ArrayList<Catalog> catalogs = new ArrayList<>();
        for (ParseObject object : objects) {
            if (object.getString(Catalog.PARENT).isEmpty()) {
                String ENTitle = object.getString(Catalog.EN_TITLE);
                String RUTitle = object.getString(Catalog.RU_TITLE);
                ArrayList<Category> subCatalog = new ArrayList<>();
                Catalog catalog = new Catalog(new Category(ENTitle, RUTitle), subCatalog);
                catalogs.add(catalog);
            } else {
                String ENTitle = object.getString(Catalog.EN_TITLE);
                String RUTitle = object.getString(Catalog.RU_TITLE);
                for (Catalog catalog : catalogs) {
                    String parent = object.getString(Catalog.PARENT);
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
        refresh.setRefreshing(true);
        Parse.Data.categoryList.clear();
        getCategory();
    }
}
