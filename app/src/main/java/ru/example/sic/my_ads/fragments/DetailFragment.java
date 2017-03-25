package ru.example.sic.my_ads.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.models.Ad;

import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.OBJECT_ID;
import static ru.example.sic.my_ads.Utils.getDateDiff;

public class DetailFragment extends Fragment {
    public static final String TAG = "DetailFragment";
    @BindView(R.id.productPictures)
    ImageView adsPager;

    @BindView(R.id.owner_type)
    TextView ownerType;

    @BindView(R.id.owner)
    TextView owner;

    @BindView(R.id.callToSeller)
    Button callToSeller;

    @BindView(R.id.smsToSeller)
    Button smsToSeller;

    @BindView(R.id.eMailToSeller)
    Button eMailToSeller;

    @BindView(R.id.productLocation)
    TextView pLocation;

    @BindView(R.id.productName)
    TextView pName;

    @BindView(R.id.fb_share_button)
    ShareButton shareButton;

    @BindView(R.id.productPrise)
    TextView pPrice;

    @BindView(R.id.productDescription)
    TextView pDescription;

    @BindView(R.id.daysAgo)
    TextView daysAgo;

    @BindView(R.id.productViews)
    TextView pViews;

    private Ad ad;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        initializeAds();
        return view;
    }

    private void initializeAds() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AD);
        Bundle arguments = getArguments();
        String id = arguments.getString("id");
        query.whereEqualTo(OBJECT_ID, id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ad = new Ad(object);
                    Parse.Request.increaseViews(object);
                    floodFields();
                }
            }
        });
    }

    private void floodFields() {
        Glide.with(getContext())
                .load(ad.photoUrl)
                .centerCrop()
                .crossFade()
                .dontAnimate()
                .into(adsPager);
        pLocation.setText(ad.address);
        pName.setText(ad.title);
        pPrice.setText(ad.cost + " " + ad.currency);
        pDescription.setText(ad.content);
        int daysCount = (int) getDateDiff(ad.date, new Date(), TimeUnit.DAYS);
        daysAgo.setText(getResources().getQuantityString(R.plurals.days_count, daysCount, daysCount));
        pViews.setText(String.valueOf(ad.views));

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setImageUrl(Uri.parse(ad.photoUrl))
                .setContentTitle(ad.title)
                .setContentDescription(ad.content)
                .build();
        shareButton.setShareContent(content);
    }
}
