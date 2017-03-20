package ru.example.sic.my_ads.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.CreateAdFragment;
import ru.example.sic.my_ads.fragments.view.DetailFragment;

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
                case CreateAdFragment.TAG:
                    transaction.add(R.id.support_container, new CreateAdFragment(), TAG);
                    ab.setTitle(R.string.create_ad_button);
                    break;
                case DetailFragment.TAG:
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(getIntent().getExtras());
                    transaction.add(R.id.support_container, detailFragment, TAG);
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