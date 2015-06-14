package com.tehmou.rxandroidstores.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 26/04/15.
 */
public abstract class ContentProviderStoreBase<U> {
    private static final String TAG = ContentProviderStoreBase.class.getSimpleName();

    protected final ContentResolver contentResolver;
    protected final DatabaseContract<U> databaseContract;
    private final ContentObserver contentObserver = getContentObserver();

    public ContentProviderStoreBase(ContentResolver contentResolver,
                                    DatabaseContract<U> databaseContract) {
        this.contentResolver = contentResolver;
        this.databaseContract = databaseContract;
        this.contentResolver.registerContentObserver(
                getContentUriBase(), true, contentObserver);
    }

    @NonNull
    protected static Handler createHandler(String name) {
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    protected void insertOrUpdate(U item, Uri uri) {
        Log.v(TAG, "insertOrUpdate to " + uri);
        ContentValues values = getContentValuesForItem(item);
        Log.v(TAG, "values(" + values + ")");
        if (contentResolver.update(uri, values, null, null) == 0) {
            final Uri resultUri = contentResolver.insert(uri, values);
            Log.v(TAG, "Inserted at " + resultUri);
        } else {
            Log.v(TAG, "Updated at " + uri);
        }
    }

    protected List<U> queryList(Uri uri) {
        Cursor cursor = contentResolver.query(uri,
                databaseContract.getProjection(), null, null, null);
        List<U> list = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                list.add(databaseContract.read(cursor));
            }
            while (cursor.moveToNext()) {
                list.add(databaseContract.read(cursor));
            }
            cursor.close();
        }
        if (list.size() == 0) {
            Log.v(TAG, "Could not find with uri: " + uri);
        }
        return list;
    }

    protected ContentValues getContentValuesForItem(U item) {
        return databaseContract.getContentValuesForItem(item);
    }

    protected abstract Uri getContentUriBase();
    protected abstract ContentObserver getContentObserver();
}
