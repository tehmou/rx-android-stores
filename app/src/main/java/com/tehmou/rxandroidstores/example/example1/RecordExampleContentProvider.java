package com.tehmou.rxandroidstores.example.example1;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;
import com.tehmou.rxandroidstores.utils.DatabaseUtils;

/**
 * Created by ttuo on 04/06/15.
 */
public class RecordExampleContentProvider extends ContractContentProviderBase {
    private static final String TAG = RecordExampleContentProvider.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.example1.RecordExampleContentProvider";
    private static final String DATABASE_NAME = "record_database";
    private static final int DATABASE_VERSION = 3;

    private static DatabaseContract<Record> recordContract;
    private static DatabaseRouteBase recordIdRoute;
    private static DatabaseRouteBase recordRootRoute;

    public RecordExampleContentProvider() {
        DatabaseContract<Record> recordContract = getRecordContract();
        addDatabaseContract(recordContract);

        DatabaseRouteBase idRoute = getIdRoute();
        addDatabaseInsertRoute(idRoute);
        addDatabaseUpdateRoute(idRoute);
        addDatabaseDeleteRoute(idRoute);
        addDatabaseQueryRoute(idRoute);

        DatabaseRouteBase rootRoute = getRootRoute();
        addDatabaseDeleteRoute(rootRoute);
        addDatabaseQueryRoute(rootRoute);
    }

    public static DatabaseContract<Record> getRecordContract() {
        if (recordContract == null) {
            final Gson gson = new Gson();
            recordContract = new DatabaseContractBase.Builder<Record>()
                    .setTableName("records")
                    .setProjection(new String[]{"id", "json"})
                    .setCreateTableSqlFunc(
                            tableName -> "CREATE TABLE " + tableName + " (id INTEGER, json TEXT NOT NULL)")
                    .setDropTableSqlFunc(DatabaseUtils.dropTableSqlFunc)
                    .setReadFunc(cursor -> {
                        final String json = cursor.getString(cursor.getColumnIndex("json"));
                        return gson.fromJson(json, Record.class);
                    })
                    .setGetContentValuesForItemFunc(record -> {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", record.getId());
                        contentValues.put("json", gson.toJson(record));
                        return contentValues;
                    })
                    .build();
        }
        return recordContract;
    }

    public static DatabaseRouteBase getIdRoute() {
        if (recordIdRoute == null) {
            // The root notifyChange is triggered automatically when
            // a value updates. No need to override.
            recordIdRoute = new DatabaseRouteBase.Builder(recordContract)
                    .setMimeType("vnd.android.cursor.item/vnd.tehmou.rxandroidstores.example.pojo.record")
                    .setPathFunc(DatabaseUtils.getIdPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereByIdFunc)
                    .build();
        }
        return recordIdRoute;
    }

    public static DatabaseRouteBase getRootRoute() {
        if (recordRootRoute == null) {
            recordRootRoute = new DatabaseRouteBase.Builder(recordContract)
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.rxandroidstores.example.pojo.record")
                    .setPathFunc(DatabaseUtils.getRootPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereRootFunc)
                    .build();
        }
        return recordRootRoute;
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
