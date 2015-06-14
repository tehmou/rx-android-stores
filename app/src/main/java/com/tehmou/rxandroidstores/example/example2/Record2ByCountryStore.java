package com.tehmou.rxandroidstores.example.example2;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.store.ListContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class Record2ByCountryStore extends ListContentProviderStoreBase<String, Record> {
    public Record2ByCountryStore(ContentResolver contentResolver,
                                 DatabaseContract<Record> databaseContract) {
        super(contentResolver, databaseContract);
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + Record2ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName()
                + "/country");
    }
}
