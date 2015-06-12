package com.tehmou.rxandroidstores.example.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.provider.ContentProviderBase;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRoute;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 04/06/15.
 */
public class Foobar2ExampleContentProvider extends ContractContentProviderBase {
    private static final String TAG = Foobar2ExampleContentProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.provider.Foobar2ExampleContentProvider";
    private static final String DATABASE_NAME = "foobar2_database";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseContract<Foobar2> foobar2Contract;
    private static DatabaseRouteBase foobar2IdRoute;
    private static DatabaseRouteBase foobar2CountryRoute;
    private static DatabaseRouteBase foobar2RootRoute;

    public Foobar2ExampleContentProvider() {
        DatabaseContract<Foobar2> foobar2Contract = getFoobar2Contract();
        addDatabaseContract(foobar2Contract);

        // Notice that the order has to be from more restrictive to less restrictive.
        DatabaseRouteBase idRoute = getFoobar2IdRoute();
        addDatabaseDeleteRoute(idRoute);
        addDatabaseInsertRoute(idRoute);
        addDatabaseUpdateRoute(idRoute);
        addDatabaseQueryRoute(idRoute);

        // Country route
        DatabaseRouteBase countryRoute = getFoobar2CountryRoute();
        addDatabaseQueryRoute(countryRoute);

        // Root route
        DatabaseRouteBase rootRoute = getFoobar2RootRoute();
        addDatabaseQueryRoute(rootRoute);
        addDatabaseDeleteRoute(rootRoute);
    }

    public static DatabaseContract<Foobar2> getFoobar2Contract() {
        if (foobar2Contract == null) {
            final Gson gson = new Gson();
            foobar2Contract = new DatabaseContractBase.Builder<Foobar2>()
                    .setTableName("foobars2")
                    .setProjection(new String[]{"id", "country", "json"})
                    .setCreateTableSql("CREATE TABLE foobars2 (id INTEGER, country STRING, json TEXT NOT NULL)")
                    .setDropTableSql("DROP TABLE IF EXISTS foobars2")
                    .setReadFunc(cursor -> {
                        final String json = cursor.getString(cursor.getColumnIndex("json"));
                        return gson.fromJson(json, Foobar2.class);
                    })
                    .setGetContentValuesForItemFunc(foobar2 -> {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", foobar2.getId());
                        contentValues.put("country", foobar2.getCountry());
                        contentValues.put("json", gson.toJson(foobar2));
                        return contentValues;
                    })
                    .build();
        }
        return foobar2Contract;
    }

    public static DatabaseRouteBase getFoobar2IdRoute() {
        if (foobar2IdRoute == null) {
            DatabaseContract<Foobar2> contract = getFoobar2Contract();
            foobar2IdRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar2")
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
        return foobar2IdRoute;
    }

    public static DatabaseRouteBase getFoobar2CountryRoute() {
        if (foobar2CountryRoute == null) {
            DatabaseContract<Foobar2> contract = getFoobar2Contract();
            foobar2CountryRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.foobar2")
                    .setPath(contract.getTableName() + "/country/*")
                    .setGetWhereFunc(uri -> "country = '" + uri.getLastPathSegment() + "'")
                    .build();
        }
        return foobar2CountryRoute;
    }

    public static DatabaseRouteBase getFoobar2RootRoute() {
        if (foobar2RootRoute == null) {
            DatabaseContract<Foobar2> contract = getFoobar2Contract();
            foobar2RootRoute = new DatabaseRouteBase.Builder(contract)
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.foobar2")
                    .setPath(contract.getTableName())
                    .setGetWhereFunc(uri -> null)
                    .build();
        }
        return foobar2RootRoute;
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
