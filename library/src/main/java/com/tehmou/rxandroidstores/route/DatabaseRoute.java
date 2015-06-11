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
}
