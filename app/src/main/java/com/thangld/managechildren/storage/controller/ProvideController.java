package com.thangld.managechildren.storage.controller;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thangld.managechildren.Constant;
import com.thangld.managechildren.storage.model.AppModel;
import com.thangld.managechildren.storage.model.AudioModel;
import com.thangld.managechildren.storage.model.CallLogModel;
import com.thangld.managechildren.storage.model.ChildModel;
import com.thangld.managechildren.storage.model.ContactModel;
import com.thangld.managechildren.storage.model.EmailModel;
import com.thangld.managechildren.storage.model.ImageModel;
import com.thangld.managechildren.storage.model.LocationModel;
import com.thangld.managechildren.storage.model.PhoneModel;
import com.thangld.managechildren.storage.model.RuleParentModel;
import com.thangld.managechildren.storage.model.SmsModel;
import com.thangld.managechildren.storage.model.VersionModel;
import com.thangld.managechildren.storage.model.VideoModel;

/**
 * Để quản lý cơ sở dữ liệu bao gồm các bảng sau
 * 0. image
 * 1. video
 * 2. calllog
 * 3. app
 * 4. contact
 * 5. email
 * 6. location
 * 7. phone
 * 8. sms
 * 9. audio
 * 10. vesion
 * Created by thangld on 18/02/2017.
 */

public class ProvideController extends ContentProvider {
    public Context mContext;
    public DatabaseHelper mDatabaseHelper;

    public static String TAG = "ProvideController";

    /**
     * URI ID for route: /Image
     */
    public static final int ROUTE_IMAGE = 1;

    /**
     * URI ID for route: /Image/{ID}
     */
    public static final int ROUTE_IMAGE_ID = 2;


    /**
     * URI ID for route: /Video
     */
    public static final int ROUTE_VIDEO = 11;

    /**
     * URI ID for route: /Video/{ID}
     */
    public static final int ROUTE_VIDEO_ID = 12;


    /**
     * URI ID for route: /Calllog
     */
    public static final int ROUTE_CALLLOG = 21;

    /**
     * URI ID for route: /CallLog/{ID}
     */
    public static final int ROUTE_CALLLOG_ID = 22;

    /**
     * URI ID for route: /App
     */
    public static final int ROUTE_APP = 31;

    /**
     * URI ID for route: /app/{ID}
     */
    public static final int ROUTE_APP_ID = 32;

    /**
     * UriMatcher, used to decode incoming URIs.
     */

    /**
     * URI ID for route: /Contact
     */
    public static final int ROUTE_CONTACT = 41;

    /**
     * URI ID for route: /Contact/{ID}
     */
    public static final int ROUTE_CONTACT_ID = 42;

    /**
     * URI ID for route: /EMAIL
     */
    public static final int ROUTE_EMAIL = 51;

    /**
     * URI ID for route: /EMAIL/{ID}
     */
    public static final int ROUTE_EMAIL_ID = 52;

    /**
     * URI ID for route: /LOCATION
     */
    public static final int ROUTE_LOCATION = 61;

    /**
     * URI ID for route: /EMAIL/{ID}
     */
    public static final int ROUTE_LOCATION_ID = 62;


    /**
     * URI ID for route: /sms
     */
    public static final int ROUTE_PHONE = 71;

    /**
     * URI ID for route: /sms/{ID}
     */
    public static final int ROUTE_PHONE_ID = 72;

    /**
     * URI ID for route: /sms
     */
    public static final int ROUTE_SMS = 81;

    /**
     * URI ID for route: /sms/{ID}
     */
    public static final int ROUTE_SMS_ID = 82;

    /**
     * URI ID for route: /sms
     */
    public static final int ROUTE_AUDIO = 91;

    /**
     * URI ID for route: /sms/{ID}
     */
    public static final int ROUTE_AUDIO_ID = 92;

    /**
     * URI ID for route: /VersionUtils
     */
    public static final int ROUTE_VERSION = 101;

    /**
     * URI ID for route: /VersionUtils/{ID}
     */
    public static final int ROUTE_VERSION_ID = 102;


    /**
     * URI ID for route: /Child
     */
    public static final int ROUTE_CHILD = 111;

    /**
     * URI ID for route: /ChildUtils/{ID}
     */
    public static final int ROUTE_CHILD_ID = 112;



    /**
     * URI ID for route: /Rule
     */
    public static final int ROUTE_RULE = 121;

