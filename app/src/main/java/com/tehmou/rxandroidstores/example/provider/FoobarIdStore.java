package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.example.pojo.Foobar;
import com.tehmou.rxandroidstores.store.SingleItemContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class FoobarIdStore extends SingleItemContentProviderStoreBase<Integer, Foobar> {
    public FoobarIdStore(ContentResolver contentResolver) {
        super(contentResolver, FoobarExampleContentProvider.createFoobarContract());
    }

    @Override
    protected Integer getIdFor(Foobar item) {
        return item.getId();
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + FoobarExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
