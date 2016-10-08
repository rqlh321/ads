package ru.example.sic.my_ads.fragments.main.my;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.adapters.MyAdsAdapter;
import ru.example.sic.my_ads.fragments.support.CreateAdFragment;
import ru.example.sic.my_ads.fragments.support.ProfileFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.EXTRA_SHAPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_ADS_OF_USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_TYPE;

public class MyAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    MyAdsAdapter adapter;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.add_ad)
    void createAd() {
        getContacts(Parse.Data.currentUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MyAdsAdapter(this, Parse.Data.my);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        if (Parse.Data.my.size() == 0) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        }
        return view;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        Parse.Data.my.clear();
        adapter.notifyDataSetChanged();
        getAdsList();
    }

    private void getAdsList() {
        Observable.just(false)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean b) {
                        return Parse.Request.getObjectRelationUser(USER_ADS_OF_USER);
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
                        Parse.Data.my.addAll(objects);
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void checkContacts() {
        boolean contactDataExist = false;
        for (ParseObject ownerContact : Parse.Data.myContacts) {
            if ((int) ownerContact.getNumber(USER_CONTACTS_OF_TYPE) == 1 ||
                    (int) ownerContact.getNumber(USER_CONTACTS_OF_TYPE) == 2) {
                contactDataExist = true;
                break;
            }
        }
        if (contactDataExist) {
            Intent intent = new Intent(getContext(), SupportActivity.class);
            intent.putExtra(EXTRA_SHAPE, CreateAdFragment.TAG);
            startActivity(intent);
        } else {
            contactsDoNotExist();
        }
    }

    private void contactsDoNotExist() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getString(R.string.contacts_do_not_exist_title));
        TextView tv = new TextView(getContext());
        tv.setText(getString(R.string.contacts_do_not_exist_text));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(20, 50, 20, 50); // llp.setMargins(left, top, right, bottom);
        tv.setTextSize(18);
        tv.setLayoutParams(llp);
        LinearLayout layout = new LinearLayout(getContext());
        layout.addView(tv);
        alert.setView(layout);
        alert.setPositiveButton(getString(R.string.add_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getContext(), SupportActivity.class);
                intent.putExtra(EXTRA_SHAPE, ProfileFragment.TAG);
                startActivity(intent);
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void getContacts(final ParseObject owner) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle(getString(R.string.loading));
        progress.setMessage(getString(R.string.wait_loading));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress.show();
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
                    public void onNext(final ArrayList<ParseObject> result) {
                        progress.dismiss();
                        Parse.Data.myContacts.clear();
                        Parse.Data.myContacts.addAll(result);
                        checkContacts();
                    }
                });
    }

}
