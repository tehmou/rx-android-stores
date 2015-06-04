package com.tehmou.bettercontentprovider.example.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.bettercontentprovider.example.pojo.Foobar;
import com.tehmou.rxandroidarchitecture.store.ContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class FoobarStore extends ContentProviderStoreBase<Integer, Foobar> {
    public FoobarStore(ContentResolver contentResolver) {
        super(contentResolver, ExampleContentProvider.createFoobarContract());
    }

    @Override
    protected Integer getIdFor(Foobar item) {
        return item.getId();
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
