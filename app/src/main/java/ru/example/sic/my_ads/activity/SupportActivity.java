package ru.example.sic.my_ads.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.support.BlackListFragment;
import ru.example.sic.my_ads.fragments.support.CreateAdFragment;
import ru.example.sic.my_ads.fragments.support.HelloFragment;
import ru.example.sic.my_ads.fragments.support.HistoryFragment;
import ru.example.sic.my_ads.fragments.support.LicenseFragment;
import ru.example.sic.my_ads.fragments.support.ProfileFragment;
import ru.example.sic.my_ads.fragments.search.SearchFragment;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;

public class SupportActivity extends AppCompatActivity {
    public static final String TAG = "SupportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (getIntent().getStringExtra(EXTRA_SHAPE)) {
                case ProfileFragment.TAG:
                    transaction.add(R.id.support_container, new ProfileFragment());
                    ab.setTitle(R.string.my_profile);
                    break;
                case HelloFragment.TAG:
                    transaction.add(R.id.support_container, new HelloFragment());
                    ab.setTitle(R.string.app_name);
                    break;
                case LicenseFragment.TAG:
                    transaction.add(R.id.support_container, new LicenseFragment());
                    ab.setTitle(R.string.about);
                    break;
                case BlackListFragment.TAG:
                    transaction.add(R.id.support_container, new BlackListFragment());
                    ab.setTitle(R.string.my_blacklist);
                    break;
                case HistoryFragment.TAG:
                    transaction.add(R.id.support_container, new HistoryFragment());
                    ab.setTitle(R.string.menu_last_viewed);
                    break;
                case CreateAdFragment.TAG:
                    transaction.add(R.id.support_container, new CreateAdFragment(), TAG);
                    ab.setTitle(R.string.create_ad_button);
                    break;
                case SearchFragment.TAG:
                    transaction.add(R.id.support_container, new SearchFragment(),TAG);
                    ab.setTitle(R.string.search);
                    break;
            }
            transaction.commit();
        } else {
            ab.setTitle(savedInstanceState.getString("savedTitle", ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("savedTitle", getSupportActionBar().getTitle().toString());
        super.onSaveInstanceState(outState);
    }
}