    /**
     * URI ID for route: /rule/{ID}
     */
    public static final int ROUTE_RULE_ID = 122;


    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //1,2
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "images", ROUTE_IMAGE);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "images/*", ROUTE_IMAGE_ID);

        //11,12
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "videos", ROUTE_VIDEO);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "videos/*", ROUTE_VIDEO_ID);

        //21,22
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "calllogs", ROUTE_CALLLOG);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "calllogs/*", ROUTE_CALLLOG_ID);

        //31,32
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "apps", ROUTE_APP);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "apps/*", ROUTE_APP_ID);

        //41,42
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "contacts", ROUTE_CONTACT);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "contacts/*", ROUTE_CONTACT_ID);

        //51,52
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "emails", ROUTE_EMAIL);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "emails/*", ROUTE_EMAIL_ID);

        //61,62
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "locations", ROUTE_LOCATION);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "locations/*", ROUTE_LOCATION_ID);

        //71,72
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "phones", ROUTE_PHONE);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "phones/*", ROUTE_PHONE_ID);

        //81,82
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "smses", ROUTE_SMS);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "smses/*", ROUTE_SMS_ID);

        //91,92
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "audios", ROUTE_AUDIO);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "audios/*", ROUTE_AUDIO_ID);

        //101,102
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "versions", ROUTE_VERSION);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "versions/*", ROUTE_VERSION_ID);


        //111,112
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "childs", ROUTE_CHILD);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "childs/*", ROUTE_CHILD_ID);

        //121,122
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "rule_patents", ROUTE_RULE);
        sUriMatcher.addURI(Constant.AUTHORITY_PROVIDER, "rule_patents/*", ROUTE_RULE_ID);

    }

    public ProvideController() {
        super();
    }

    @Override
    public boolean onCreate() {

        mContext = getContext();
        mDatabaseHelper = new DatabaseHelper(mContext);
        return true;
    }

    /**
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            /**
             * 0. Image
             */
            case ROUTE_IMAGE_ID:
                // Return a single Image, by ID.
                String id = uri.getLastPathSegment();
                builder.where(ImageModel.Contents._ID + "=?", id);

            case ROUTE_IMAGE:
                // Return all known Image
                builder.table(ImageModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                Context ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 1. video
             */
            case ROUTE_VIDEO_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(VideoModel.Contents._ID + "=?", id);
            case ROUTE_VIDEO:
                // Return all known Image
                builder.table(VideoModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 1. video
             */
            case ROUTE_AUDIO_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(AudioModel.Contents._ID + "=?", id);
            case ROUTE_AUDIO:
                // Return all known Image
                builder.table(AudioModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;


            /**
             * 2.Calllog
             */

            case ROUTE_CALLLOG_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(CallLogModel.Contents._ID + "=?", id);
            case ROUTE_CALLLOG:
                // Return all known Image
                builder.table(CallLogModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 3.
             */
            case ROUTE_APP_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(AppModel.Contents._ID + "=?", id);
            case ROUTE_APP:
                // Return all known Image
                builder.table(AppModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 4. C
             */
            case ROUTE_CONTACT_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(ContactModel.Contents._ID + "=?", id);
            case ROUTE_CONTACT:
                // Return all known Image
                builder.table(ContactModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 5.
             */
            case ROUTE_EMAIL_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(EmailModel.Contents._ID + "=?", id);
            case ROUTE_EMAIL:
                // Return all known Image
                builder.table(EmailModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 6. Location
             */
            case ROUTE_LOCATION_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(LocationModel.Contents._ID + "=?", id);
            case ROUTE_LOCATION:
                // Return all known Image
                builder.table(LocationModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 7.ph
             */
            case ROUTE_PHONE_ID:
                // Return a single phone, by ID.
                id = uri.getLastPathSegment();
                builder.where(PhoneModel.Contents._ID + "=?", id);
            case ROUTE_PHONE:
                // Return all known Phone
                builder.table(PhoneModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 8. SMS
             */
            case ROUTE_SMS_ID:
                // Return a single SMS, by ID.
                id = uri.getLastPathSegment();
                builder.where(SmsModel.Contents._ID + "=?", id);
            case ROUTE_SMS:
                // Return all known Image
                builder.table(SmsModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 9. VersionUtils
             */
            case ROUTE_VERSION_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(VersionModel.Contents._ID + "=?", id);
            case ROUTE_VERSION:
                // Return all known Image
                builder.table(VersionModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            /**
             * 10. ChildUtils
             */
            case ROUTE_CHILD_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(ChildModel.Contents._ID + "=?", id);
            case ROUTE_CHILD:
                // Return all known Image
                builder.table(ChildModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            /**
             * 11. Rules
             */
            case ROUTE_RULE_ID:
                // Return a single Image, by ID.
                id = uri.getLastPathSegment();
                builder.where(RuleParentModel.Contents._ID + "=?", id);
            case ROUTE_RULE:
                // Return all known Image
                builder.table(RuleParentModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                ctx = getContext();
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private void queryID(String id, Uri uri, SelectionBuilder builder, Class myClass) {
// Return a single Image, by ID.
        id = uri.getLastPathSegment();
        builder.where(CallLogModel.Contents._ID + "=?", id);

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Thuc hien insert Image
     *
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {


        Log.d(TAG, uri.toString().replace(Constant.AUTHORITY_PROVIDER, "") + values.toString());


        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            /**
             * 0. Image
             */
            case ROUTE_IMAGE:
                long id = db.insertOrThrow(ImageModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(ImageModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_IMAGE_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 1. Video
            case ROUTE_VIDEO:
                id = db.insertOrThrow(VideoModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(VideoModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_VIDEO_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 2. calllog
            case ROUTE_CALLLOG:
                id = db.insertOrThrow(CallLogModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(CallLogModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_CALLLOG_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 3. app
            case ROUTE_APP:
                id = db.insertOrThrow(AppModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(AppModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_APP_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 4.Contact
            case ROUTE_CONTACT:
                id = db.insertOrThrow(ContactModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(ContactModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_CONTACT_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 5. email
            case ROUTE_EMAIL:
                id = db.insertOrThrow(EmailModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(EmailModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_EMAIL_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 6. location
            case ROUTE_LOCATION:
                id = db.insertOrThrow(LocationModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(LocationModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_LOCATION_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 7. phone
            case ROUTE_PHONE:
                id = db.insertOrThrow(PhoneModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(PhoneModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_PHONE_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 8. sms
            case ROUTE_SMS:
                id = db.insertOrThrow(SmsModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(SmsModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_SMS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 9. audio
            case ROUTE_AUDIO:
                id = db.insertOrThrow(AudioModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(AudioModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_AUDIO_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 10. verison
            case ROUTE_VERSION:
                id = db.insertOrThrow(VersionModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(VersionModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_VERSION_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);

                // 11. CHILD
            case ROUTE_CHILD:
                id = db.insertOrThrow(ChildModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(ChildModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_CHILD_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
                // 12. rules
            case ROUTE_RULE:
                id = db.insertOrThrow(RuleParentModel.Contents.TABLE_NAME, null, values);
                result = Uri.parse(RuleParentModel.Contents.CONTENT_URI + "/" + id);
                break;
            case ROUTE_RULE_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Xoa Image
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.d(TAG, "delete");
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            /**
             * 0. image
             */
            case ROUTE_IMAGE:
                count = builder.table(ImageModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_IMAGE_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(ImageModel.Contents.TABLE_NAME)
                        .where(ImageModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 1. Video
             */
            case ROUTE_VIDEO:
                count = builder.table(VideoModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_VIDEO_ID:
                id = uri.getLastPathSegment();
                count = builder.table(VideoModel.Contents.TABLE_NAME)
                        .where(VideoModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 3. Calllog
             */
            case ROUTE_CALLLOG:
                count = builder.table(CallLogModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_CALLLOG_ID:
                id = uri.getLastPathSegment();
                count = builder.table(CallLogModel.Contents.TABLE_NAME)
                        .where(CallLogModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;


            /**
             * 4. Apps
             */
            case ROUTE_APP:
                count = builder.table(AppModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_APP_ID:
                id = uri.getLastPathSegment();
                count = builder.table(AppModel.Contents.TABLE_NAME)
                        .where(AppModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;


            /**
             * 5. Contact
             */
            case ROUTE_CONTACT:
                count = builder.table(ContactModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_CONTACT_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ContactModel.Contents.TABLE_NAME)
                        .where(ContactModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 6. Email
             */
            case ROUTE_EMAIL:
                count = builder.table(EmailModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_EMAIL_ID:
                id = uri.getLastPathSegment();
                count = builder.table(EmailModel.Contents.TABLE_NAME)
                        .where(EmailModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 7. Phone
             */
            case ROUTE_PHONE:
                count = builder.table(PhoneModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_PHONE_ID:
                id = uri.getLastPathSegment();
                count = builder.table(PhoneModel.Contents.TABLE_NAME)
                        .where(PhoneModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 8. sms
             */
            case ROUTE_SMS:
                count = builder.table(SmsModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_SMS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(SmsModel.Contents.TABLE_NAME)
                        .where(SmsModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 9. sms
             */
            case ROUTE_AUDIO:
                count = builder.table(AudioModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            /**
             * 10.Audio
             */
            case ROUTE_AUDIO_ID:
                id = uri.getLastPathSegment();
                count = builder.table(AudioModel.Contents.TABLE_NAME)
                        .where(AudioModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 11. ChildUtils
             */
            case ROUTE_VERSION:
                count = builder.table(ChildModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_VERSION_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ChildModel.Contents.TABLE_NAME)
                        .where(VersionModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            /**
             * 11. ChildUtils
             */
            case ROUTE_RULE:
                count = builder.table(RuleParentModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_RULE_ID:
                id = uri.getLastPathSegment();
                count = builder.table(RuleParentModel.Contents.TABLE_NAME)
                        .where(RuleParentModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();

        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Thuc hien update
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            /**
             * 0. Image
             */
            case ROUTE_IMAGE:
                count = builder.table(ImageModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_IMAGE_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(ImageModel.Contents.TABLE_NAME)
                        .where(ImageModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 1. Video
             */
            case ROUTE_VIDEO:
                count = builder.table(VideoModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_VIDEO_ID:
                id = uri.getLastPathSegment();
                count = builder.table(VideoModel.Contents.TABLE_NAME)
                        .where(VideoModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 2. CallLog
             */
            case ROUTE_CALLLOG:
                count = builder.table(CallLogModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_CALLLOG_ID:
                id = uri.getLastPathSegment();
                count = builder.table(CallLogModel.Contents.TABLE_NAME)
                        .where(CallLogModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            /**
             * 3. App
             */
            case ROUTE_APP:
                count = builder.table(AppModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_APP_ID:
                id = uri.getLastPathSegment();
                count = builder.table(AppModel.Contents.TABLE_NAME)
                        .where(AppModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            /**
             * 4.Contact
             */
            case ROUTE_CONTACT:
                count = builder.table(ContactModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_CONTACT_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ContactModel.Contents.TABLE_NAME)
                        .where(ContactModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            /**
             * 5. email
             */
            case ROUTE_EMAIL:
                count = builder.table(EmailModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_EMAIL_ID:
                id = uri.getLastPathSegment();
                count = builder.table(EmailModel.Contents.TABLE_NAME)
                        .where(EmailModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            /**
             * 6. Location
             */
            case ROUTE_LOCATION:
                count = builder.table(LocationModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_LOCATION_ID:
                id = uri.getLastPathSegment();
                count = builder.table(LocationModel.Contents.TABLE_NAME)
                        .where(LocationModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;


            /**
             * 7. Phone
             */
            case ROUTE_PHONE:
                count = builder.table(PhoneModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_PHONE_ID:
                id = uri.getLastPathSegment();
                count = builder.table(PhoneModel.Contents.TABLE_NAME)
                        .where(PhoneModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 8. sms
             */
            case ROUTE_SMS:
                count = builder.table(SmsModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_SMS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(SmsModel.Contents.TABLE_NAME)
                        .where(SmsModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 9. audio
             */
            case ROUTE_AUDIO:
                count = builder.table(AudioModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_AUDIO_ID:
                id = uri.getLastPathSegment();
                count = builder.table(AudioModel.Contents.TABLE_NAME)
                        .where(AudioModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 9. VersionUtils
             */
            case ROUTE_VERSION:
                count = builder.table(VersionModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_VERSION_ID:
                id = uri.getLastPathSegment();
                count = builder.table(VersionModel.Contents.TABLE_NAME)
                        .where(VersionModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            /**
             * 10. CHildUtils
             */
            case ROUTE_CHILD:
                count = builder.table(ChildModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_CHILD_ID:
                id = uri.getLastPathSegment();
                count = builder.table(ChildModel.Contents.TABLE_NAME)
                        .where(VersionModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            /**
             * 10. CHildUtils
             */
            case ROUTE_RULE:
                count = builder.table(RuleParentModel.Contents.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_RULE_ID:
                id = uri.getLastPathSegment();
                count = builder.table(RuleParentModel.Contents.TABLE_NAME)
                        .where(RuleParentModel.Contents._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }


}

