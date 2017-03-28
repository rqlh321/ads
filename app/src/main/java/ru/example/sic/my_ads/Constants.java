package ru.example.sic.my_ads;

import java.util.Locale;

public final class Constants {
    public static final String LANGUAGE = Locale.getDefault().getDisplayLanguage().equals("English") ? "en" : "ru";
    public static final String EXTRA_SELECTED_CATEGORY = "extraSelectedCategory";
    public static final String EXTRA_IS_CATEGORY = "extraCategoryType";
    public static final int RC_GET_PICTURE = 0;
    public static final int RC_LOGIN = 1;

    public static final String MY_ADS = "my";
    public static final String RECOMMENDED = "recommended";
    public static final String LAST = "last";
    public static final String PROMO = "promo";
    public static final String CATALOG = "catalog";
    public static final String CATALOG_VERSION = "catalog_version";
}
