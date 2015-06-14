package com.tehmou.rxandroidstores.example.example1;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.store.SingleItemContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class RecordIdStore extends SingleItemContentProviderStoreBase<Integer, Record> {
    public RecordIdStore(ContentResolver contentResolver,
                         DatabaseContract<Record> databaseContract) {
        super(contentResolver, databaseContract);
    }

    @Override
    protected Integer getIdFor(Record item) {
        return item.getId();
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + RecordExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
