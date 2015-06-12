package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.store.ListContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class Foobar2ByCountryStore extends ListContentProviderStoreBase<String, Foobar2> {
    public Foobar2ByCountryStore(ContentResolver contentResolver,
                                 DatabaseContract<Foobar2> databaseContract) {
        super(contentResolver, databaseContract);
    }

    @Override
    protected String getIdFor(Foobar2 item) {
        return item.getCountry();
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + Foobar2ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName()
                + "/country");
    }
}
