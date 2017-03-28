package ru.example.sic.my_ads.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;

public class PromoAction implements Parcelable {
    public static final String IMAGE = "actionImage";
    public static final String RU = "ru";
    public static final String EN = "en";

    public String ru;
    public String en;
    public Bitmap bitmap;

    public PromoAction(ParseObject parseObject) {
        try {
            byte[] dataPic = parseObject.getParseFile(IMAGE).getData();
            bitmap = BitmapFactory.decodeByteArray(dataPic, 0, dataPic.length);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ru = parseObject.containsKey(RU) ? parseObject.getString(RU) : "";
        en = parseObject.containsKey(EN) ? parseObject.getString(EN) : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ru);
        dest.writeString(this.en);
        dest.writeParcelable(this.bitmap, flags);
    }

    protected PromoAction(Parcel in) {
        this.ru = in.readString();
        this.en = in.readString();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<PromoAction> CREATOR = new Parcelable.Creator<PromoAction>() {
        @Override
        public PromoAction createFromParcel(Parcel source) {
            return new PromoAction(source);
        }

        @Override
        public PromoAction[] newArray(int size) {
            return new PromoAction[size];
        }
    };
}
