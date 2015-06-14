package com.tehmou.rxandroidstores.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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

    public static <T> Func1<T, ContentValues> getContentValuesForItemIdJsonFunc(
            Gson gson, Func1<T, String> getId) {
        return value -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", getId.call(value));
            contentValues.put("json", gson.toJson(value));
            return contentValues;
        };
    }

    public static <T> Func1<Cursor, T> readJsonFunc(
            Gson gson, TypeToken<T> typeToken) {
        return readJsonFunc(gson, typeToken, "json");
    }

    public static <T> Func1<Cursor, T> readJsonFunc(
            Gson gson, TypeToken<T> typeToken, String jsonColumnName) {
        return cursor -> {
            final String json = cursor.getString(cursor.getColumnIndex(jsonColumnName));
            return gson.fromJson(json, typeToken.getType());
        };
    }
}
