package ru.example.sic.my_ads.fragments.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.UserViewAdapter;
import ru.example.sic.my_ads.fragments.support.BlackListFragment;


public class OwnerViewFragment extends Fragment {
    public ParseObject owner;
    public ArrayList<ParseObject> ownerContacts;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.ownerParams)
    RecyclerView ownerParameters;
    @BindView(R.id.blockButton)
    AppCompatButton bunAction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_view, container, false);
        ButterKnife.bind(this, view);
        setup();
        return view;
    }

    protected void setup() {
        if (owner.getParseFile(Parse.Constants.USER_AVATAR) != null) {
            Glide.with(this).load(owner.getParseFile(Parse.Constants.USER_AVATAR).getUrl())
                    .centerCrop()
                    .into(avatar);
        }
        ownerParameters.setLayoutManager(new LinearLayoutManager(ownerParameters.getContext()));
        UserViewAdapter adapter = new UserViewAdapter(getContext(), ownerContacts);
        ownerParameters.setAdapter(adapter);

        if (!owner.getObjectId().equals(Parse.Data.currentUser.getObjectId())) {
            if (getTag().equals(BlackListFragment.TAG)) {
                bunAction.setText(R.string.unblock_button);
                bunAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Parse.Request.addOrRemoveFromBlacklist(owner.getObjectId(), false);
                        getActivity().onBackPressed();
                    }
                });
                Parse.Data.blackList.clear();
            } else {
                bunAction.setText(R.string.block_button);
                bunAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Parse.Request.addOrRemoveFromBlacklist(owner.getObjectId(), true);
                        getActivity().onBackPressed();
                    }
                });
            }
        } else {
            bunAction.setVisibility(View.GONE);
        }
    }

}