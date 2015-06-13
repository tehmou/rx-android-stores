package com.tehmou.rxandroidstores.example.example2;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.provider.ContentProviderBase;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;

import java.util.List;

/**
 * Created by ttuo on 04/06/15.
 */
public class Record2ExampleContentProvider extends ContractContentProviderBase {
    private static final String TAG = Record2ExampleContentProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.example2.Record2ExampleContentProvider";
    private static final String DATABASE_NAME = "record2_database";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseContract<Record> record2Contract;
    private static DatabaseRouteBase record2IdRoute;
    private static DatabaseRouteBase record2CountryRoute;
    private static DatabaseRouteBase record2RootRoute;

    public Record2ExampleContentProvider() {
        DatabaseContract<Record> record2Contract = getRecord2Contract();
        addDatabaseContract(record2Contract);

        // Notice that the order has to be from more restrictive to less restrictive.
        DatabaseRouteBase idRoute = getRecord2IdRoute();
        addDatabaseDeleteRoute(idRoute);
        addDatabaseInsertRoute(idRoute);
        addDatabaseUpdateRoute(idRoute);
        addDatabaseQueryRoute(idRoute);

        // Country route
        DatabaseRouteBase countryRoute = getRecord2CountryRoute();
        addDatabaseQueryRoute(countryRoute);

        // Root route
        DatabaseRouteBase rootRoute = getRecord2RootRoute();
        addDatabaseQueryRoute(rootRoute);
        addDatabaseDeleteRoute(rootRoute);
    }

    public static DatabaseContract<Record> getRecord2Contract() {
        if (record2Contract == null) {
            final Gson gson = new Gson();
            record2Contract = new DatabaseContractBase.Builder<Record>()
                    .setTableName("records2")
                    .setProjection(new String[]{"id", "country", "json"})
                    .setCreateTableSql("CREATE TABLE records2 (id INTEGER, country STRING, json TEXT NOT NULL)")
                    .setDropTableSql("DROP TABLE IF EXISTS records2")
                    .setReadFunc(cursor -> {
                        final String json = cursor.getString(cursor.getColumnIndex("json"));
                        return gson.fromJson(json, Record.class);
                    })
                    .setGetContentValuesForItemFunc(record2 -> {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", record2.getId());
                        contentValues.put("country", record2.getCountry());
                        contentValues.put("json", gson.toJson(record2));
                        return contentValues;
                    })
                    .build();
        }
        return record2Contract;
    }

    public static DatabaseRouteBase getRecord2IdRoute() {
        if (record2IdRoute == null) {
            DatabaseContract<Record> contract = getRecord2Contract();
            record2IdRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.record2")
                    .setPath(contract.getTableName() + "/country/*/id/*")
                    .setGetWhereFunc(uri -> {
                        final List<String> pathSegments = uri.getPathSegments();
                        final String country = pathSegments.get(pathSegments.size() - 3);
                        final String id = pathSegments.get(pathSegments.size() - 1);
                        return "country = '" + country + "' AND id = " + id;
                    })
                    .setNotifyChangeFunc((uri, notifyUri) -> {
                        Log.d(TAG, "idRoute notifyChange(" + uri + ")");
                        notifyUri.call(uri);

                        Uri countryUri = ContentProviderBase.removeLastPathSegments(uri, 2);
                        Log.v(TAG, "Notifying country uri " + countryUri);
                        notifyUri.call(countryUri);
                    })
                    .build();
        }
        return record2IdRoute;
    }

    public static DatabaseRouteBase getRecord2CountryRoute() {
        if (record2CountryRoute == null) {
            DatabaseContract<Record> contract = getRecord2Contract();
            record2CountryRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.record2")
                    .setPath(contract.getTableName() + "/country/*")
                    .setGetWhereFunc(uri -> "country = '" + uri.getLastPathSegment() + "'")
                    .build();
        }
        return record2CountryRoute;
    }

    public static DatabaseRouteBase getRecord2RootRoute() {
        if (record2RootRoute == null) {
            DatabaseContract<Record> contract = getRecord2Contract();
            record2RootRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.record2")
                    .setPath(contract.getTableName())
                    .setGetWhereFunc(uri -> null)
                    .build();
        }
        return record2RootRoute;
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
