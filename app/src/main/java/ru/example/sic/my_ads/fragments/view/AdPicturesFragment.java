package ru.example.sic.my_ads.fragments.view;

import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import ru.example.sic.my_ads.R;

public class AdPicturesFragment extends Fragment {
    private static final String ARG_URI = "uri";

    public static AdPicturesFragment create(String uri) {
        AdPicturesFragment fragment = new AdPicturesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_URI, uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.custom_multiviewpager_item_in_deatil_activity, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageItem);
        final ObjectAnimator anim = ObjectAnimator.ofInt(imageView, "ImageLevel", 0, 10000);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();
        Glide.with(this)
                .load(getArguments().getString(ARG_URI))
                .centerCrop()
                //.placeholder(R.drawable.loadfish)
                .crossFade()
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }
                })
                .into(imageView);

        return rootView;
    }


}
