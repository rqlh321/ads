package ru.example.sic.my_ads.fragments.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;

import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_ALL_PHOTOS;
import static ru.example.sic.my_ads.Parse.Constants.AD_CONTENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_TERMS_OF_PAYMENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.AD_VIEWS;
import static ru.example.sic.my_ads.Parse.Constants.OBJECT_ID;
import static ru.example.sic.my_ads.Parse.Constants.PHOTOS_PHOTO;
import static ru.example.sic.my_ads.Utils.getDateDiff;

public class DetailFragment extends Fragment {
    public static final String TAG = "DetailFragment";
    @BindView(R.id.productAddToFavorite)
    LinearLayout pAddToFavorite;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.productPictures)
    ImageView adsPager;
    @BindView(R.id.owner)
    LinearLayout ownerView;
    @BindView(R.id.person_type)
    TextView ownerType;
    @BindView(R.id.name)
    TextView ownerName;
    @BindView(R.id.callToSeller)
    Button callToSeller;
    @BindView(R.id.smsToSeller)
    Button smsToSeller;
    @BindView(R.id.eMailToSeller)
    Button eMailToSeller;
    @BindView(R.id.thumbUpPic)
    ImageView thumbUpPicture;
    @BindView(R.id.thumbUpcount)
    TextView thumbUpCount;
    @BindView(R.id.report)
    LinearLayout report;
    @BindView(R.id.productLocation)
    TextView pLocation;
    @BindView(R.id.productName)
    TextView pName;
    @BindView(R.id.fb_share_button)
    ShareButton shareButton;
    @BindView(R.id.productPrise)
    TextView pPrice;
    @BindView(R.id.productTermOfPayment)
    TextView pPaymentType;
    @BindView(R.id.productDescription)
    TextView pDescription;
    @BindView(R.id.daysAgo)
    TextView daysAgo;
    @BindView(R.id.productViews)
    TextView pViews;
    private String titlePicUri = "";
    private ParseObject ad;

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
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                ad = results.get(0);
                Parse.Request.increaseViews(ad);
                getPics();
                floodFields();
            }
        });
    }

    private void floodFields() {
        pLocation.setText(ad.getString(AD_ADDRESS));
        pName.setText(ad.getString(AD_TITLE));
        pPrice.setText(ad.getNumber(AD_COST) + " " + ad.getString(AD_CURRENCY));
        pDescription.setText(ad.getString(AD_CONTENT));
        int daysCount = (int) getDateDiff(ad.getCreatedAt(), new Date(), TimeUnit.DAYS);
        daysAgo.setText(getResources().getQuantityString(R.plurals.days_count, daysCount, daysCount));
        pViews.setText(ad.getNumber(AD_VIEWS).toString());
        try {
            JSONArray terms = ad.getJSONArray(AD_TERMS_OF_PAYMENT);
            if (terms != null && terms.length() == 2) {
                pPaymentType.setText(getResources().getStringArray(R.array.paymentType)[terms.getInt(0)]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setImageUrl(Uri.parse(titlePicUri))
                .setContentTitle(ad.getString(AD_TITLE))
                .setContentDescription(ad.getString(AD_CONTENT))
                .build();
        shareButton.setShareContent(content);
    }

    private void getPics() {
        ParseRelation<ParseObject> relation = ad.getRelation(AD_ALL_PHOTOS);
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0) {
                    titlePicUri = objects.get(0).getParseFile(PHOTOS_PHOTO).getUrl();
                    Glide.with(getContext())
                            .load(objects.get(0).getParseFile(PHOTOS_PHOTO).getUrl())
                            .centerCrop()
                            //.placeholder(R.drawable.loadfish)
                            .crossFade()
                            .dontAnimate()
                            .into(adsPager);
                } else {
                    adsPager.setVisibility(View.GONE);
                }
            }
        });
    }

}
