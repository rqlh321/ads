package ru.example.sic.my_ads.fragments.main.catalog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ExpandableRecyclerViewAdapter;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.Catalog;
import ru.example.sic.my_ads.models.Category;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.LANGUAGE;

public class CatalogFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SharedPreferences prefs;
    private Gson gson = new Gson();

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
                        ParseObject category = new ParseObject(Category.class.getSimpleName());
                        category.put(Category.EN, en.getText().toString());
                        category.put(Category.RU, ru.getText().toString());
                        category.put(Category.PARENT, "");
                        category.saveInBackground();
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    public ArrayList<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, view);
        refresh.setOnRefreshListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String catalogString = prefs.getString(Constants.CATALOG, null);
        if (catalogString != null) {
            Type type = new TypeToken<List<Category>>() {}.getType();
            categoryList = gson.fromJson(catalogString, type);
            createCatalog();
        } else {
            categoryList = new ArrayList<>();
            getCategory();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prefs.edit()
                .putString(Constants.CATALOG, gson.toJson(categoryList))
                .apply();
    }

    @Override
    public void onRefresh() {
        categoryList.clear();
        getCategory();
    }

    public void getCategory() {
        refresh.setRefreshing(true);
        Observable.just(LANGUAGE)
                .map(new Func1<String, ArrayList<Category>>() {
                    @Override
                    public ArrayList<Category> call(String language) {
                        ArrayList<Category> categories = new ArrayList<>();
                        try {
                            List<ParseObject> list = ParseQuery.getQuery(Category.class.getSimpleName())
                                    .find();
                            for (ParseObject parseObject : list) {
                                categories.add(new Category(parseObject));
                            }
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                        return categories;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Category>>() {
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
                    public void onNext(final ArrayList<Category> objects) {
                        categoryList.clear();
                        categoryList.addAll(objects);
                        createCatalog();
                    }
                });
    }

    private void createCatalog() {
        ArrayList<Catalog> catalogs = compileCatalogs(categoryList);
        ExpandableRecyclerViewAdapter adapter = new ExpandableRecyclerViewAdapter(CatalogFragment.this, catalogs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        expandableRecyclerView.setLayoutManager(linearLayoutManager);
        expandableRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Catalog> compileCatalogs(ArrayList<Category> categories) {
        ArrayList<Catalog> catalogs = new ArrayList<>();
        for (Category category : categories) {
            if (category.parent.isEmpty()) {
                ArrayList<Category> subCatalog = new ArrayList<>();
                Catalog catalog = new Catalog(category, subCatalog);
                catalogs.add(catalog);
            } else {
                for (Catalog catalog : catalogs) {
                    if (catalog.getCategory().en.equals(category.parent)) {
                        catalog.getChildItemList().add(category);
                        break;
                    }
                }
            }
        }
        return catalogs;
    }
}
