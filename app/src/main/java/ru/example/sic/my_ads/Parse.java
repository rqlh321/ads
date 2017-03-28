package ru.example.sic.my_ads;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_AUTHOR_ID;
import static ru.example.sic.my_ads.Parse.Constants.AD_CONTENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CREATED_AT;
import static ru.example.sic.my_ads.Parse.Constants.AD_SUBCATEGORY_OBJECT;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.AD_VIEWS;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST_BLOCKED_USER;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST_WHOM_BLOCKED;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_EN_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_IS_ROOT;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_PARENT;
import static ru.example.sic.my_ads.Parse.Constants.USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_IS_PERSON;

public final class Parse {

    public static class Request {


        public static ArrayList<ParseObject> getAdsBySubcategorys(ArrayList<ParseObject> subcategoryObjects, int currentListSize) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(AD);
                query.whereContainedIn(AD_SUBCATEGORY_OBJECT, subcategoryObjects);
                query.orderByDescending(AD_CREATED_AT);
                query.setSkip(currentListSize);
                query.setLimit(10);
                return (ArrayList<ParseObject>) query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        public static ArrayList<ParseObject> getSubGroupsList(String group, boolean isCategory) {
            try {
                ParseQuery<ParseObject> queryRowSubGroupsList = ParseQuery.getQuery(CATEGORY);
                if (isCategory) {
                    queryRowSubGroupsList.whereMatches(CATEGORY_PARENT, "(" + group + ")", "i");
                } else {
                    queryRowSubGroupsList.whereEqualTo(CATEGORY_IS_ROOT, false);
                    queryRowSubGroupsList.whereEqualTo(CATEGORY_EN_TITLE, group);
                }
                queryRowSubGroupsList.setLimit(1000);
                return (ArrayList<ParseObject>) queryRowSubGroupsList.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

    }

    public static final class Constants {
        public static final String OBJECT_ID = "objectId";

        public static final String USER = "_User";
        public static final String USER_MIDDLE_NAME = "middleName";
        public static final String USER_LAST_NAME = "lastName";
        public static final String USER_ADS_OF_USER = "AdsOfUser";
        public static final String USER_FAVORITES = "Favorites";
        public static final String USER_AVATAR = "avatar";
        public static final String USER_FIRST_NAME = "firstName";
        public static final String USER_IS_PERSON = "isPerson";

        public static final String AD = "Ad";
        public static final String AD_CURRENCY = "currensy";
        public static final String AD_ALL_PHOTOS = "allPhotos";
        public static final String AD_AUTHOR_ID = "authorID";
        public static final String AD_COST = "cost";
        public static final String AD_VIEWS = "views";
        public static final String AD_PHOTO = "photo";
        public static final String AD_ADDRESS = "address";
        public static final String AD_VIEWED_AT = "viewedAt";
        public static final String AD_TITLE = "title";
        public static final String AD_CONTENT = "content";
        public static final String AD_CREATED_AT = "createdAt";
        public static final String AD_COORDINATES = "coordinates";
        public static final String AD_LIKES = "likes";
        public static final String AD_SUBCATEGORY_OBJECT = "SubcategoryObj";
        public static final String AD_IS_RECOMMENDED_BY_ADMIN = "isRecommendedByAdmin";
        public static final String AD_TERMS_OF_PAYMENT = "termsOfPayment";
        public static final String AD_LIKED = "likers";
        public static final String AD_EXPIRATION_DATE = "expirationDate";

        public static final String BLACKLIST = "Blacklist";
        public static final String BLACKLIST_WHOM_BLOCKED = "whomBlocked";
        public static final String BLACKLIST_BLOCKED_USER = "blockedUser";

        public static final String CATEGORY = "Category";
        public static final String CATEGORY_PARENT = "Parent";
        public static final String CATEGORY_IS_ROOT = "isRoot";
        public static final String CATEGORY_EN_TITLE = "ENTitle";
        public static final String CATEGORY_RU_TITLE = "RUTitle";

        public static final String USER_CONTACTS = "UserContacts";
        public static final String USER_CONTACTS_OF_USER = "ofUser";
        public static final String USER_CONTACTS_COUNTRY_CODE = "countryCode";
        public static final String USER_CONTACTS_VALUE = "contact";
        public static final String USER_CONTACTS_OF_TYPE = "ofType";
        public static final String USER_CONTACTS_IS_VISIBLE = "isVisible";

        public static final String PHOTOS = "Photos";
        public static final String PHOTOS_IS_IN_USE = "isInUse";
        public static final String PHOTOS_PHOTO = "photo";

        public static final String USERS_AD_REPORT = "UsersAdReport";
        public static final String USERS_AD_REPORT_REPORTED_AD = "reportedAd";
        public static final String USERS_AD_REPORT_USERS_COMMENT = "usersComment";

        public static final String APP_REPORT = "AppReport";
        public static final String APP_REPORT_FROM_USER = "fromUser";
        public static final String APP_REPORT_REPORT_FROM_USER = "reportFromUser";

        public static final String APP_SUPPORT = "AppSupport";
        public static final String APP_SUPPORT_FROM_USER = "fromUser";
        public static final String APP_SUPPORT_QUESTION_FROM_USER = "questionFromUser";

        public static final String APP_REVIEW = "AppReview";
        public static final String APP_REVIEW_FROM_USER = "fromUser";
        public static final String APP_REVIEW_REVIEW_FROM_USER = "reviewFromUser";

        public static final String PROMO_ACTIONS = "PromoActions";
        public static final String PROMO_ACTIONS_ACTION_IMAGE = "actionImage";
        public static final String PROMO_ACTIONS_ACTIVE = "active";
        public static final String PROMO_ACTIONS_RU_TITLE = "RUTitle";
        public static final String PROMO_ACTIONS_EN_TITLE = "ENTitle";


    }

}