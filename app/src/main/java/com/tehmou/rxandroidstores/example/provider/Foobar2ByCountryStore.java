package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.example.pojo.Foobar;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.store.ContentProviderStoreBase;
import com.tehmou.rxandroidstores.store.ListContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class Foobar2ByCountryStore extends ListContentProviderStoreBase<String, Foobar2> {
    public Foobar2ByCountryStore(ContentResolver contentResolver) {
        super(contentResolver, ExampleContentProvider.createFoobar2Contract());
    }

    @Override
    protected String getIdFor(Foobar2 item) {
        return item.getCountry();
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName()
                + "/country");
    }
}
