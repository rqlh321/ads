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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.adapters.MyAdsAdapter;
import ru.example.sic.my_ads.fragments.CreateAdFragment;
import ru.example.sic.my_ads.models.Ad;

public class MyAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.PHOTO, Ad.TITLE, Ad.AUTHOR_ID};

    private MyAdsAdapter adapter;
    private ArrayList<Ad> my;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.add_ad)
    void createAd() {
        Intent intent = new Intent(getContext(), SupportActivity.class);
        intent.putExtra(Constants.EXTRA_SHAPE, CreateAdFragment.TAG);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        my = ((MainActivity) getActivity()).my;
        adapter = new MyAdsAdapter(this, my);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAdsList();
    }

    private void getAdsList() {
        ParseQuery.getQuery(Ad.class.getSimpleName())
                .whereEqualTo(Ad.AUTHOR_ID, ParseUser.getCurrentUser().getObjectId())
                .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS)))
                .setLimit(10)
                .orderByDescending(Ad.CREATED_AT)
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> results, ParseException e) {
                        if (e == null) {
                            my.clear();
                            for (ParseObject parseObject : results) {
                                my.add(new Ad(parseObject));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {

    }
}
