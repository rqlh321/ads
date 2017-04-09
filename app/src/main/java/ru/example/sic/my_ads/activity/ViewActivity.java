package ru.example.sic.my_ads.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.models.Ad;
import ru.example.sic.my_ads.models.User;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ViewActivity extends AppCompatActivity {
    public static final String[] PREVIEW_KEYS = new String[]{Ad.VIEWS, Ad.ADDRESS, Ad.CONTENT, Ad.COORDINATES, Ad.RECOMMENDED, Ad.SUBCATEGORY, Ad.CREATED_AT};
    public static final String OBJECT_ID = "objectId";
    private User user;
    private Ad ad;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.productPictures)
    public ImageView adsPager;

    @BindView(R.id.owner_type)
    public TextView ownerType;

    @BindView(R.id.owner)
    public TextView ownerName;

    @BindView(R.id.callToSeller)
    public Button callToSeller;

    @BindView(R.id.smsToSeller)
    public Button smsToSeller;

    @BindView(R.id.eMailToSeller)
    public Button eMailToSeller;

    @BindView(R.id.productLocation)
    public TextView pLocation;

    @BindView(R.id.productPrise)
    public TextView pPrice;

    @BindView(R.id.productDescription)
    public TextView pDescription;

    @BindView(R.id.productViews)
    public TextView pViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ad = getIntent().getParcelableExtra("ad");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ab.setTitle(ad.title);

        Glide.with(this)
                .load(ad.photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(adsPager);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey("ad") &&
                savedInstanceState.getParcelable("ad") != null &&
                ((Ad) savedInstanceState.getParcelable("ad")).content != null) {
            ad = savedInstanceState.getParcelable("ad");
            setupAd(ad);
        } else {
            Observable.just(ad)
                    .map(new Func1<Ad, Ad>() {
                        @Override
                        public Ad call(Ad ad) {
                            try {
                                ParseObject parseObject = ParseQuery.getQuery(Ad.class.getSimpleName())
                                        .whereEqualTo(OBJECT_ID, ad.id)
                                        .selectKeys(new HashSet<>(Arrays.asList(PREVIEW_KEYS)))
                                        .getFirst();
                                parseObject.increment(Ad.VIEWS);
                                parseObject.save();
                                ad.refresh(parseObject);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return ad;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Ad>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Ad ad) {
                            ViewActivity.this.ad = ad;
                            setupAd(ad);
                        }
                    });
        }

        if (savedInstanceState != null &&
                savedInstanceState.containsKey("user") &&
                savedInstanceState.getParcelable("user") != null) {
            user = savedInstanceState.getParcelable("user");
            setupOwner(user, ad);
        } else {
            Observable.just(ad.authorId)
                    .map(new Func1<String, User>() {
                        @Override
                        public User call(String id) {
                            try {
                                ParseObject ownerParseObject = ParseQuery.getQuery("_" + User.class.getSimpleName())
                                        .whereEqualTo(OBJECT_ID, id)
                                        .getFirst();
                                return new User(ownerParseObject);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return null;
                            }

                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<User>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(User owner) {
                            if (owner != null) {
                                user = owner;
                                setupOwner(owner, ad);
                            }
                        }
                    });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("user", user);
        outState.putParcelable("ad", ad);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void setupOwner(final User owner, final Ad ad) {
        ownerName.setText(owner.name);
        ownerType.setText(owner.person ? getString(R.string.person) : getString(R.string.organization));

        if (!owner.phone.isEmpty()) {
            callToSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(ViewActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", owner.phone, null)));
                }
            });
            callToSeller.setBackgroundResource(R.color.mainColor);

            smsToSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("smsto:" + Uri.encode(owner.phone)));
                    startActivity(intent);
                }
            });
            smsToSeller.setBackgroundResource(R.color.mainColor);
        }

        eMailToSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{owner.email});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "-" + ad.title);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ViewActivity.this, getString(R.string.no_app_to_send_email), Toast.LENGTH_SHORT).show();
                }
            }
        });
        eMailToSeller.setBackgroundResource(R.color.mainColor);
    }

    private void setupAd(Ad ad) {
        pLocation.setText(ad.address);
        pPrice.setText(ad.cost + " " + ad.currency);
        pDescription.setText(ad.content);
        pViews.setText(String.valueOf(ad.views));
    }
}