package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Foobar;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;

/**
 * Created by ttuo on 04/06/15.
 */
public class FoobarExampleContentProvider extends ContractContentProviderBase {
    private static final String TAG = FoobarExampleContentProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.provider.FoobarExampleContentProvider";
    private static final String DATABASE_NAME = "foobar_database";
    private static final int DATABASE_VERSION = 1;

    public FoobarExampleContentProvider() {
        DatabaseContract<Foobar> foobarContract = createFoobarContract();
        addDatabaseContract(foobarContract);

        DatabaseRouteBase idDatabaseRoute = new DatabaseRouteBase.Builder(foobarContract)
                .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar")
                .setPath(foobarContract.getTableName() + "/*")
                .setGetWhereFunc(uri -> "id = " + uri.getLastPathSegment())
                .build();
        addDatabaseInsertRoute(idDatabaseRoute);
        addDatabaseUpdateRoute(idDatabaseRoute);
        addDatabaseDeleteRoute(idDatabaseRoute);
        addDatabaseQueryRoute(idDatabaseRoute);
    }

    public static DatabaseContract<Foobar> createFoobarContract() {
        return new DatabaseContractBase.Builder<Foobar>()
                .setTableName("foobars")
                .setProjection(new String[]{"id","json"})
                .setCreateTableSql("CREATE TABLE foobars (id INTEGER, json TEXT NOT NULL)")
                .setDropTableSql("DROP TABLE IF EXISTS foobars")
                .setReadFunc(cursor -> {
                    final String json = cursor.getString(cursor.getColumnIndex("json"));
                    return new Gson().fromJson(json, Foobar.class);
                })
                .setGetContentValuesForItemFunc(foobar -> {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", foobar.getId());
                    contentValues.put("json", new Gson().toJson(foobar));
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
