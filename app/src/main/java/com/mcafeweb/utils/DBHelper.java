package com.mcafeweb.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.Models.UserModel;
import com.mcafeweb.utils.Helper;

/**
 * Created by Balpreet on 10/9/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    String TAG = "DBHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mCafeDatabase";
    public static final String TABLE_USER_GROUP = "tblusergroup";
    public static final String TABLE_BLOG = "tblblog";
    public static final String TABLE_USER_BLOG = "tbluserblog";
    public static final String TABLE_INTERESTS = "tblinterests";
    public static final String TABLE_USER_INTERESTS = "tbluserinterests";
    public static final String TABLE_GROUP_CONTENT = "tblgroupcontent";

    public static final String TABLE_CONTRIBUTOR_I_FOLLOW = "tblcontributorifollow";
    public static final String TABLE_USER_PROFILE = "tbluserprofile";

    public static final String TABLE_FAV_BLOG_LIST = "tblfavbloglist";
    public static final String TABLE_FAV_BLOG_LIST_CONTENT = "tblfavbloglistcontent";
    public static final String TABLE_MY_CURATED_BLOGS = "tblmycuratedblogs";


    public static final String COLUMN_USER_ID = "userID";

    public static final String COLUMN_GROUP_ID = "groupID";
    public static final String COLUMN_GROUP_CREATED_BY = "groupCreatedBy";
    public static final String COLUMN_GROUP_FOLLOWED_ON = "groupFollowedOn";

    public static final String COLUMN_BLOG_ID = "blogID";
    public static final String COLUMN_BLOG_TITLE = "blogTitle";
    public static final String COLUMN_BLOG_BRIEF = "blogBrief";
    public static final String COLUMN_BLOG_IMAGE = "blogImage";
    public static final String COLUMN_BLOG_CATEGORY = "blogCategory";
    public static final String COLUMN_BLOG_LINK = "blogLink";
    public static final String COLUMN_BLOG_LIKES = "blogLikes";
    public static final String COLUMN_BLOG_VIEWS = "blogViews";
    public static final String COLUMN_BLOG_SHARES = "blogShares";
    public static final String COLUMN_BLOG_SHARED_BY = "blogSharedBy";
    public static final String COLUMN_BLOG_SHARED_DATE = "blogSharedDate";
    public static final String COLUMN_BLOG_CANONICAL_LINK = "blogCanonicalLink";


    public static final String COLUMN_GROUP_CONTENT_ID = "groupContentID";
    public static final String COLUMN_GROUP_CONTENT = "groupContent";
    public static final String COLUMN_GROUP_CONTENT_LIKES = "groupContentLikes";
    public static final String COLUMN_GROUP_CONTENT_TIME = "groupContentTime";
    public static final String COLUMN_GROUP_FIRST_NAME = "groupUserFirstName";

    public static final String COLUMN_INTEREST_ID = "interestID";
    public static final String COLUMN_INTEREST_NAME = "interestName";


    public static final String COLUMN_CONTRIBUTOR_FIRST_NAME = "contributorFirstName";
    public static final String COLUMN_CONTRIBUTOR_LAST_NAME = "contributorLastName";


    public static final String COLUMN_USER_FIRST_NAME = "userFirstName";
    public static final String COLUMN_USER_LAST_NAME = "userLastName";
    public static final String COLUMN_USER_EMAIL = "userEmail";
    public static final String COLUMN_USER_PASSWORD = "userPassword";
    public static final String COLUMN_USER_CREATED_ON = "userCreatedOn";
    public static final String COLUMN_USER_BIO = "userBio";
    public static final String COLUMN_USER_CITY = "userCity";
    public static final String COLUMN_USER_COUNTRY = "userCountry";
    public static final String COLUMN_USER_EXPERIENCE = "userExperience";
    public static final String COLUMN_USER_CERTIFICATIONS = "userCertifications";
    public static final String COLUMN_USER_BRIEF_PROFILE = "userBriefProfile";

    public static final String COLUMN_USER_ROLE = "userRole";

    public static final String COLUMN_FAV_BLOG_LIST_ID = "favBlogListID";
    public static final String COLUMN_FAV_BLOG_LIST_NAME = "favBlogListName";


    public Cursor cursor;

    private String CREATE_TABLE_USER_GROUP = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_GROUP + " ( " +
            COLUMN_USER_ID + " NUMBER," +
            COLUMN_GROUP_ID + " NUMBER," +
            COLUMN_GROUP_CREATED_BY + " NUMBER," +
            COLUMN_GROUP_FOLLOWED_ON + " DATETIME," +
            " PRIMARY KEY (" + COLUMN_USER_ID + " , " + COLUMN_GROUP_ID + "))";

    private String CREATE_TABLE_BLOG = "CREATE TABLE IF NOT EXISTS " + TABLE_BLOG + " ( " +
            COLUMN_BLOG_ID + " NUMBER PRIMARY KEY," +
            COLUMN_BLOG_LINK + " VARCHAR," +
            COLUMN_BLOG_TITLE + " VARCHAR," +
            COLUMN_BLOG_BRIEF + " VARCHAR," +
            COLUMN_BLOG_IMAGE + " BLOB," +
            COLUMN_BLOG_CATEGORY + " NUMBER," +
            COLUMN_BLOG_LIKES + " NUMBER," +
            COLUMN_BLOG_VIEWS + " NUMBER," +
            COLUMN_BLOG_SHARES + " NUMBER," +
            COLUMN_BLOG_SHARED_BY + " NUMBER," +
            COLUMN_BLOG_SHARED_DATE + " VARCHAR," +
            COLUMN_BLOG_CANONICAL_LINK + " VARCHAR)";

    private String CREATE_TABLE_FAV_BLOG_LIST_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_FAV_BLOG_LIST_CONTENT + " ( " +
            COLUMN_BLOG_ID + " NUMBER PRIMARY KEY," +
            COLUMN_BLOG_LINK + " VARCHAR," +
            COLUMN_BLOG_TITLE + " VARCHAR," +
            COLUMN_BLOG_BRIEF + " VARCHAR," +
            COLUMN_BLOG_IMAGE + " BLOB," +
            COLUMN_BLOG_CATEGORY + " NUMBER," +
            COLUMN_BLOG_LIKES + " NUMBER," +
            COLUMN_BLOG_VIEWS + " NUMBER," +
            COLUMN_BLOG_SHARES + " NUMBER," +
            COLUMN_BLOG_SHARED_BY + " NUMBER," +
            COLUMN_BLOG_SHARED_DATE + " VARCHAR," +
            COLUMN_BLOG_CANONICAL_LINK + " VARCHAR)";

    private String CREATE_TABLE_MY_CURATED_BLOGS = "CREATE TABLE IF NOT EXISTS " + TABLE_MY_CURATED_BLOGS + " ( " +
            COLUMN_BLOG_ID + " NUMBER PRIMARY KEY," +
            COLUMN_BLOG_LINK + " VARCHAR," +
            COLUMN_BLOG_TITLE + " VARCHAR," +
            COLUMN_BLOG_BRIEF + " VARCHAR," +
            COLUMN_BLOG_IMAGE + " BLOB," +
            COLUMN_BLOG_CATEGORY + " NUMBER," +
            COLUMN_BLOG_LIKES + " NUMBER," +
            COLUMN_BLOG_VIEWS + " NUMBER," +
            COLUMN_BLOG_SHARES + " NUMBER," +
            COLUMN_BLOG_SHARED_BY + " NUMBER," +
            COLUMN_BLOG_SHARED_DATE + " VARCHAR," +
            COLUMN_BLOG_CANONICAL_LINK + " VARCHAR)";

    private String CREATE_TABLE_GROUP_CONTENT = "CREATE TABLE IF NOT EXISTS " + TABLE_GROUP_CONTENT + " ( " +
            COLUMN_GROUP_CONTENT_ID + " NUMBER PRIMARY KEY," +
            COLUMN_GROUP_CONTENT + " VARCHAR," +
            COLUMN_USER_ID + " NUMBER," +
            COLUMN_GROUP_ID + " NUMBER," +
            COLUMN_GROUP_CONTENT_LIKES + " NUMBER," +
            COLUMN_GROUP_CONTENT_TIME + " DATETIME," +
            COLUMN_USER_FIRST_NAME + " VARCHAR)";

    private String CREATE_TABLE_INTERESTS = "CREATE TABLE IF NOT EXISTS " + TABLE_INTERESTS + " ( " +
            COLUMN_INTEREST_ID + " NUMBER PRIMARY KEY," +
            COLUMN_INTEREST_NAME + " VARCHAR)";

    private String CREATE_TABLE_USER_INTERESTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_INTERESTS + " ( " +
            COLUMN_INTEREST_ID + " NUMBER PRIMARY KEY)";

    private String CREATE_TABLE_CONTRIBUTOR_I_FOLLOW = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTRIBUTOR_I_FOLLOW + " ( " +
            COLUMN_USER_ID + " NUMBER PRIMARY KEY," +
            COLUMN_CONTRIBUTOR_FIRST_NAME + " VARCHAR," +
            COLUMN_CONTRIBUTOR_LAST_NAME + " VARCHAR)";

    private String CREATE_TABLE_USER_PROFILE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_PROFILE + " ( " +
            COLUMN_USER_ID + " NUMBER PRIMARY KEY," +
            COLUMN_USER_FIRST_NAME + " VARCHAR," +
            COLUMN_USER_LAST_NAME + " VARCHAR," +
            COLUMN_USER_EMAIL + " VARCHAR," +
            COLUMN_USER_BIO + " VARCHAR," +
            COLUMN_USER_CITY + " VARCHAR," +
            COLUMN_USER_COUNTRY + " VARCHAR," +
            COLUMN_USER_EXPERIENCE + " VARCHAR," +
            COLUMN_USER_CERTIFICATIONS + " VARCHAR," +
            COLUMN_USER_BRIEF_PROFILE + " VARCHAR," +
            COLUMN_USER_PASSWORD + " VARCHAR," +
            COLUMN_USER_CREATED_ON + " VARCHAR," +
            COLUMN_USER_ROLE + " VARCHAR)";


    private String CREATE_TABLE_FAV_BLOG_LIST = "CREATE TABLE IF NOT EXISTS " + TABLE_FAV_BLOG_LIST + " ( " +
            COLUMN_FAV_BLOG_LIST_ID + " NUMBER PRIMARY KEY," +
            COLUMN_FAV_BLOG_LIST_NAME + " VARCHAR)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v(TAG, "DBHELPER OBJECT CREATED");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(CREATE_TABLE_USER_GROUP);
        db.execSQL(CREATE_TABLE_BLOG);
        db.execSQL(CREATE_TABLE_GROUP_CONTENT);
        db.execSQL(CREATE_TABLE_INTERESTS);
        db.execSQL(CREATE_TABLE_USER_INTERESTS);
        db.execSQL(CREATE_TABLE_CONTRIBUTOR_I_FOLLOW);
        db.execSQL(CREATE_TABLE_USER_PROFILE);
        db.execSQL(CREATE_TABLE_FAV_BLOG_LIST);
        db.execSQL(CREATE_TABLE_FAV_BLOG_LIST_CONTENT);
        db.execSQL(CREATE_TABLE_MY_CURATED_BLOGS);

        Log.v(TAG, "TABLE CREATED");
    }

    public void createFirst() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_USER_EMAIL + " FROM " + TABLE_USER_PROFILE, null);
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO " + TABLE_USER_PROFILE + " Values(" +
                        "-1," +
                        "'name'," +
                        "'name'," +
                        "'email'," +
                        "'bio'," +
                        "'city'," +
                        "'country'," +
                        "'experience'," +
                        "'certification'," +
                        "'brief'," +
                        "'password'," +
                        "'createdon'," +
                        "'role');");
                Log.v(TAG, "User Profile Table created first time");
            } else {
                Log.v(TAG, "User Profile Table has already been created");
            }


            cursor = db.rawQuery("SELECT " + COLUMN_FAV_BLOG_LIST_ID + " FROM " + TABLE_FAV_BLOG_LIST, null);
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO " + TABLE_FAV_BLOG_LIST + " Values(" +
                        "-1," +
                        "'name');");
                Log.v(TAG, "Fav Blog Table created first time");
            } else {
                Log.v(TAG, "Fav Blog Table has already been created");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP);
        // Create tables again
        onCreate(db);

    }

    public void sendMessageToGroup(int groupcontentid, int userid, int groupid, String groupcontent, int groupcontentlikes, String groupcontenttime, String user_firstName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Posting Message to Group");

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_CONTENT_ID, groupcontentid);
        values.put(COLUMN_GROUP_CONTENT, groupcontent);
        values.put(COLUMN_GROUP_CONTENT_LIKES, groupcontentlikes);
        values.put(COLUMN_USER_ID, userid);
        values.put(COLUMN_GROUP_ID, groupid);
        values.put(COLUMN_GROUP_CONTENT_TIME, groupcontenttime);
        values.put(COLUMN_USER_FIRST_NAME, user_firstName);

        long result = db.insert(TABLE_GROUP_CONTENT, null, values);
        Log.v(TAG, "Insert = " + result);
        if (db != null) db.close();

    }

    public int checkGroupActivityForData(int groupid, int userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = 0;
        try {
            String query = "SELECT " + COLUMN_GROUP_CONTENT_ID + " FROM " + TABLE_GROUP_CONTENT + " WHERE " +
                    COLUMN_USER_ID + " = " + userid + " AND " +
                    COLUMN_GROUP_ID + " = " + groupid;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            result = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }

    public Object[][] getGroupContentRow(int userid, int groupid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Object[][] result = null;

        try {
            String query = "SELECT * FROM " + TABLE_GROUP_CONTENT + " WHERE " +
                    COLUMN_USER_ID + " = " + userid + " AND " +
                    COLUMN_GROUP_ID + " = " + groupid +
                    " ORDER BY " + COLUMN_GROUP_CONTENT_ID + " DESC ";
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = new Object[cursor.getCount()][7];
                for (int i = 0; i < result.length; i++) {

                    result[i][0] = cursor.getInt(0);
                    result[i][1] = cursor.getString(1);
                    result[i][2] = cursor.getString(2);
                    result[i][3] = cursor.getString(3);
                    result[i][4] = cursor.getBlob(4);
                    result[i][5] = cursor.getInt(5);
                    result[i][6] = cursor.getInt(6);
                    result[i][7] = cursor.getInt(7);
                    result[i][8] = cursor.getInt(8);
                    result[i][9] = cursor.getInt(9);
                    result[i][10] = cursor.getString(10);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public InterestModel[] getInterestList() {
        SQLiteDatabase db = this.getReadableDatabase();
        InterestModel[] result = null;
        try {
            String query = "SELECT * FROM " + TABLE_INTERESTS;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = new InterestModel[cursor.getCount()];
                Log.v(TAG, "Interest Count : " + result.length + " Column Name 1 : " + cursor.getColumnName(0) + " Column Name 2 : " + cursor.getColumnName(1));
                for (int i = 0; i < result.length; i++) {
                    result[i] = new InterestModel();
                    result[i].setInterestID(cursor.getInt(0));
                    result[i].setInterestName(cursor.getString(1));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }


    public long addInterestToTable(int interestid, String interestName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding Interest to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_INTEREST_ID, interestid);
        values.put(COLUMN_INTEREST_NAME, interestName);

        try {
            result = db.insert(TABLE_INTERESTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Insert = " + result);
        if (db != null) db.close();
        return result;
    }

    public int[] getMyInterests() {
        SQLiteDatabase db = this.getReadableDatabase();
        int[] result = null;

        try {
            String query = "SELECT * FROM " + TABLE_USER_INTERESTS;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = new int[cursor.getCount()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = cursor.getInt(0);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    /*
    public UserModel[] getContributorsIFollow(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserModel[] result = null;

        try {
            String query = "SELECT * FROM " + TABLE_NAME;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = new UserModel[cursor.getCount()];
                for (int i = 0; i < result.length; i++) {
                    result[i]= new UserModel();
                    result[i].setUserID(cursor.getInt(0));
                    result[i].setUserFirstName(cursor.getString(1));
                    result[i].setUserLastName(cursor.getString(2));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if(db!=null)                 db.close();
        }
        return result;
    }

    public long addContributorIFollow( UserModel model,String TABLE_NAME) {

        SQLiteDatabase db = null;
        long result = -1;

        if (checkExistingUser(model.getUserID(),TABLE_NAME)) {
            Log.v(TAG, "User ALready Exists");
        } else {
            db =  this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_USER_ID, model.getUserID());
                values.put(COLUMN_CONTRIBUTOR_FIRST_NAME, model.getUserFirstName());
                values.put(COLUMN_CONTRIBUTOR_LAST_NAME, model.getUserLastName());

                result = db.insert(TABLE_CONTRIBUTOR_I_FOLLOW, null, values);
                Log.v(TAG, "Add Contributor = " + result);
            }
            if(db!=null)
                if(db!=null)                 db.close();
        return result;
    }
    */

    public void removeAllUserInterests() {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Deleted from Table User Interests : " + db.delete(TABLE_USER_INTERESTS, null, null));
        if (db != null) db.close();

    }

    public long addUserInterest(int interestid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User Interest to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_INTEREST_ID, interestid);

        String query = "SELECT " + COLUMN_INTEREST_ID + " FROM " + TABLE_USER_INTERESTS + " WHERE " + COLUMN_INTEREST_ID + " = " + interestid;
        Log.v(TAG, "Query = " + query);
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {

        } else {
            result = db.insert(TABLE_USER_INTERESTS, null, values);
        }
        if (db != null) db.close();
        return result;
    }

    public int getInterestID(String interestName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String query = "SELECT " + COLUMN_INTEREST_ID + " FROM " + TABLE_INTERESTS + " WHERE " + COLUMN_INTEREST_NAME + " = '" + interestName + "'";
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public long setUserEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User Email to Table");

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        long result = -1;
        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public long setUserFirstName(String firstname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User First name to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRST_NAME, firstname);

        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public long setUserLastName(String lastname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User Last name to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_LAST_NAME, lastname);

        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public long setUserPassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User Password to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, password);

        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public long setUserRole(String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User First name to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ROLE, role);

        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public long setUserID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding User First name to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, id);

        result = db.update(TABLE_USER_PROFILE, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public String getUserEmail() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        try {
            String query = "SELECT " + COLUMN_USER_EMAIL + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            result = cursor.getString(0);
            Log.v(TAG, "Email : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public String getUserPassword() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        try {
            String query = "SELECT " + COLUMN_USER_PASSWORD + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            result = cursor.getString(0);
            Log.v(TAG, "Password : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public String getUserFirstName() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        try {
            String query = "SELECT " + COLUMN_USER_FIRST_NAME + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public String getUserLastName() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        try {
            String query = "SELECT " + COLUMN_USER_LAST_NAME + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public String getUserRole() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = null;
        try {
            String query = "SELECT " + COLUMN_USER_ROLE + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public int getUserID() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USER_PROFILE;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {
                result = cursor.getInt(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();

            if (db != null) db.close();
        }
        return result;
    }

    public int getInterestCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String query = "SELECT * FROM " + TABLE_USER_INTERESTS;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public long setFavBlogList(int id, String listName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Adding Fav Blog List ID to Table");
        long result = -1;

        ContentValues values = new ContentValues();
        values.put(COLUMN_FAV_BLOG_LIST_ID, id);
        values.put(COLUMN_FAV_BLOG_LIST_NAME, listName);

        result = db.update(TABLE_FAV_BLOG_LIST, values, null, null);
        Log.v(TAG, "Row Updated");
        if (db != null) db.close();
        return result;
    }

    public int getFavBlogListId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String query = "SELECT " + COLUMN_FAV_BLOG_LIST_ID + " FROM " + TABLE_FAV_BLOG_LIST;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                result = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    public void printTable(String TableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT * FROM " + TableName;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                int columncount = cursor.getColumnCount();
                for (int i = 0; i < cursor.getCount(); i++) {
                    for (int j = 0; j < columncount; j++) {
                        Log.v(TAG, "Column  : " + cursor.getColumnName(j) + " Value : " + cursor.getString(j));
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
    }

    /*
    public int checkPreviousBlogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        try {
            String query = "SELECT " + COLUMN_BLOG_ID + " FROM " + TABLE_BLOG;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            result = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if(db!=null)                 db.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }



    public int checkPreviousContributors() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = 0;
        try {
            String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_CONTRIBUTOR_I_FOLLOW;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            result = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        Log.v(TAG, "Result = " + result);
        if(db!=null)                 db.close();
        return result;
    }

    public BlogModel[] getBlogRows( String TableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        BlogModel[] result = null;

        try {
            String query = "SELECT * FROM " + TableName +
                    " ORDER BY " + COLUMN_BLOG_ID + " DESC ";
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = new BlogModel[cursor.getCount()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = new BlogModel();
                    result[i].setBlogID(cursor.getInt(0));
                    result[i].setBlogUrl(cursor.getString(1));
                    result[i].setTitle(cursor.getString(2));
                    result[i].setBlogBrief(cursor.getString(3));
                    result[i].setBlogImage(Helper.Instance.getBitmapFromByteArray(cursor.getBlob(4)));
                    result[i].setBlogCategory(cursor.getInt(5));
                    result[i].setBlogLikes(cursor.getInt(6));
                    result[i].setBlogViews(cursor.getInt(7));
                    result[i].setBlogShares(cursor.getInt(8));
                    result[i].setBlogSharedByUser(cursor.getInt(9));
                    result[i].setBlogSharedDate(cursor.getString(10));
                    result[i].setCanonicalURL(cursor.getString(11));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if(db!=null)                 db.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }

    public BlogModel getBlogRow( String TableName, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        BlogModel model = null;

        try {
            String query = "SELECT * FROM " + TableName + " WHERE " + COLUMN_BLOG_ID + " = " + id;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                model = new BlogModel();
                model.setBlogID(cursor.getInt(0));
                model.setBlogUrl(cursor.getString(1));
                model.setTitle(cursor.getString(2));
                model.setBlogBrief(cursor.getString(3));
                model.setBlogImage(Helper.Instance.getBitmapFromByteArray(cursor.getBlob(4)));
                model.setBlogCategory(cursor.getInt(5));
                model.setBlogLikes(cursor.getInt(6));
                model.setBlogViews(cursor.getInt(7));
                model.setBlogShares(cursor.getInt(8));
                model.setBlogSharedByUser(cursor.getInt(9));
                model.setBlogSharedDate(cursor.getString(10));
                model.setCanonicalURL(cursor.getString(11));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if(db!=null)                 db.close();

        }
        return model;
    }

    public boolean checkExistingBlog( int blogid,String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        try {
            String query = "SELECT " + COLUMN_BLOG_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_BLOG_ID + " = " + blogid;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if(db!=null)                 db.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }
    */

    public boolean checkExistingUser(int userid, String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        try {
            String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = " + userid;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            if (db != null) db.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }

/*
    public boolean checkExistingFavBlog( int blogid,String TABLE_NAME) {
        boolean result = false;
        try {
            String query = "SELECT " + COLUMN_BLOG_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_BLOG_ID + " = " + blogid;
            Log.v(TAG, "Query = " + query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if (cursor.getCount() != 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        Log.v(TAG, "Result = " + result);
        return result;
    }


    public void addBlogToTable( BlogModel blogModel,String TABLE_NAME) {

        SQLiteDatabase db = null;
        if (checkExistingBlog(blogModel.getBlogID(),TABLE_NAME)) {
            Log.v(TAG, "BLog ALready Exists");
        } else {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_BLOG_ID, blogModel.getBlogID());

            values.put(COLUMN_BLOG_LINK, blogModel.getBlogUrl());
            if(!blogModel.getTitle().equals(""))
            {
                values.put(COLUMN_BLOG_TITLE, blogModel.getTitle());
            }
            if(!blogModel.getCanonicalURL().equals("")) {
                values.put(COLUMN_BLOG_CANONICAL_LINK, blogModel.getCanonicalURL());
            }
            if (!blogModel.getBlogBrief().equals("")) {
                values.put(COLUMN_BLOG_BRIEF, blogModel.getBlogBrief());
            }
            if (blogModel.getBlogImage() != null) {
                values.put(COLUMN_BLOG_IMAGE, Helper.Instance.getByteArrayFromBitmap(blogModel.getBlogImage()));
            }
            values.put(COLUMN_BLOG_CATEGORY, blogModel.getBlogCategory());
            values.put(COLUMN_BLOG_LIKES, blogModel.getBlogLikes());
            values.put(COLUMN_BLOG_VIEWS, blogModel.getBlogViews());
            values.put(COLUMN_BLOG_SHARES, blogModel.getBlogShares());
            values.put(COLUMN_BLOG_SHARED_BY, blogModel.getBlogSharedByUser());
            values.put(COLUMN_BLOG_SHARED_DATE, blogModel.getBlogSharedDate());

            try {
                db.insert(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException sqlce) {
                sqlce.printStackTrace();
            }
            finally {
                if(db!=null)                 db.close();
            }
        }


    }

    public void updateBlog( BlogModel model,String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "Updating Blog : " + model.getBlogID());

        ContentValues values = new ContentValues();
        values.put(COLUMN_BLOG_IMAGE, Helper.Instance.getByteArrayFromBitmap(model.getBlogImage()));
        values.put(COLUMN_BLOG_TITLE, model.getTitle());
        values.put(COLUMN_BLOG_CANONICAL_LINK, model.getCanonicalURL());
        values.put(COLUMN_BLOG_BRIEF, model.getBlogBrief());
        values.put(COLUMN_BLOG_LIKES, model.getBlogLikes());
        values.put(COLUMN_BLOG_VIEWS, model.getBlogViews());
        values.put(COLUMN_BLOG_SHARES, model.getBlogShares());
        db.update(TABLE_NAME, values, COLUMN_BLOG_ID + " = " + model.getBlogID(), null);
        Log.v(TAG, "Row Updated");
        if(db!=null)                 db.close();

    }

    /*
    public void addBlogToFavList( BlogModel blogModel) {
        if (checkExistingFavBlog(db, blogModel.getBlogID())) {
            Log.v(TAG, "Blog ALready Exists");
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BLOG_ID, blogModel.getBlogID());
            values.put(COLUMN_BLOG_TITLE, blogModel.getTitle());
            values.put(COLUMN_BLOG_LINK, blogModel.getBlogUrl());
            values.put(COLUMN_BLOG_BRIEF, blogModel.getBlogBrief());
            values.put(COLUMN_BLOG_IMAGE, Helper.Instance.getByteArrayFromBitmap(blogModel.getBlogImage()));
            values.put(COLUMN_BLOG_CATEGORY, blogModel.getBlogCategory());
            values.put(COLUMN_BLOG_LIKES, blogModel.getBlogLikes());
            values.put(COLUMN_BLOG_VIEWS, blogModel.getBlogViews());
            values.put(COLUMN_BLOG_SHARES, blogModel.getBlogShares());
            values.put(COLUMN_BLOG_SHARED_BY, blogModel.getBlogSharedByUser());
            values.put(COLUMN_BLOG_SHARED_DATE, blogModel.getBlogSharedDate());
            values.put(COLUMN_BLOG_CANONICAL_LINK, blogModel.getCanonicalURL());

            try {
                db.insert(TABLE_FAV_BLOG_LIST_CONTENT, null, values);
            } catch (SQLiteConstraintException sqlce) {
                sqlce.printStackTrace();
            }
        }

    }

*/

    public boolean deleteAllContent() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_USER_GROUP, null, null);
            db.delete(TABLE_BLOG, null, null);
            db.delete(TABLE_INTERESTS, null, null);
            db.delete(TABLE_USER_INTERESTS, null, null);
            db.delete(TABLE_GROUP_CONTENT, null, null);
            db.delete(TABLE_CONTRIBUTOR_I_FOLLOW, null, null);
            db.delete(TABLE_USER_PROFILE, null, null);
            db.delete(TABLE_FAV_BLOG_LIST, null, null);
            db.delete(TABLE_FAV_BLOG_LIST_CONTENT, null, null);
            db.delete(TABLE_CONTRIBUTOR_I_FOLLOW, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) db.close();
            return false;
        }
        if (db != null) db.close();
        return true;

    }
}

