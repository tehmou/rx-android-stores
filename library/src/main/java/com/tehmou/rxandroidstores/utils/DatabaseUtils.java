package com.tehmou.rxandroidstores.utils;

import android.net.Uri;

import rx.functions.Func1;

/**
 * Created by ttuo on 13/06/15.
 */
public class DatabaseUtils {
    public static final Func1<String, String> createJsonIdTableSqlFunc =
            tableName -> "CREATE TABLE " + tableName + " (id INTEGER, json TEXT NOT NULL)";
    public static final Func1<String, String> dropTableSqlFunc =
            tableName -> "DROP TABLE IF EXISTS " + tableName;
    public static final Func1<Uri, String> getWhereByIdFunc =
            uri -> "id = " + uri.getLastPathSegment();
    public static final Func1<String, String> getIdPathFunc =
            tableName -> tableName + "/*";
    public static final Func1<String, String> getRootPathFunc =
            tableName -> tableName;
    public static final Func1<Uri, String> getWhereRootFunc =
            uri -> null;

    private DatabaseUtils() {

    }
}
