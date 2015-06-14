package com.tehmou.rxandroidstores.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tehmou.rxandroidstores.route.DatabaseDeleteRoute;
import com.tehmou.rxandroidstores.route.DatabaseInsertUpdateRoute;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 11/01/15.
 */
abstract public class ContentProviderBase extends ContentProvider {
    private static final String TAG = ContentProviderBase.class.getSimpleName();
    protected SQLiteDatabase db;
    protected SQLiteOpenHelper databaseHelper;

    @Override
    public boolean onCreate() {
        createUriMatchers();
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
        DatabaseDeleteRoute route = getDeleteRoute(uri);
        int count = db.delete(route.getTableName(), route.getWhere(uri), selectionArgs);
        if (count > 0) {
            route.notifyChange(uri, this::notifyChange);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        DatabaseInsertUpdateRoute route = getInsertRoute(uri);
        db.insertWithOnConflict(route.getTableName(),
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
        route.notifyChange(values, uri, this::notifyChange);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        DatabaseQueryRoute route = getQueryRoute(uri);

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = route.getSortOrder();
        }

        builder.setTables(route.getTableName());
        builder.setProjectionMap(route.getProjectionMap());
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        route.getWhere(uri),
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

        DatabaseInsertUpdateRoute route = getUpdateRoute(uri);
        int count = db.update(
                route.getTableName(), values, route.getWhere(uri), selectionArgs);
        if (count > 0) {
            route.notifyChange(values, uri, this::notifyChange);
        }
        return count;
    }

    private void notifyChange(Uri uri) {
        Log.v(TAG, "notifyChange(" + uri + ")");
        getContext().getContentResolver().notifyChange(uri, null);
    }

    protected abstract DatabaseDeleteRoute getDeleteRoute(Uri uri);
    protected abstract DatabaseInsertUpdateRoute getInsertRoute(Uri uri);
    protected abstract DatabaseQueryRoute getQueryRoute(Uri uri);
    protected abstract DatabaseInsertUpdateRoute getUpdateRoute(Uri uri);
    protected abstract SQLiteOpenHelper createDatabaseHelper(final Context context);
    protected abstract void createUriMatchers();

    public static Uri removeLastPathSegments(final Uri uri, final int n) {
        final List<String> pathSegments = uri.getPathSegments();
        final List<String> newSegments = new ArrayList<>();
        for (int i = 0; i < pathSegments.size() - n; i++) {
            newSegments.add(pathSegments.get(i));
        }

        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedPath(uri.getPath());
        builder.encodedQuery(uri.getQuery());
        builder.encodedPath(TextUtils.join("/", newSegments));
        return builder.build();
    }
}
