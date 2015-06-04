package com.tehmou.rxandroidarchitecture.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tehmou.rxandroidarchitecture.contract.DatabaseContract;

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
abstract public class ContentProviderStoreBase<T, U> {
    private static final String TAG = ContentProviderStoreBase.class.getSimpleName();

    protected final ContentResolver contentResolver;
    private final ConcurrentMap<Uri, Subject<U, U>> subjectMap = new ConcurrentHashMap<>();
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
    private ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(this.getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.v(TAG, "onChange(" + uri + ")");

                if (subjectMap.containsKey(uri)) {
                    subjectMap.get(uri).onNext(query(uri));
                }
            }
        };
    }

    @NonNull
    private static Handler createHandler(String name) {
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        return new Handler(handlerThread.getLooper());
    }

    public void put(U item) {
        insertOrUpdate(item);
    }

    public Observable<U> getStream(T id) {
        Log.v(TAG, "getStream(" + id + ")");
        final U item = query(id);
        final Observable<U> observable = lazyGetSubject(id);
        if (item != null) {
            Log.v(TAG, "Found existing item for id=" + id);
            return observable.startWith(item);
        }
        return observable;
    }

    private Observable<U> lazyGetSubject(T id) {
        Log.v(TAG, "lazyGetSubject(" + id + ")");
        final Uri uri = getUriForKey(id);
        subjectMap.putIfAbsent(uri, PublishSubject.<U>create());
        return subjectMap.get(uri);
    }

    public void insertOrUpdate(U item) {
        Uri uri = getUriForKey(getIdFor(item));
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

    protected U query(T id) {
        return query(getUriForKey(id));
    }

    protected U query(Uri uri) {
        List<U> list = queryList(uri);
        return list.size() > 0 ? list.get(0) : null;
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

    public Uri getUriForKey(T id) {
        return Uri.withAppendedPath(getContentUriBase(), id.toString());
    }

    abstract protected Uri getContentUriBase();

    abstract protected T getIdFor(U item);
}
