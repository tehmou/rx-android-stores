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
public abstract class ListContentProviderStoreBase<T, U> extends ContentProviderStoreBase<U> {
    private static final String TAG = ListContentProviderStoreBase.class.getSimpleName();
    private final ConcurrentMap<Uri, Subject<List<U>, List<U>>> subjectMap = new ConcurrentHashMap<>();

    public ListContentProviderStoreBase(ContentResolver contentResolver, DatabaseContract<U> databaseContract) {
        super(contentResolver, databaseContract);
    }

    protected List<U> queryList(T id) {
        return queryList(getUriForKey(id));
    }

    public Uri getUriForKey(T id) {
        return Uri.withAppendedPath(getContentUriBase(), id.toString());
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
                    Log.v(TAG, "Updating subject at: " + uri + ")");
                    subjectMap.get(uri).onNext(queryList(uri));
                }
            }
        };
    }

    public Observable<List<U>> getStream(T id) {
        Log.v(TAG, "getStream(" + id + ")");
        final List<U> list = queryList(id);
        final Observable<List<U>> observable = lazyGetSubject(id);
        if (list != null) {
            Log.v(TAG, "Found existing item for id=" + id);
            return observable.startWith(list);
        }
        return observable;
    }

    private Observable<List<U>> lazyGetSubject(T id) {
        return lazyGetSubject(getUriForKey(id));
    }

    private Observable<List<U>> lazyGetSubject(Uri uri) {
        Log.v(TAG, "lazyGetSubject(" + uri + ")");
        subjectMap.putIfAbsent(uri, PublishSubject.<List<U>>create());
        return subjectMap.get(uri);
    }
}
