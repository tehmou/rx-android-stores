package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/06/15.
 */
public class Foobar2ExampleContentProvider extends ContractContentProviderBase {
    private static final String TAG = Foobar2ExampleContentProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.provider.Foobar2ExampleContentProvider";
    private static final String DATABASE_NAME = "foobar2_database";
    private static final int DATABASE_VERSION = 1;

    public Foobar2ExampleContentProvider() {
        DatabaseContract<Foobar2> foobar2Contract = createFoobar2Contract();
        addDatabaseContract(foobar2Contract);

        // Notice that the order has to be from more restrictive to less restrictive.
        DatabaseRouteBase idDatabaseRoute = new DatabaseRouteBase.Builder(foobar2Contract)
                .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar2")
                .setPath(foobar2Contract.getTableName() + "/country/*/id/*")
                .setGetWhereFunc(uri -> {
                    final List<String> pathSegments = uri.getPathSegments();
                    final String country = pathSegments.get(pathSegments.size() - 3);
                    final String id = pathSegments.get(pathSegments.size() - 1);
                    return "country = '" + country + "' AND id = " + id;
                })
                .setNotifyChangeFunc((uri, notifyUri) -> {
                    notifyUri.call(uri);
                    Uri countryUri = Uri.EMPTY;
                    final List<String> pathSegments = uri.getPathSegments();
                    for (int i = 0; i < pathSegments.size() - 3; i++) {
                        countryUri = Uri.withAppendedPath(countryUri, pathSegments.get(i));
                    }
                    Log.v(TAG, "Notifying country uri " + countryUri);
                    notifyUri.call(countryUri);
                })
                .build();
        addDatabaseDeleteRoute(idDatabaseRoute);
        addDatabaseInsertRoute(idDatabaseRoute);
        addDatabaseQueryRoute(idDatabaseRoute);
        addDatabaseQueryRoute(
                new DatabaseRouteBase.Builder(foobar2Contract)
                        .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.foobar2")
                        .setPath(foobar2Contract.getTableName() + "/country/*")
                        .setGetWhereFunc(uri -> "country = '" + uri.getLastPathSegment() + "'")
                        .build());
        addDatabaseQueryRoute(
                new DatabaseRouteBase.Builder(foobar2Contract)
                        .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.foobar2")
                        .setPath(foobar2Contract.getTableName())
                        .setGetWhereFunc(uri -> null)
                        .build());
    }

    public static DatabaseContract<Foobar2> createFoobar2Contract() {
        return new DatabaseContractBase.Builder<Foobar2>()
                .setTableName("foobars2")
                .setProjection(new String[]{"id", "country", "json"})
                .setCreateTableSql("CREATE TABLE foobars2 (id INTEGER, country STRING, json TEXT NOT NULL)")
                .setDropTableSql("DROP TABLE IF EXISTS foobars2")
                .setReadFunc(cursor -> {
                    final String json = cursor.getString(cursor.getColumnIndex("json"));
                    return new Gson().fromJson(json, Foobar2.class);
                })
                .setGetContentValuesForItemFunc(foobar2 -> {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", foobar2.getId());
                    contentValues.put("country", foobar2.getCountry());
                    contentValues.put("json", new Gson().toJson(foobar2));
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
