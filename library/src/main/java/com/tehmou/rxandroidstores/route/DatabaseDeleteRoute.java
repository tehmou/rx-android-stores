package com.tehmou.rxandroidstores.route;

import android.content.ContentValues;
import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 12/06/15.
 */
public interface DatabaseDeleteRoute extends DatabaseRoute {
    void notifyChange(Uri uri, Action1<Uri> notifyChange);
}
