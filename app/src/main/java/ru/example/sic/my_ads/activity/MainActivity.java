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
import ru.example.sic.my_ads.models.Category;
import ru.example.sic.my_ads.models.PromoAction;

public class MainActivity extends AppCompatActivity {
//    public ArrayList<Ad> my;
//    public ArrayList<Ad> recommended;
//    public ArrayList<Ad> last;
//    public ArrayList<PromoAction> promoActions;
//    public ArrayList<Ad> categoryAds;
//    public ArrayList<Ad> search;
//    public ArrayList<Ad> map;

    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    @BindView(R.id.tabs)
    public TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        if (savedInstanceState == null) {
//            recommended = new ArrayList<>();
//            last = new ArrayList<>();
//            promoActions = new ArrayList<>();
//          //  my = new ArrayList<>();
//            categoryAds = new ArrayList<>();
//            search = new ArrayList<>();
//            map = new ArrayList<>();
//        } else {
//            my = savedInstanceState.getParcelableArrayList("my");
//            promoActions = savedInstanceState.getParcelableArrayList("promoActions");
//            recommended = savedInstanceState.getParcelableArrayList("recommended");
//            last = savedInstanceState.getParcelableArrayList("last");
//            categoryAds = savedInstanceState.getParcelableArrayList("categoryAds");
//            search = savedInstanceState.getParcelableArrayList("search");
//            map = savedInstanceState.getParcelableArrayList("map");
//        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), getString(R.string.fragment_home));
        adapter.addFragment(new CatalogRootFragment(), getString(R.string.menu_catalog));
        adapter.addFragment(new SearchSimpleFragment(), getString(R.string.search));
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("my", my);
//        outState.putParcelableArrayList("promoActions", promoActions);
//        outState.putParcelableArrayList("recommended", recommended);
//        outState.putParcelableArrayList("last", last);
//        outState.putParcelableArrayList("categoryAds", categoryAds);
//        outState.putParcelableArrayList("search", search);
//        outState.putParcelableArrayList("map", map);
    }
}