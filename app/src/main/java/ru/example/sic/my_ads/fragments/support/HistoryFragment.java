package ru.example.sic.my_ads.fragments.support;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.DatabaseHelper;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.HistoryAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HistoryFragment extends Fragment {
    public static final String TAG = "HistoryActivity";
    HistoryAdapter adapter;
    DatabaseHelper dbHelper;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public void onResume() {
        super.onResume();
        getHistory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_history, container, false);
        ButterKnife.bind(this, view);
        dbHelper = new DatabaseHelper(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void getHistory() {
        final ArrayList<String> adsId = dbHelper.getHistoryList(Parse.Data.currentUser.getObjectId());

        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle(getString(R.string.loading));
        progress.setMessage(getString(R.string.wait_loading));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress.show();
        Observable.just(adsId)
                .map(new Func1<ArrayList<String>, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(ArrayList<String> ids) {
                        return Parse.Request.getAdsByIds(ids);
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
                        Parse.Data.history.clear();
                        Parse.Data.history.addAll(result);
                        adapter = new HistoryAdapter(HistoryFragment.this, Parse.Data.history, adsId);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

}
