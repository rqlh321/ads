package ru.example.sic.my_ads.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ViewPagerAdapter;
import ru.example.sic.my_ads.fragments.main.CatalogFragment;
import ru.example.sic.my_ads.fragments.main.HomeFragment;
import ru.example.sic.my_ads.fragments.main.MyAdsFragment;
import ru.example.sic.my_ads.fragments.main.ProfileFragment;
import ru.example.sic.my_ads.fragments.main.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    @BindView(R.id.tabs)
    public TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), getString(R.string.fragment_home));
        adapter.addFragment(new CatalogFragment(), getString(R.string.menu_catalog));
        adapter.addFragment(new SearchFragment(), getString(R.string.search));
        adapter.addFragment(new MyAdsFragment(), getString(R.string.menu_my_ads));
        adapter.addFragment(new ProfileFragment(), getString(R.string.my_profile));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.vector_drawable_ic_list_black___px);
        tabLayout.getTabAt(2).setIcon(R.drawable.vector_drawable_ic_search_black___px);
        tabLayout.getTabAt(3).setIcon(R.drawable.vector_drawable_ic_playlist_add_black___px);
        tabLayout.getTabAt(4).setIcon(R.drawable.account);
    }
}