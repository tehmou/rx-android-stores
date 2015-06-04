package com.tehmou.bettercontentprovider.example.provider;

import android.net.Uri;

import com.tehmou.rxandroidarchitecture.contract.DatabaseContract;
import com.tehmou.rxandroidarchitecture.contract.SerializedJsonContract;
import com.tehmou.rxandroidarchitecture.provider.ContractContentProviderBase;
import com.tehmou.rxandroidarchitecture.route.DatabaseRouteBase;

import rx.functions.Func1;

/**
 * Created by ttuo on 04/06/15.
 */
public class ExampleContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.bettercontentprovider.example.provider.ExampleContentProvider";
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    public ExampleContentProvider() {
        DatabaseContract<String> contract =
                SerializedJsonContract.<String>createBuilder("foobars", String.class)
                        .build();
        addDatabaseContract(contract);
        addDatabaseRoute(
                new DatabaseRouteBase.Builder(contract)
                        .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.bettercontentprovider.foobar")
                        .setPath(contract.getTableName() + "/*")
                        .build());
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
