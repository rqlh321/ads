package ru.example.sic.my_ads.fragments.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.parse.ParseObject;
import com.pixplicity.multiviewpager.MultiViewPager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.DatabaseHelper;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_CONTENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_TERMS_OF_PAYMENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.AD_VIEWS;
import static ru.example.sic.my_ads.Parse.Constants.PHOTOS_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.USERS_AD_REPORT;
import static ru.example.sic.my_ads.Parse.Constants.USERS_AD_REPORT_REPORTED_AD;
import static ru.example.sic.my_ads.Parse.Constants.USERS_AD_REPORT_USERS_COMMENT;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_COUNTRY_CODE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_TYPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_VALUE;
import static ru.example.sic.my_ads.Parse.Constants.USER_FIRST_NAME;
import static ru.example.sic.my_ads.Parse.Constants.USER_IS_PERSON;
import static ru.example.sic.my_ads.Utils.getDateDiff;

public class DetailFragment extends Fragment {
    public static final String TAG = "DetailFragment";
    @BindView(R.id.productAddToFavorite)
    LinearLayout pAddToFavorite;
    @BindView(R.id.star)
    ImageView star;
    @BindView(R.id.productPictures)
    MultiViewPager adsPager;
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
    private DatabaseHelper dbHelper;
    private OwnerViewFragment ownerViewFragment = new OwnerViewFragment();
    private ParseObject ad;
    private ArrayList<ParseObject> adList;

    @OnClick(R.id.next)
    void goNext() {
        if (adList.size() > 0) {
            int i = adList.indexOf(ad);
            if (i + 1 < adList.size()) {
                ad = adList.get(i + 1);
            } else {
                ad = adList.get(0);
            }
            onResume();
        }
    }

    @OnClick(R.id.back)
    void goBack() {
        if (adList.size() > 0) {
            int i = adList.indexOf(ad);
            if (i - 1 < 0) {
                ad = adList.get(adList.size() - 1);
            } else {
                ad = adList.get(i - 1);
            }
            onResume();
        }
    }

    @OnClick(R.id.report)
    void report() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText editText = new EditText(getContext());
        alert.setTitle(getString(R.string.report_to_ad_title));
        alert.setView(editText);
        alert.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Parse.Request.addAdReport(USERS_AD_REPORT,
                        USERS_AD_REPORT_REPORTED_AD,
                        ad, USERS_AD_REPORT_USERS_COMMENT,
                        editText.getText().toString());
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeAds();
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        dbHelper = new DatabaseHelper(getContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Parse.Request.increaseViews(ad);
        dbHelper.addToDataBase(Parse.Data.currentUser.getObjectId(), ad.getObjectId());
        getPics();
        getOwner();
        isFavorite();
        getLikes();
        isLiked();
    }

    private void initializeAds() {
        DetailRootFragment parent = (DetailRootFragment) getParentFragment();
        ad = parent.ad;
        adList = parent.ads;
    }

    private void floodFields(ParseObject owner) {
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
        if (owner != null && Parse.Data.currentUser != null && Parse.Data.currentUser.getObjectId().equals(owner.getObjectId())) {
            report.setVisibility(View.GONE);
        }
    }

