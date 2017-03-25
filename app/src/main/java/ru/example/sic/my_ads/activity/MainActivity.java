package ru.example.sic.my_ads.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ViewPagerAdapter;
import ru.example.sic.my_ads.fragments.main.HomeFragment;
import ru.example.sic.my_ads.fragments.main.MyAdsFragment;
import ru.example.sic.my_ads.fragments.main.ProfileFragment;
import ru.example.sic.my_ads.fragments.main.SearchSimpleFragment;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogRootFragment;
import ru.example.sic.my_ads.models.Ad;

public class MainActivity extends AppCompatActivity {

    public ArrayList<ParseObject> banners = new ArrayList<>();
    public ArrayList<ParseObject> recommended = new ArrayList<>();
    public ArrayList<ParseObject> last = new ArrayList<>();
    public ArrayList<Ad> my = new ArrayList<>();

    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    @BindView(R.id.tabs)
    public TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupViewPager();
        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        TabLayout.Tab tab2 = tabLayout.getTabAt(2);
        TabLayout.Tab tab3 = tabLayout.getTabAt(3);
        TabLayout.Tab tab4 = tabLayout.getTabAt(4);
        tab0.setIcon(R.drawable.home);
        tab1.setIcon(R.drawable.vector_drawable_ic_list_black___px);
        tab2.setIcon(R.drawable.vector_drawable_ic_search_black___px);
        tab3.setIcon(R.drawable.vector_drawable_ic_playlist_add_black___px);
        tab4.setIcon(R.drawable.account);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), getString(R.string.fragment_home));
        adapter.addFragment(new CatalogRootFragment(), getString(R.string.menu_catalog));
        adapter.addFragment(new SearchSimpleFragment(), getString(R.string.search));
        adapter.addFragment(new MyAdsFragment(), getString(R.string.menu_my_ads));
        adapter.addFragment(new ProfileFragment(), getString(R.string.my_profile));
        viewPager.setAdapter(adapter);
    }

}