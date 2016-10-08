package ru.example.sic.my_ads.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    private String en;
    private String ru;

    public Category(String en, String ru) {
        this.en = en;
        this.ru = ru;
    }

    protected Category(Parcel in) {
        en = in.readString();
        ru = in.readString();
    }

    public String getEn() {
        return en;
    }

    public String getRu() {
        return ru;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(en);
        parcel.writeString(ru);
    }
}

