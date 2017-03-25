package ru.example.sic.my_ads;

import java.util.Locale;

public final class Constants {
    public static final String LANGUAGE = Locale.getDefault().getDisplayLanguage().equals("English") ? "en" : "ru";
    public static final String EXTRA_SELECTED_CATEGORY = "extraSelectedCategory";
    public static final String EXTRA_IS_CATEGORY = "extraCategoryType";
    public static final String EXTRA_SHAPE = "extraShape";
    public static final int RC_GET_PICTURE = 0;
    public static final int RC_LOGIN = 1;
}
