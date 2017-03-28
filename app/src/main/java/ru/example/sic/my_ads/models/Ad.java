package ru.example.sic.my_ads.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import java.util.Date;

public class Ad implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.currency);
        dest.writeString(this.authorId);
        dest.writeValue(this.cost);
        dest.writeValue(this.views);
        dest.writeString(this.address);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
        dest.writeValue(this.recommended);
        dest.writeString(this.subcategory);
        dest.writeString(this.createdAt);
        dest.writeString(this.photo);
    }

    protected Ad(Parcel in) {
        this.id = in.readString();
        this.currency = in.readString();
        this.authorId = in.readString();
        this.cost = (Integer) in.readValue(Integer.class.getClassLoader());
        this.views = (Integer) in.readValue(Integer.class.getClassLoader());
        this.address = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.recommended = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.subcategory = in.readString();
        this.createdAt = in.readString();
        this.photo = in.readString();
    }

    public static final Parcelable.Creator<Ad> CREATOR = new Parcelable.Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel source) {
            return new Ad(source);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };
}

