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
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ListAdsAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchSimpleFragment extends Fragment {
    @BindView(R.id.textForSearch)
    EditText textForSearch;
    private ArrayList<ParseObject> resultList = new ArrayList<>();
    private ListAdsAdapter adapter;

    @OnClick(R.id.searchButton)
    public void search() {
        if (!textForSearch.getText().toString().equals("")) {
            final ProgressDialog progress = new ProgressDialog(getContext());
            progress.setTitle(getString(R.string.loading));
            progress.setMessage(getString(R.string.wait_loading));
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            progress.show();
            Observable.just(textForSearch.getText().toString())
                    .map(new Func1<String, ArrayList<ParseObject>>() {
                        @Override
                        public ArrayList<ParseObject> call(String text) {
                            return Parse.Request.getSearchResult("", "", 2, 0, text, "", "");
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
            Toast.makeText(getContext(), getString(R.string.empty_search_string), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_search, container, false);
        ButterKnife.bind(this, view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ListAdsAdapter(getParentFragment(), resultList, R.id.support_container);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
