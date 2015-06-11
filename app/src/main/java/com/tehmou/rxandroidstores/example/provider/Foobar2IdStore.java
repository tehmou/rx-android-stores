package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.example.pojo.CountryIdKey;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.store.SingleItemContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class Foobar2IdStore extends SingleItemContentProviderStoreBase<CountryIdKey, Foobar2> {
    public Foobar2IdStore(ContentResolver contentResolver) {
        super(contentResolver, Foobar2ExampleContentProvider.createFoobar2Contract());
    }

    @Override
    protected CountryIdKey getIdFor(Foobar2 item) {
        return new CountryIdKey(item.getCountry(), item.getId());
    }

    @Override
    public Uri getUriForKey(CountryIdKey key) {
        Uri uri = Uri.withAppendedPath(getContentUriBase(), "country");
        uri = Uri.withAppendedPath(uri, key.getCountry());
        uri = Uri.withAppendedPath(uri, "id");
        uri = Uri.withAppendedPath(uri, String.valueOf(key.getId()));
        return uri;
    }

    @Override
    protected Uri getContentUriBase() {
        return Uri.parse("content://" + Foobar2ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
