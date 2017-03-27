package ru.example.sic.my_ads.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

public class User implements Parcelable {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String PERSON = "person";

    public String name;
    public String email;
    public String phone;
    public boolean person;

    public User(ParseObject parseObject) {
        name = parseObject.containsKey(NAME) ? parseObject.getString(NAME) : "";
        email = parseObject.containsKey(EMAIL) ? parseObject.getString(EMAIL) : "";
        phone = parseObject.containsKey(PHONE) ? parseObject.getString(PHONE) : "";
        person = !parseObject.containsKey(PERSON) || parseObject.getBoolean(PERSON);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeByte(this.person ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.person = in.readByte() != 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
