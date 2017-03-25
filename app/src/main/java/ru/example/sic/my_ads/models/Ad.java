package ru.example.sic.my_ads.models;

import com.parse.ParseObject;

import java.util.Date;

public class Ad {
    public static final String CURRENCY = "currensy";
    public static final String AUTHOR_ID = "authorID";
    public static final String COST = "cost";
    public static final String VIEWS = "views";
    public static final String PHOTO = "photo";
    public static final String ADDRESS = "address";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String COORDINATES = "coordinates";
    public static final String RECOMMENDED = "recommended";
    public static final String SUBCATEGORY = "subcategory";
    public static final String CREATED_AT = "createdAt";

    public String id;
    public String currency;
    public String authorId;
    public int cost;
    public int views;
    public String photoUrl;
    public String address;
    public String title;
    public String content;
    public Date date;
    public double longitude;
    public double latitude;
    public boolean recommended;
    public String subcategory;
    public String createdAt;

    public Ad(ParseObject parseObject) {
        id = parseObject.getObjectId();
        currency = parseObject.containsKey(CURRENCY) ? parseObject.getString(CURRENCY) : "";
        authorId = parseObject.containsKey(AUTHOR_ID) ? parseObject.getString(AUTHOR_ID) : "";
        cost = parseObject.containsKey(COST) ? parseObject.getInt(COST) : 0;
        views = parseObject.containsKey(VIEWS) ? parseObject.getInt(VIEWS) : 0;
        photoUrl = parseObject.containsKey(PHOTO) ? parseObject.getParseFile(PHOTO) != null ? parseObject.getParseFile(PHOTO).getUrl() : "" : "";
        address = parseObject.containsKey(ADDRESS) ? parseObject.getString(ADDRESS) : "";
        title = parseObject.containsKey(TITLE) ? parseObject.getString(TITLE) : "";
        content = parseObject.containsKey(CONTENT) ? parseObject.getString(CONTENT) : "";
        date = parseObject.getCreatedAt();
        latitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLatitude() : 0;
        longitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLongitude() : 0;
        recommended = parseObject.containsKey(RECOMMENDED) && parseObject.getBoolean(RECOMMENDED);
        subcategory = parseObject.containsKey(SUBCATEGORY) ? parseObject.getString(SUBCATEGORY) : "";
        createdAt = parseObject.containsKey(CREATED_AT) ? parseObject.getString(CREATED_AT) : "";
    }
}

