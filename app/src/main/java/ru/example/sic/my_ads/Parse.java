package ru.example.sic.my_ads;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.example.sic.my_ads.models.AdContent;

import static ru.example.sic.my_ads.Parse.Constants.AD;
import static ru.example.sic.my_ads.Parse.Constants.AD_ADDRESS;
import static ru.example.sic.my_ads.Parse.Constants.AD_ALL_PHOTOS;
import static ru.example.sic.my_ads.Parse.Constants.AD_AUTHOR_ID;
import static ru.example.sic.my_ads.Parse.Constants.AD_CONTENT;
import static ru.example.sic.my_ads.Parse.Constants.AD_COORDINATES;
import static ru.example.sic.my_ads.Parse.Constants.AD_COST;
import static ru.example.sic.my_ads.Parse.Constants.AD_CREATED_AT;
import static ru.example.sic.my_ads.Parse.Constants.AD_CURRENCY;
import static ru.example.sic.my_ads.Parse.Constants.AD_EXPIRATION_DATE;
import static ru.example.sic.my_ads.Parse.Constants.AD_IS_RECOMMENDED_BY_ADMIN;
import static ru.example.sic.my_ads.Parse.Constants.AD_LIKED;
import static ru.example.sic.my_ads.Parse.Constants.AD_LIKES;
import static ru.example.sic.my_ads.Parse.Constants.AD_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.AD_SUBCATEGORY_OBJECT;
import static ru.example.sic.my_ads.Parse.Constants.AD_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.AD_VIEWED_AT;
import static ru.example.sic.my_ads.Parse.Constants.AD_VIEWS;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST_BLOCKED_USER;
import static ru.example.sic.my_ads.Parse.Constants.BLACKLIST_WHOM_BLOCKED;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_EN_TITLE;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_IS_ROOT;
import static ru.example.sic.my_ads.Parse.Constants.CATEGORY_PARENT;
import static ru.example.sic.my_ads.Parse.Constants.OBJECT_ID;
import static ru.example.sic.my_ads.Parse.Constants.PHOTOS;
import static ru.example.sic.my_ads.Parse.Constants.PHOTOS_IS_IN_USE;
import static ru.example.sic.my_ads.Parse.Constants.PHOTOS_PHOTO;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS;
import static ru.example.sic.my_ads.Parse.Constants.PROMO_ACTIONS_ACTIVE;
import static ru.example.sic.my_ads.Parse.Constants.USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_ADS_OF_USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_TYPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_FAVORITES;
import static ru.example.sic.my_ads.Parse.Constants.USER_IS_PERSON;

public final class Parse {

    public static class Request {

