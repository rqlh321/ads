package ru.example.sic.my_ads.adapters;

import android.content.Intent;
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

import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.CreateAdActivity;
import ru.example.sic.my_ads.activity.ListAdsActivity;
import ru.example.sic.my_ads.fragments.CreateAdFragment;
import ru.example.sic.my_ads.models.Catalog;
import ru.example.sic.my_ads.models.Category;

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
        categoryViewHolder.textView.setText(Constants.LANGUAGE.equals("ru") ? mCatalog.getCategory().ru : mCatalog.getCategory().en);
        switch (fragment.getTag()) {
            case CreateAdFragment.TAG:
                categoryViewHolder.imageView.setVisibility(View.GONE);
                break;
            default:
                categoryViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                        LinearLayout linearLayout = new LinearLayout(fragment.getContext());
                        final EditText en = new EditText(fragment.getContext());
                        linearLayout.addView(en);
                        final EditText ru = new EditText(fragment.getContext());
                        linearLayout.addView(ru);
                        builder.setView(linearLayout)
                                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ParseObject category = new ParseObject(Category.class.getSimpleName());
                                        category.put(Category.EN, en.getText().toString());
                                        category.put(Category.RU, ru.getText().toString());
                                        category.put(Category.PARENT, mCatalog.getCategory().en);
                                        category.saveInBackground();
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();*/
                    }
                });
                break;
        }
    }

    @Override
    public void onBindChildViewHolder(CatalogChildViewHolder subCategoryViewHolder, int position, Object childListItem) {
        final Category mSubCatalogItem = (Category) childListItem;
        subCategoryViewHolder.subCatalog.setText(Constants.LANGUAGE.equals("ru") ? mSubCatalogItem.ru : mSubCatalogItem.en);
        switch (fragment.getTag()) {
            case CreateAdFragment.TAG:
                subCategoryViewHolder.subCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CreateAdFragment createAdFragment = (CreateAdFragment) fragment.getFragmentManager()
                                .findFragmentByTag(CreateAdActivity.TAG);
                        createAdFragment.categoryText.setText(Constants.LANGUAGE.equals("ru") ? mSubCatalogItem.ru : mSubCatalogItem.en);
                        createAdFragment.categoryId = mSubCatalogItem.id;
                        fragment.getActivity().onBackPressed();
                    }
                });
                break;
            default:
                subCategoryViewHolder.subCatalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(fragment.getContext(), ListAdsActivity.class);
                        intent.putExtra(Constants.EXTRA_SELECTED_CATEGORY, mSubCatalogItem);
                        intent.putExtra(Constants.EXTRA_IS_CATEGORY, true);
                        fragment.startActivity(intent);
                    }
                });
                break;
        }
    }
}