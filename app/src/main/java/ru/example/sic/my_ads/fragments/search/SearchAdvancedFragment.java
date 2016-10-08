package ru.example.sic.my_ads.fragments.search;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ListAdsAdapter;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchAdvancedFragment extends Fragment {
    public static final String TAG = "SearchAdvancedFragment";

    public String categoryTrueName = "";
    @BindView(R.id.category)
    public TextView category;
    @BindView(R.id.textForSearch)
    EditText textForSearch;
    @BindView(R.id.startCost)
    EditText startCost;
    @BindView(R.id.endCost)
    EditText endCost;
    @BindView(R.id.person_type)
    RadioGroup isPerson;
    @BindView(R.id.where_to_search)
    RadioGroup whereToSearch;
    @BindView(R.id.clearCategory)
    ImageButton clearCategory;
    private String textForSearchText;
    private String startCostText;
    private String endCostText;
    private int isPersonPosition;
    private int whereToSearchPosition;
    private ArrayList<ParseObject> resultList = new ArrayList<>();
    private ListAdsAdapter adapter;

    @OnClick(R.id.clearCategory)
    void clearCategory() {
        category.setText("");
        categoryTrueName = "";
        clearCategory.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.clear_fields)
    void clearSearchFields() {
        isPerson.check(R.id.any);
        whereToSearch.check(R.id.everywhere);
        category.setText("");
        categoryTrueName = "";
        clearCategory.setVisibility(View.INVISIBLE);
        textForSearch.setText("");
        startCost.setText("");
        endCost.setText("");
    }

    @OnClick(R.id.choose_category)
    void setCategory() {
        Fragment parentFragment = getParentFragment();
        parentFragment.getFragmentManager()
                .beginTransaction()
                .hide(parentFragment)
                .add(R.id.support_container, new CatalogFragment(), TAG)
                .addToBackStack(null)
                .commit();
        clearCategory.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.searchButton)
    void search() {
        if (
                !category.getText().toString().equals("") ||
                        isPerson.getCheckedRadioButtonId() != R.id.any ||
                        !textForSearch.getText().toString().equals("") ||
                        !startCost.getText().toString().equals("") ||
                        !endCost.getText().toString().equals("")
                ) {
            final ProgressDialog progress = new ProgressDialog(getContext());
            progress.setTitle(getString(R.string.loading));
            progress.setMessage(getString(R.string.wait_loading));
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            progress.show();

            switch (isPerson.getCheckedRadioButtonId()) {
                case R.id.any: {
                    isPersonPosition = 2;
                    break;
                }
                case R.id.organization: {
                    isPersonPosition = 1;
                    break;
                }
                case R.id.person: {
                    isPersonPosition = 0;
                    break;
                }
            }
            switch (whereToSearch.getCheckedRadioButtonId()) {
                case R.id.in_text: {
                    whereToSearchPosition = 2;
                    break;
                }
                case R.id.in_title
                        : {
                    whereToSearchPosition = 1;
                    break;
                }
                case R.id.everywhere: {
                    whereToSearchPosition = 0;
                    break;
                }
            }
            textForSearchText = textForSearch.getText().toString();
            startCostText = startCost.getText().toString();
            endCostText = endCost.getText().toString();
            Observable.just(textForSearch.getText().toString())
                    .map(new Func1<String, ArrayList<ParseObject>>() {
                        @Override
                        public ArrayList<ParseObject> call(String text) {
                            return
                                    Parse.Request.getSearchResult(
                                            // addressText,
                                            "",
                                            categoryTrueName,
                                            isPersonPosition,
                                            whereToSearchPosition,
                                            textForSearchText,
                                            startCostText,
                                            endCostText);
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
                            progress.dismiss();
                            resultList.clear();
                            resultList.addAll(result);
                            adapter.notifyDataSetChanged();
                        }
                    });

        } else {
            Toast.makeText(SearchAdvancedFragment.this.getContext(), getString(R.string.empty_search_string), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        ButterKnife.bind(this, view);

        isPerson.check(R.id.any);
        whereToSearch.check(R.id.everywhere);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new ListAdsAdapter(getParentFragment(), resultList, R.id.support_container);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
