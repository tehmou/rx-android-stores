package com.tehmou.rxandroidstores.example.example2;

import android.content.ContentResolver;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.example.pojo.CountryIdKey;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.store.SingleItemContentProviderStoreBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class Record2IdStore extends SingleItemContentProviderStoreBase<CountryIdKey, Record> {
    public Record2IdStore(ContentResolver contentResolver, DatabaseContract<Record> databaseContract) {
        super(contentResolver, databaseContract);
    }

    @Override
    protected CountryIdKey getIdFor(Record item) {
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
        return Uri.parse("content://" + Record2ExampleContentProvider.PROVIDER_NAME + "/"
                + databaseContract.getTableName());
    }
}
