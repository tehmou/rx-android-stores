package com.tehmou.rxandroidstores.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderBase extends ContentProvider {
    private static final String TAG = ContentProviderBase.class.getSimpleName();
    protected SQLiteDatabase db;
    protected SQLiteOpenHelper databaseHelper;
    protected UriMatcher URI_MATCHER;

    @Override
    public boolean onCreate() {
        createUriMatcher();
        Context context = getContext();
        databaseHelper = createDatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = getUriMatch(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);
        int count = db.delete(tableName, where, selectionArgs);
        if (count > 0) {
            notifyChange(match, uri);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final int match = getUriMatch(uri);
        String tableName = getTableName(match);
        db.insertWithOnConflict(tableName,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
        notifyChange(match, uri);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        final int match = getUriMatch(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = getDefaultSortOrder(match);
        }

        builder.setTables(tableName);
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        where,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {

        if (selection != null) {
            Log.e(TAG, "selection not supported");
        }
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        final int match = getUriMatch(uri);
        String tableName = getTableName(match);
        String where = getWhere(match, uri);

        int count = db.update(tableName, values, where, selectionArgs);
        if (count > 0) {
            notifyChange(match, uri);
        }
        return count;
    }

    private int getUriMatch(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return match;
    }

    protected abstract String getWhere(final int match, Uri uri);
    protected abstract String getTableName(final int match);
    protected abstract String getDefaultSortOrder(final int match);
    protected abstract SQLiteOpenHelper createDatabaseHelper(final Context context);
    protected abstract void createUriMatcher();
    protected abstract void notifyChange(final int match, Uri uri);
}