package com.tehmou.rxandroidstores.example.example1;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.store.ContentProviderStoreBase;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by ttuo on 12/06/15.
 */
public class RecordRootStore extends ContentProviderStoreBase<Record> {
    private Subject<List<Record>, List<Record>> subject;

    public RecordRootStore(ContentResolver contentResolver,
                           DatabaseContract<Record> databaseContract) {
        super(contentResolver, databaseContract);
    }

    @Override
    protected ContentObserver getContentObserver() {
        return new ContentObserver(createHandler(this.getClass().getSimpleName())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (subject != null) {
                    subject.onNext(queryList(getContentUriBase()));
                }
            }
        };
    }

    public Observable<List<Record>> getAll() {
        if (subject == null) {
            subject = PublishSubject.create();
            return subject.startWith(queryList(getContentUriBase()));
        }
        return subject;
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + RecordExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
