package ru.example.sic.my_ads.fragments.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.CreateAdActivity;
import ru.example.sic.my_ads.adapters.MyAdsAdapter;
import ru.example.sic.my_ads.models.Ad;

public class MyAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.TITLE, Ad.PHOTO, Ad.COST, Ad.CURRENCY, Ad.AUTHOR_ID};
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private MyAdsAdapter adapter;
    public ArrayList<Ad> my;

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;

    @OnClick(R.id.add_ad)
    void createAd() {
        Intent intent = new Intent(getContext(), CreateAdActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        ButterKnife.bind(this, view);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String jsonString = prefs.getString(Constants.MY_ADS, null);
        if (jsonString != null) {
            Type type = new TypeToken<List<Ad>>() {
            }.getType();
            my = gson.fromJson(jsonString, type);
        } else {
            my = new ArrayList<>();
            getAdsList();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyAdsAdapter(this, my);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        prefs.edit()
                .putString(Constants.MY_ADS, gson.toJson(my))
                .apply();
    }

    @Override
    public void onRefresh() {

    }

    private void getAdsList() {
        ParseQuery.getQuery(Ad.class.getSimpleName())
                .whereEqualTo(Ad.AUTHOR_ID, ParseUser.getCurrentUser().getObjectId())
                .orderByDescending(Ad.CREATED_AT)
                .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS)))
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
}