    private void getPics() {
        Observable.just(ad != null)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean adExist) {
                        if (adExist) {
                            return Parse.Request.getPhotos(ad);
                        }
                        return new ArrayList<>();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ArrayList<ParseObject> objects) {
                        if (objects.size() > 0) {
                            titlePicUri = objects.get(0).getParseFile(PHOTOS_PHOTO).getUrl();
                            adsPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {

                                @Override
                                public int getCount() {
                                    return objects.size();
                                }

                                @Override
                                public Fragment getItem(int position) {
                                    return AdPicturesFragment.create(objects.get(position).getParseFile(PHOTOS_PHOTO).getUrl());
                                }
                            });
                        } else {
                            adsPager.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getContacts(final ParseObject owner) {
        Observable.just(owner != null)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean ownerExist) {
                        if (ownerExist) {
                            return Parse.Request.getContacts(owner, null);
                        }
                        return new ArrayList<>();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ArrayList<ParseObject> ownerContacts) {
                        ownerViewFragment.ownerContacts = ownerContacts;
                        ownerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Parse.Data.currentUser != null) {
                                    getFragmentManager().beginTransaction()
                                            .hide(DetailFragment.this)
                                            .add(R.id.detail_container, ownerViewFragment, TAG)
                                            .addToBackStack(null)
                                            .commit();
                                } else {
                                    MainActivity.logIn(getActivity());
                                }
                            }
                        });
                        String ownerPhoneNumber = null;
                        for (ParseObject ownerContact : ownerContacts) {
                            if ((int) ownerContact.getNumber(USER_CONTACTS_OF_TYPE) == 1) {
                                ownerPhoneNumber = ownerContact.getString(USER_CONTACTS_COUNTRY_CODE) + ownerContact.getString(USER_CONTACTS_VALUE);
                                break;
                            }
                        }
                        final String finalOwnerPhoneNumber = ownerPhoneNumber;
                        if (finalOwnerPhoneNumber != null) {
                            callToSeller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Parse.Data.currentUser != null) {
                                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", finalOwnerPhoneNumber, null)));
                                    }
                                }
                            });
                            smsToSeller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Parse.Data.currentUser != null) {
                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("smsto:" + Uri.encode(finalOwnerPhoneNumber)));
                                        startActivity(intent);
                                    }
                                }
                            });
                            smsToSeller.setBackgroundColor(getResources().getColor(R.color.mainColor));
                            callToSeller.setBackgroundColor(getResources().getColor(R.color.mainColor));
                        }

                        String eMail = null;
                        for (ParseObject ownerContact : ownerContacts) {
                            if ((int) ownerContact.getNumber(USER_CONTACTS_OF_TYPE) == 2) {
                                eMail = ownerContact.getString(USER_CONTACTS_VALUE);
                                break;
                            }
                        }
                        if (eMail != null) {
                            final String finalEMail = eMail;
                            eMailToSeller.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Parse.Data.currentUser != null) {
                                        Intent i = new Intent(Intent.ACTION_SEND);
                                        i.setType("message/rfc822");
                                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{finalEMail});
                                        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "-" + ad.getString(AD_TITLE));
                                        try {
                                            startActivity(Intent.createChooser(i, "Send mail..."));
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Toast.makeText(getContext(), getString(R.string.no_app_to_send_email), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            eMailToSeller.setBackgroundColor(getResources().getColor(R.color.mainColor));
                        }
                    }
                });
    }

    private void getOwner() {
        Observable.just(ad != null)
                .map(new Func1<Boolean, ParseObject>() {
                    @Override
                    public ParseObject call(Boolean adExist) {
                        if (adExist) {
                            return Parse.Request.getOwner(ad);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ParseObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ParseObject object) {
                        ownerName.setText(object.getString(USER_FIRST_NAME));
                        ownerType.setText(object.getBoolean(USER_IS_PERSON) ? getString(R.string.person) : getString(R.string.organization));
                        ownerViewFragment.owner = object;
                        getContacts(object);
                        floodFields(object);
                    }
                });
    }

    private void isFavorite() {
        Observable.just(ad != null)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean adExist) {
                        if (adExist && Parse.Data.currentUser != null) {
                            return Parse.Request.isFavorite(ad.getObjectId(), Parse.Data.currentUser);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final Boolean result) {
                        if (!result) {
                            pAddToFavorite.setVisibility(View.VISIBLE);
                            pAddToFavorite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Parse.Data.currentUser != null) {
                                        pAddToFavorite.setOnClickListener(null);
                                        int color = Color.parseColor("#2196F3"); //The color u want
                                        star.setColorFilter(color);
                                        Parse.Request.addToFavorite(ad.getObjectId());
                                    }
                                }
                            });
                        } else {
                            int color = Color.parseColor("#2196F3"); //The color u want
                            star.setColorFilter(color);
                        }
                    }
                });
    }

    private void getLikes() {
        Observable.just(ad != null)
                .map(new Func1<Boolean, Integer>() {
                    @Override
                    public Integer call(Boolean adExist) {
                        if (adExist) {
                            return Parse.Request.getLikeCount(ad);
                        }
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final Integer result) {
                        thumbUpCount.setText(String.valueOf(result));
                        thumbUpPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Parse.Data.currentUser != null) {
                                    int color = Color.parseColor("#2196F3"); //The color u want
                                    thumbUpPicture.setColorFilter(color);
                                    thumbUpPicture.setOnClickListener(null);
                                    Parse.Request.addToLiked(ad.getObjectId());
                                    thumbUpCount.setText(String.valueOf(result + 1));
                                }
                            }
                        });
                    }
                });
    }

    private void isLiked() {
        Observable.just(ad != null)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean adExist) {
                        if (adExist && Parse.Data.currentUser != null) {
                            return Parse.Request.isLike(ad, Parse.Data.currentUser);
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final Boolean result) {
                        if (result) {
                            thumbUpPicture.setOnClickListener(null);
                            int color = Color.parseColor("#404654"); //The color u want
                            thumbUpPicture.setColorFilter(color);
                        }
                    }
                });
    }


}
