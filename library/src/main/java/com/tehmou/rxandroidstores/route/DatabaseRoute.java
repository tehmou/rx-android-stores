package com.tehmou.rxandroidstores.route;

import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseRoute {
    String getPath();
    String getTableName();
    String getSortOrder();
    String getWhere(Uri uri);
    String getMimeType();
    void notifyChange(Uri uri, Action1<Uri> notifyChange);
}
