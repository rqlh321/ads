package ru.example.sic.my_ads.models;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Catalog implements ParentListItem {
    public static final String PARENT = "parent";
    public static final String EN_TITLE = "en";
    public static final String RU_TITLE = "ru";

    private List<Category> subCategoryList;
    private Category mCategory;

    public Catalog(Category mCategory, List<Category> subCategoryList) {
        this.subCategoryList = subCategoryList;
        this.mCategory = mCategory;
    }

    public Category getCategory() {
        return mCategory;
    }

    @Override
    public List<Category> getChildItemList() {
        return subCategoryList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

}

