package ru.example.sic.my_ads.models;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;

public class Ad implements Serializable {
    public static final String CURRENCY = "currensy";
    public static final String AUTHOR_ID = "authorID";
    public static final String COST = "cost";
    public static final String PHOTO = "photo";
    public static final String TITLE = "title";
    public static final String VIEWS = "views";
    public static final String ADDRESS = "address";
    public static final String CONTENT = "content";
    public static final String COORDINATES = "coordinates";
    public static final String RECOMMENDED = "recommended";
    public static final String SUBCATEGORY = "subcategory";
    public static final String CREATED_AT = "createdAt";

    public String id;
    public String currency;
    public String authorId;
    public Integer cost;
    public Integer views;
    public String address;
    public String title;
    public String content;
    public Date date;
    public Double longitude;
    public Double latitude;
    public Boolean recommended;
    public String subcategory;
    public String createdAt;
    public String photo;

    public Ad(ParseObject parseObject) {
        id = parseObject.getObjectId();
        currency = parseObject.containsKey(CURRENCY) ? parseObject.getString(CURRENCY) : null;
        authorId = parseObject.containsKey(AUTHOR_ID) ? parseObject.getString(AUTHOR_ID) : null;
        cost = parseObject.containsKey(COST) ? parseObject.getInt(COST) : null;
        views = parseObject.containsKey(VIEWS) ? parseObject.getInt(VIEWS) : null;
        photo = parseObject.containsKey(PHOTO) ? parseObject.getParseFile(PHOTO).getUrl() : null;
        address = parseObject.containsKey(ADDRESS) ? parseObject.getString(ADDRESS) : null;
        title = parseObject.containsKey(TITLE) ? parseObject.getString(TITLE) : null;
        content = parseObject.containsKey(CONTENT) ? parseObject.getString(CONTENT) : null;
        date = parseObject.getCreatedAt();
        latitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLatitude() : null;
        longitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLongitude() : null;
        recommended = parseObject.containsKey(RECOMMENDED) && parseObject.getBoolean(RECOMMENDED);
        subcategory = parseObject.containsKey(SUBCATEGORY) ? parseObject.getString(SUBCATEGORY) : null;
        createdAt = parseObject.containsKey(CREATED_AT) ? parseObject.getString(CREATED_AT) : null;
    }

    public void refresh(ParseObject parseObject) {
        id = parseObject.getObjectId();
        currency = parseObject.containsKey(CURRENCY) ? parseObject.getString(CURRENCY) : currency;
        authorId = parseObject.containsKey(AUTHOR_ID) ? parseObject.getString(AUTHOR_ID) : authorId;
        cost = parseObject.containsKey(COST) ? parseObject.getInt(COST) : cost;
        views = parseObject.containsKey(VIEWS) ? parseObject.getInt(VIEWS) : views;
        photo = parseObject.containsKey(PHOTO) ? parseObject.getParseFile(PHOTO).getUrl() : photo;
        address = parseObject.containsKey(ADDRESS) ? parseObject.getString(ADDRESS) : address;
        title = parseObject.containsKey(TITLE) ? parseObject.getString(TITLE) : title;
        content = parseObject.containsKey(CONTENT) ? parseObject.getString(CONTENT) : content;
        latitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLatitude() : latitude;
        longitude = parseObject.containsKey(COORDINATES) ? parseObject.getParseGeoPoint(COORDINATES).getLongitude() : longitude;
        recommended = parseObject.containsKey(RECOMMENDED) && parseObject.getBoolean(RECOMMENDED);
        subcategory = parseObject.containsKey(SUBCATEGORY) ? parseObject.getString(SUBCATEGORY) : subcategory;
        createdAt = parseObject.containsKey(CREATED_AT) ? parseObject.getString(CREATED_AT) : createdAt;
    }

}