        public static ArrayList<ParseObject> getObjectRelationUser(String columnName) {
            try {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    ParseQuery<ParseObject> queryInRelation = currentUser.getRelation(columnName).getQuery();
                    queryInRelation.whereNotContainedIn(AD_AUTHOR_ID, getBadIds(currentUser));
                    return (ArrayList<ParseObject>) queryInRelation.find();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        public static ArrayList<ParseObject> getSearchResult(String address, String subcategory, int isPerson, int whereToSearch, String textForSearch, String costStart, String costEnd) {
            try {
                ParseQuery query = ParseQuery.getQuery(AD);
                if (!costStart.equals("")) {
                    int value = Integer.parseInt(costStart);
                    query.whereGreaterThanOrEqualTo(AD_COST, value);
                }
                if (!costEnd.equals("")) {
                    int value = Integer.parseInt(costEnd);
                    query.whereLessThanOrEqualTo(AD_COST, value);
                }
                if (!textForSearch.equals("")) {
                    if (whereToSearch == 0) {
                        ParseQuery<ParseObject> queryContent = ParseQuery.getQuery(AD);
                        ParseQuery<ParseObject> queryTitle = ParseQuery.getQuery(AD);

                        queryContent.whereMatches(AD_CONTENT, "(" + textForSearch + ")", "i");
                        queryTitle.whereMatches(AD_TITLE, "(" + textForSearch + ")", "i");

                        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
                        queries.add(queryContent);
                        queries.add(queryTitle);

                        query = ParseQuery.or(queries);
                    }
                    if (whereToSearch == 2) {
                        query.whereMatches(AD_CONTENT, "(" + textForSearch + ")", "i");
                    }
                    if (whereToSearch == 1) {
                        query.whereMatches(AD_TITLE, "(" + textForSearch + ")", "i");
                    }
                }

                if (!address.equals("")) {
                    query.whereMatches(AD_ADDRESS, "(" + address + ")", "i");
                }
                if (!subcategory.equals("")) {
                    ParseQuery categoryQuery = ParseQuery.getQuery(CATEGORY);
                    categoryQuery.whereEqualTo(CATEGORY_IS_ROOT, false);
                    ArrayList<ParseObject> subcategoryObj = (ArrayList<ParseObject>) categoryQuery.whereEqualTo(CATEGORY_EN_TITLE, subcategory).find();
                    if (subcategoryObj.size() > 0) {
                        query.whereEqualTo(AD_SUBCATEGORY_OBJECT, subcategoryObj.get(0));
                    } else {
                        //если был выбран пункт вся группа
                        query.whereContainedIn(AD_SUBCATEGORY_OBJECT, getSubGroupsList(subcategory, false));
                    }
                }

                if (isPerson != 2) {
                    ParseQuery userQuery = ParseQuery.getQuery(USER);
                    if (isPerson == 1) {
                        ParseQuery<ParseObject> queryFalse = ParseQuery.getQuery(USER);
                        ParseQuery<ParseObject> queryNull = ParseQuery.getQuery(USER);

                        queryFalse.whereEqualTo(USER_IS_PERSON, false);
                        queryNull.whereEqualTo(USER_IS_PERSON, null);

                        List<ParseQuery<ParseObject>> queriesUser = new ArrayList<>();
                        queriesUser.add(queryFalse);
                        queriesUser.add(queryNull);

                        userQuery = ParseQuery.or(queriesUser);
                    } else {
                        userQuery.whereEqualTo(USER_IS_PERSON, true);
                    }
                    List users = userQuery.find();
                    query.whereContainedIn(AD_AUTHOR_ID, users);
                }
                query.whereNotContainedIn(AD_AUTHOR_ID, getBadIds(ParseUser.getCurrentUser()));
                return (ArrayList<ParseObject>) query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new ArrayList<ParseObject>();
        }

        public static ArrayList<ParseObject> getPhotos(ParseObject ad) {
            try {
                ParseRelation<ParseObject> relation = ad.getRelation(AD_ALL_PHOTOS);
                return (ArrayList<ParseObject>) relation.getQuery().find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        public static ArrayList<ParseObject> getBadIds(ParseUser currentUser) {
            ArrayList<ParseObject> bad = new ArrayList<>();
            try {
                ParseQuery<ParseObject> whomBlockedQuery = ParseQuery.getQuery(BLACKLIST);
                whomBlockedQuery.whereEqualTo(BLACKLIST_WHOM_BLOCKED, currentUser);
                List<ParseObject> iBlock = whomBlockedQuery.find();
                for (ParseObject row : iBlock) {
                    bad.add(row.getParseObject(BLACKLIST_BLOCKED_USER));
                }

                ParseQuery<ParseObject> blockedUserQuery = ParseQuery.getQuery(BLACKLIST);
                blockedUserQuery.whereEqualTo(BLACKLIST_BLOCKED_USER, currentUser);
                List<ParseObject> meBlocked = blockedUserQuery.find();
                for (ParseObject row : meBlocked) {
                    bad.add(row.getParseObject(BLACKLIST_WHOM_BLOCKED));
                }

                return bad;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return bad;
        }

        public static ArrayList<ParseObject> getAdsBySubcategorys(ArrayList<ParseObject> subcategoryObjects, int currentListSize) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(AD);
                query.whereContainedIn(AD_SUBCATEGORY_OBJECT, subcategoryObjects);
                query.whereNotContainedIn(AD_AUTHOR_ID, getBadIds(ParseUser.getCurrentUser()));
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

        public static boolean addAd(AdContent content) {
            try {
                ParseGeoPoint point = new ParseGeoPoint(content.latitude, content.longitude);
                ParseObject row = new ParseObject(AD);

                /*ParseObject baner = new ParseObject(PROMO_ACTIONS);
                baner.put(PROMO_ACTIONS_ACTIVE, true);
                baner.put(PROMO_ACTIONS_EN_TITLE, "Test promo action!");
                baner.put(PROMO_ACTIONS_RU_TITLE, "Тестовая акция!");
*/
                ParseRelation<ParseObject> relationToPhotos = row.getRelation(AD_ALL_PHOTOS);

                ParseQuery<ParseObject> queryUser = ParseQuery.getQuery(USER);
                queryUser.whereEqualTo(OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
                ParseObject rowUser = queryUser.getFirst();
                ParseRelation<ParseObject> relationUser = rowUser.getRelation(USER_ADS_OF_USER);

                row.put(AD_AUTHOR_ID, ParseUser.getCurrentUser());
                row.put(AD_VIEWS, 0);
                row.put(AD_VIEWED_AT, new Date());
                row.put(AD_LIKES, 0);
                row.put(AD_IS_RECOMMENDED_BY_ADMIN, false);
                if (content.longitude != 0 && content.latitude != 0) {
                    row.put(AD_COORDINATES, point);
                }
                row.put(AD_CURRENCY, content.currency);
                row.put(AD_ADDRESS, content.adr);
                row.put(AD_TITLE, content.title);
                ParseQuery<ParseObject> queryCategory = ParseQuery.getQuery(CATEGORY);
                ParseObject subcategoryObjects = queryCategory.whereEqualTo(CATEGORY_EN_TITLE, content.category).getFirst();
                row.put(AD_SUBCATEGORY_OBJECT, subcategoryObjects);
                row.put(AD_CONTENT, content.desc);
                row.put(AD_COST, content.cost);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());

                switch (content.duration) {
                    case 0: {
                        cal.add(Calendar.MONTH, 1);
                        row.put(AD_EXPIRATION_DATE, cal.getTime());
                        break;
                    }
                    case 1: {
                        cal.add(Calendar.MONTH, 3);
                        row.put(AD_EXPIRATION_DATE, cal.getTime());
                        break;
                    }
                    case 2: {
                        cal.add(Calendar.YEAR, 1);
                        row.put(AD_EXPIRATION_DATE, cal.getTime());
                        break;
                    }
                }
                for (int i = 0; i < content.pictures.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    content.pictures.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] data = stream.toByteArray();
                    ParseFile file = new ParseFile("photo.jpg", data);
                    if (i == 0) {
                        row.put(AD_PHOTO, file);
                        // baner.put(PROMO_ACTIONS_ACTION_IMAGE, file);

                    }
                    ParseObject photoRow = new ParseObject(PHOTOS);
                    photoRow.put(PHOTOS_PHOTO, file);
                    photoRow.put(PHOTOS_IS_IN_USE, true);
                    photoRow.save();
                    relationToPhotos.add(photoRow);
                }
                relationUser.add(row); //привязываем объявление к пользователю
                row.save();
                //baner.save();
                rowUser.save();
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static void deleteAd(String id) {
            try {
                ParseQuery<ParseObject> queryAd = ParseQuery.getQuery(AD);
                ParseObject ad = queryAd.whereEqualTo(OBJECT_ID, id).getFirst();
                List<ParseObject> photos = ad.getRelation(AD_ALL_PHOTOS).getQuery().find();
                for (ParseObject photo : photos) {
                    photo.put(PHOTOS_IS_IN_USE, false);
                    photo.saveInBackground();
                }
                ad.deleteInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public static void increaseViews(ParseObject object) {
            object.increment(AD_VIEWS);
            object.put(AD_VIEWED_AT, new Date());
            object.saveInBackground();
        }

    }

    public static class Data {
        public static ArrayList<ParseObject> categoryList = new ArrayList<>();
        public static ArrayList<ParseObject> categoryAds = new ArrayList<>();
        public static ArrayList<ParseObject> my = new ArrayList<>();
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