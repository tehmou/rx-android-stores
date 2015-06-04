package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentValues;

import com.tehmou.rxandroidstores.example.pojo.Foobar;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.SerializedJsonContract;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class ExampleContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.provider.ExampleContentProvider";
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    public ExampleContentProvider() {
        DatabaseContract<Foobar> contract = createFoobarContract();
        addDatabaseContract(contract);
        addDatabaseRoute(
                new DatabaseRouteBase.Builder(contract)
                        .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar")
                        .setPath(contract.getTableName() + "/*")
                        .build());
    }

    public static DatabaseContract<Foobar> createFoobarContract() {
        return SerializedJsonContract.<Foobar>createBuilder(
                "foobars", "INTEGER", Foobar.class, value -> {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SerializedJsonContract.ID, value.getId());
                    return contentValues;
                })
                .build();
    }

    @Override
    protected String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    protected int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
