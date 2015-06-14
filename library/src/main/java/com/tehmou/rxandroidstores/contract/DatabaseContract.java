package com.tehmou.rxandroidstores.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Map;

import rx.functions.Func1;

/**
 * Created by ttuo on 13/01/15.
 */
public interface DatabaseContract<T> {
    String getCreateTable();
    String getDropTable();
    String getTableName();
    T read(Cursor cursor);
    ContentValues getContentValuesForItem(T item);
    String[] getProjection();
    String getDefaultSortOrder();
    Func1<Uri, String> getDefaultWhereFunc();
}
