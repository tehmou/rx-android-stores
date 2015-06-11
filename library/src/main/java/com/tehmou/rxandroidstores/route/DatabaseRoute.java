package com.tehmou.rxandroidstores.route;

import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 11/06/15.
 */
public interface DatabaseRoute {
    String getPath();
    String getTableName();
    String getWhere(Uri uri);

    // This is not allowed for query routes, but Java does not support polymorphism..
    void notifyChange(Uri uri, Action1<Uri> notifyChange);
}
