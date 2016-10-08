package ru.example.sic.my_ads.fragments.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.R;

public class DetailRootFragment extends Fragment {
    public static final String TAG = "DetailRootFragment";

    public ParseObject ad;
    public ArrayList<ParseObject> ads;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getChildFragmentManager().beginTransaction()
                .add(R.id.detail_container, new DetailFragment())
                .commit();
        return inflater.inflate(R.layout.fragment_detail_place_holder, container, false);
    }
}
