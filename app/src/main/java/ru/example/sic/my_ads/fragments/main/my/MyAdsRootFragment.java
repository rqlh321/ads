package ru.example.sic.my_ads.fragments.main.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.example.sic.my_ads.R;

public class MyAdsRootFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getFragmentManager().beginTransaction()
                .add(R.id.container_my_ads, new MyAdsFragment())
                .commit();
        return inflater.inflate(R.layout.fragment_my_ads_place_holder, container, false);
    }
}
