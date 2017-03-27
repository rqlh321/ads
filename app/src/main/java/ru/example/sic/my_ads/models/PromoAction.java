package ru.example.sic.my_ads.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;

public class PromoAction implements Serializable {
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

}
