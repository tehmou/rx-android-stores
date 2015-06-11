package com.tehmou.rxandroidstores.store;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tehmou.rxandroidstores.contract.DatabaseContract;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 11/06/15.
 */
public abstract class SingleItemContentProviderStoreBase<T, U> extends ContentProviderStoreBase<T, U> {
    private static final String TAG = SingleItemContentProviderStoreBase.class.getSimpleName();
    private final ConcurrentMap<Uri, Subject<U, U>> subjectMap = new ConcurrentHashMap<>();

    public SingleItemContentProviderStoreBase(ContentResolver contentResolver, DatabaseContract<U> databaseContract) {
        super(contentResolver, databaseContract);
    }

    protected U querySingle(T id) {
        return querySingle(getUriForKey(id));
    }

    protected U querySingle(Uri uri) {
        List<U> list = queryList(uri);
        return list.size() > 0 ? list.get(0) : null;
    }

    public Uri getUriForKey(T id) {
        return Uri.withAppendedPath(getContentUriBase(), id.toString());
    }

    public void put(U item) {
        Uri uri = getUriForKey(getIdFor(item));
        insertOrUpdate(item, uri);
    }

    @NonNull
    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(this.getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.v(TAG, "onChange(" + uri + ")");

                if (subjectMap.containsKey(uri)) {
                    subjectMap.get(uri).onNext(querySingle(uri));
                }
            }
        };
    }

    public Observable<U> getStream(T id) {
        Log.v(TAG, "getStream(" + id + ")");
        final U item = querySingle(id);
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
}
