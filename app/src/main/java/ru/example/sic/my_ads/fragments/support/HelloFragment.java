package ru.example.sic.my_ads.fragments.support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.R;

public class HelloFragment extends Fragment {
    public static final String TAG="HelloFragment";
    @OnClick(R.id.next)
    void onNextClick() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.support_container, new LicenseFragment())
                .addToBackStack(null)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hello, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
