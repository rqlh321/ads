package ru.example.sic.my_ads.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ViewPagerAdapter;
import ru.example.sic.my_ads.fragments.support.BlackListFragment;
import ru.example.sic.my_ads.fragments.support.HelloFragment;
import ru.example.sic.my_ads.fragments.support.HistoryFragment;
import ru.example.sic.my_ads.fragments.support.LicenseFragment;
import ru.example.sic.my_ads.fragments.support.ProfileFragment;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogRootFragment;
import ru.example.sic.my_ads.fragments.main.favorite.FavoriteRootFragment;
import ru.example.sic.my_ads.fragments.main.home.HomeRootFragment;
import ru.example.sic.my_ads.fragments.main.my.MyAdsRootFragment;
import ru.example.sic.my_ads.fragments.search.SearchFragment;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;
import static ru.example.sic.my_ads.Constants.LICENSE_AGREEMENT;
import static ru.example.sic.my_ads.Constants.RC_LOGIN;
import static ru.example.sic.my_ads.Parse.Constants.APP_REPORT;
import static ru.example.sic.my_ads.Parse.Constants.APP_REPORT_FROM_USER;
import static ru.example.sic.my_ads.Parse.Constants.APP_REPORT_REPORT_FROM_USER;
import static ru.example.sic.my_ads.Parse.Constants.APP_REVIEW;
import static ru.example.sic.my_ads.Parse.Constants.APP_REVIEW_FROM_USER;
import static ru.example.sic.my_ads.Parse.Constants.APP_REVIEW_REVIEW_FROM_USER;
import static ru.example.sic.my_ads.Parse.Constants.APP_SUPPORT;
import static ru.example.sic.my_ads.Parse.Constants.APP_SUPPORT_FROM_USER;
import static ru.example.sic.my_ads.Parse.Constants.APP_SUPPORT_QUESTION_FROM_USER;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void logIn(Activity activity) {
        ParseLoginBuilder builder = new ParseLoginBuilder(activity);
        Intent parseLoginIntent = builder
                //.setAppLogo(R.mipmap.logo_vorschau900)
                .setParseLoginEnabled(true)
                .setParseLoginButtonText(activity.getString(R.string.login_text))
                .setParseLoginEmailAsUsername(true)
                .build();
        activity.startActivityForResult(parseLoginIntent, RC_LOGIN);
    }

    private void logOut() {
        ParseUser.logOut();
        Parse.Data.currentUser = null;
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    @OnClick(R.id.search)
    void onSearchClick() {
        if (Parse.Data.currentUser != null) {
            Intent intent = new Intent(MainActivity.this, SupportActivity.class);
            intent.putExtra(EXTRA_SHAPE, SearchFragment.TAG);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLicenseAgreement();

        setupToolBar();
        setupNavigationView();
        setupViewPager();
        setupTabLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Parse.Data.currentUser != null) {
            View navigationViewHeader = navigationView.getHeaderView(0);
            TextView text = (TextView) navigationViewHeader.findViewById(R.id.navHeaderTextView);
            text.setText(Parse.Data.currentUser.getUsername());
            CircleImageView myAvatar = (CircleImageView) navigationViewHeader.findViewById(R.id.myAvatar);
            if (Parse.Data.currentUser.getParseFile(Parse.Constants.USER_AVATAR) != null) {
                Glide.with(MainActivity.this)
                        .load(Parse.Data.currentUser.getParseFile(Parse.Constants.USER_AVATAR).getUrl())
                        .centerCrop()
                        .into(myAvatar);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ParseUser.getCurrentUser() == null) {
            drawerLayout.closeDrawer(GravityCompat.START);
            logIn(MainActivity.this);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == RESULT_OK) {
            Parse.Data.currentUser = ParseUser.getCurrentUser();
        }
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SupportActivity.class);
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        intent.putExtra(EXTRA_SHAPE, ProfileFragment.TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_black_list:
                        intent.putExtra(EXTRA_SHAPE, BlackListFragment.TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_history:
                        intent.putExtra(EXTRA_SHAPE, HistoryFragment.TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_support:
                        getSupport();
                        break;
                    case R.id.nav_license:
                        intent.putExtra(EXTRA_SHAPE, LicenseFragment.TAG);
                        startActivity(intent);
                        break;
                    case R.id.nav_sign_out:
                        logOut();
                        break;
                }
                item.setChecked(true);
                return false;
            }
        });
    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        TabLayout.Tab tab2 = tabLayout.getTabAt(2);
        TabLayout.Tab tab3 = tabLayout.getTabAt(3);
        tab0.setIcon(R.drawable.home);
        tab1.setIcon(R.drawable.vector_drawable_ic_list_black___px);
        tab2.setIcon(R.drawable.vector_drawable_ic_star_black___px);
        tab3.setIcon(R.drawable.vector_drawable_ic_playlist_add_black___px);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Parse.Data.currentUser == null) {
                    viewPager.setCurrentItem(0);
                    logIn(MainActivity.this);
                } else {
                    viewPager.setCurrentItem(tab.getPosition());
                }
            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeRootFragment(), getString(R.string.fragment_home));
        adapter.addFragment(new CatalogRootFragment(), getString(R.string.menu_catalog));
        adapter.addFragment(new FavoriteRootFragment(), getString(R.string.fragment_favorites));
        adapter.addFragment(new MyAdsRootFragment(), getString(R.string.menu_my_ads));
        viewPager.setAdapter(adapter);
    }

    private void checkLicenseAgreement() {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean licensed = mSharedPreference1.getBoolean(LICENSE_AGREEMENT, false);
        if (!licensed) {
            finish();
            Intent intent = new Intent(MainActivity.this, SupportActivity.class);
            intent.putExtra(EXTRA_SHAPE, HelloFragment.TAG);
            startActivity(intent);
        }
    }

    private void getSupport() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialogCustom);
        alert.setTitle(getString(R.string.support_title));

        ListView supportSet = new ListView(MainActivity.this);
        String[] supportNames = getResources().getStringArray(R.array.supportType);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, R.layout.simple_list_item, supportNames);
        supportSet.setAdapter(adapter);
        supportSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final EditText editText = new EditText(MainActivity.this);
                        alert.setTitle(getString(R.string.question_desc));
                        alert.setView(editText);
                        alert.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Parse.Request.addAdReport(APP_SUPPORT,
                                        APP_SUPPORT_FROM_USER, ParseUser.getCurrentUser(),
                                        APP_SUPPORT_QUESTION_FROM_USER,
                                        editText.getText().toString());
                            }
                        });
                        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                        break;
                    }
                    case 1: {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final EditText editText = new EditText(MainActivity.this);
                        alert.setTitle(getString(R.string.error_desc));
                        alert.setView(editText);
                        alert.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Parse.Request.addAdReport(APP_REPORT,
                                        APP_REPORT_FROM_USER, ParseUser.getCurrentUser(),
                                        APP_REPORT_REPORT_FROM_USER,
                                        editText.getText().toString());
                            }
                        });
                        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                        break;
                    }
                    case 2: {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final EditText editText = new EditText(MainActivity.this);
                        alert.setTitle(getString(R.string.review_desc));
                        alert.setView(editText);
                        alert.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Parse.Request.addAdReport(APP_REVIEW,
                                        APP_REVIEW_FROM_USER, ParseUser.getCurrentUser(),
                                        APP_REVIEW_REVIEW_FROM_USER,
                                        editText.getText().toString());
                            }
                        });
                        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                        break;
                    }
                }
            }
        });
        alert.setView(supportSet);

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

}