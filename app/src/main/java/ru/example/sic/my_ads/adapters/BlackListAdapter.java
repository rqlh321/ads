package ru.example.sic.my_ads.adapters;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;

import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.fragments.support.BlackListFragment;
import ru.example.sic.my_ads.fragments.view.OwnerViewFragment;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Parse.Constants.USER_AVATAR;
import static ru.example.sic.my_ads.Parse.Constants.USER_FIRST_NAME;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {
    ArrayList<ParseObject> list;
    BlackListFragment fragment;

    public BlackListAdapter(BlackListFragment fragment, ArrayList<ParseObject> list) {
        this.list = list;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.get(position).getParseFile(USER_AVATAR) != null) {
            Glide.with(fragment.getContext())
                    .load(list.get(position).getParseFile(USER_AVATAR).getUrl())
                    .fitCenter()
                    .into(holder.mImageView);
        }
        holder.mTextView.setText(list.get(position).getString(USER_FIRST_NAME));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void getContacts(final ParseObject owner) {
        final ProgressDialog progress = new ProgressDialog(fragment.getContext());
        progress.setTitle(fragment.getString(R.string.loading));
        progress.setMessage(fragment.getString(R.string.wait_loading));
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
                    public void onNext(final ArrayList<ParseObject> ownerContacts) {
                        progress.dismiss();
                        OwnerViewFragment ownerViewFragment = new OwnerViewFragment();
                        ownerViewFragment.ownerContacts = ownerContacts;
                        ownerViewFragment.owner = owner;
                        fragment.getFragmentManager()
                                .beginTransaction()
                                .hide(fragment)
                                .add(R.id.support_container, ownerViewFragment, BlackListFragment.TAG)
                                .addToBackStack(null)
                                .commit();
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(R.id.text1);
        }

    }
}