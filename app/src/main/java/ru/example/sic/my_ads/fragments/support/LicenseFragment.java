package ru.example.sic.my_ads.fragments.support;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;

import static ru.example.sic.my_ads.Constants.LICENSE_AGREEMENT;

public class LicenseFragment extends Fragment {
    public static final String TAG = "LicenseFragment";
    SharedPreferences.Editor edit;
    @BindView(R.id.apply)
    TextView apply;

    @OnClick(R.id.apply)
    void onApplyClick() {
        edit.putBoolean(LICENSE_AGREEMENT, true);
        edit.apply();
        getActivity().finish();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_license, container, false);
        ButterKnife.bind(this, view);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = sp.edit();
        if (sp.getBoolean(LICENSE_AGREEMENT, false)) {
            apply.setVisibility(View.GONE);
        }
        return view;
    }
}
