package ru.example.sic.my_ads.fragments.main.catalog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.example.sic.my_ads.R;

public class CatalogRootFragment extends Fragment {
    public static final String TAG = "CatalogRootFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container_catalog, new CatalogFragment(), TAG)
                    .commit();
        }
        return inflater.inflate(R.layout.fragment_catalog_place_holder, container, false);
    }

}
