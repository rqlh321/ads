package ru.example.sic.my_ads.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.SupportActivity;
import ru.example.sic.my_ads.fragments.CreateAdFragment;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogRootFragment;
import ru.example.sic.my_ads.fragments.main.catalog.ListAdsFragment;
import ru.example.sic.my_ads.models.Catalog;
import ru.example.sic.my_ads.models.Category;

import static ru.example.sic.my_ads.Constants.EXTRA_IS_CATEGORY;
import static ru.example.sic.my_ads.Constants.EXTRA_SELECTED_CATEGORY;
import static ru.example.sic.my_ads.Constants.LANGUAGE;

class CatalogParentViewHolder extends ParentViewHolder {
    public TextView textView;
    public ImageView imageView;

    public CatalogParentViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.catalog_text);
        imageView = (ImageView) itemView.findViewById(R.id.catalog_action);
    }
}

class CatalogChildViewHolder extends ChildViewHolder {
    public TextView subCatalog;

    public CatalogChildViewHolder(View itemView) {
        super(itemView);
        subCatalog = (TextView) itemView.findViewById(R.id.text1);
    }
}

public class ExpandableRecyclerViewAdapter extends ExpandableRecyclerAdapter<CatalogParentViewHolder, CatalogChildViewHolder> {
    private LayoutInflater mInflater;
    private Fragment fragment;

    public ExpandableRecyclerViewAdapter(Fragment fragment, @NonNull List<Catalog> parentItemList) {
        super(parentItemList);
        this.mInflater = LayoutInflater.from(fragment.getContext());
        this.fragment = fragment;
    }

    @Override
    public CatalogParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View catalogView = mInflater.inflate(R.layout.catalog_list_item, parentViewGroup, false);
        return new CatalogParentViewHolder(catalogView);
    }

    @Override
    public CatalogChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View subCatalogView = mInflater.inflate(R.layout.sub_catalog_list_item, childViewGroup, false);
        return new CatalogChildViewHolder(subCatalogView);
    }

    @Override
    public void onBindParentViewHolder(CatalogParentViewHolder categoryViewHolder, final int position, final ParentListItem parentListItem) {
        final Catalog mCatalog = (Catalog) parentListItem;
        categoryViewHolder.textView.setText(LANGUAGE.equals("ru") ? mCatalog.getCategory().getRu() : mCatalog.getCategory().getEn());
        switch (fragment.getTag()) {
            case CatalogRootFragment.TAG:
                categoryViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListAdsFragment listAdsFragment = new ListAdsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EXTRA_SELECTED_CATEGORY, mCatalog.getCategory());
                        bundle.putBoolean(EXTRA_IS_CATEGORY, true);
                        listAdsFragment.setArguments(bundle);
                        fragment.getFragmentManager()
                                .beginTransaction()
                                .hide(fragment)
                                .add(R.id.container_catalog, listAdsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                break;
            default:
                categoryViewHolder.imageView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBindChildViewHolder(CatalogChildViewHolder subCategoryViewHolder, int position, Object childListItem) {
        final Category mSubCatalogItem = (Category) childListItem;
        subCategoryViewHolder.subCatalog.setText(LANGUAGE.equals("ru") ? mSubCatalogItem.getRu() : mSubCatalogItem.getEn());
        switch (fragment.getTag()) {
            case CreateAdFragment.TAG:
                subCategoryViewHolder.subCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CreateAdFragment createAdFragment = (CreateAdFragment) fragment.getFragmentManager()
                                .findFragmentByTag(SupportActivity.TAG);
                        createAdFragment.categoryText.setText(LANGUAGE.equals("ru") ? mSubCatalogItem.getRu() : mSubCatalogItem.getEn());
                        createAdFragment.category = mSubCatalogItem.getEn();
                        fragment.getActivity().onBackPressed();
                    }
                });
                break;
            case CatalogRootFragment.TAG:
                subCategoryViewHolder.subCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ListAdsFragment listAdsFragment = new ListAdsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(EXTRA_SELECTED_CATEGORY, mSubCatalogItem);
                        bundle.putBoolean(EXTRA_IS_CATEGORY, false);
                        listAdsFragment.setArguments(bundle);
                        fragment.getFragmentManager()
                                .beginTransaction()
                                .hide(fragment)
                                .add(R.id.container_catalog, listAdsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                break;
        }
    }
}