package com.tehmou.rxandroidstores.route;

import android.net.Uri;

import rx.functions.Action1;

/**
 * Created by ttuo on 04/05/15.
 */
public interface DatabaseQueryRoute extends DatabaseRoute {
    String getSortOrder();
    String getMimeType();
}
