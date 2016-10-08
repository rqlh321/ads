package ru.example.sic.my_ads.fragments.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ViewPagerAdapter;

public class SearchFragment extends Fragment {
    public static final String TAG = "SearchRootFragment";
    @BindView(R.id.viewpagerSearch)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SearchSimpleFragment(), getString(R.string.search));
        adapter.addFragment(new SearchAdvancedFragment(), getString(R.string.advanced_search));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        tab0.setIcon(R.drawable.vector_drawable_ic_search_black___px);
        tab1.setIcon(R.drawable.vector_drawable_ic_filter_list_black___px);
        return view;
    }
}
