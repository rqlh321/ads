package ru.example.sic.my_ads.models;

import com.parse.ParseObject;

import java.io.Serializable;

public class Category implements Serializable {
    public static final String PARENT = "parent";
    public static final String EN = "en";
    public static final String RU = "ru";
    public String en;
    public String parent;
    public String ru;

    public Category(ParseObject parseObject) {
        en = parseObject.containsKey(EN) ? parseObject.getString(EN) : null;
        ru = parseObject.containsKey(RU) ? parseObject.getString(RU) : null;
        parent = parseObject.containsKey(PARENT) ? parseObject.getString(PARENT) : null;
    }

}

