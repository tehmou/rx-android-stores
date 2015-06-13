package com.tehmou.rxandroidstores.example.example3;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.example.pojo.User;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;
import com.tehmou.rxandroidstores.utils.DatabaseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ttuo on 13/06/15.
 */
public class Record3ExampleContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.example3.Record3ExampleContentProvider";
    private static final String DATABASE_NAME = "record3_database";
    private static final int DATABASE_VERSION = 4;

    private static DatabaseContractBase<Record> record3Contract;
    private static DatabaseContractBase<User> userContract;
    private static DatabaseRouteBase recordRootRoute;
    private static DatabaseRouteBase userRootRoute;
    private static DatabaseQueryRoute userForRecordIdRoute;

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

    public Record3ExampleContentProvider() {
        addDatabaseContract(getRecordContract());
        addDatabaseContract(getUserContract());

        addDatabaseInsertRoute(getRecordRootRoute());
        addDatabaseDeleteRoute(getRecordRootRoute());

        addDatabaseInsertRoute(getUserRootRoute());
        addDatabaseDeleteRoute(getUserRootRoute());

        addDatabaseQueryRoute(getUserForRecordIdRoute());
    }

    public static DatabaseContract<Record> getRecordContract() {
        if (record3Contract == null) {
            final Gson gson = new Gson();
            record3Contract = new DatabaseContractBase.Builder<Record>()
                    .setTableName("records")
                    .setCreateTableSqlFunc(tableName ->
                            "CREATE TABLE " + tableName
                                    + " (id INTEGER, user_id INTEGER, json TEXT NOT NULL)")
                    .setDropTableSqlFunc(DatabaseUtils.dropTableSqlFunc)
                    .setGetContentValuesForItemFunc(
                            value -> {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("id", value.getId());
                                contentValues.put("user_id", value.getUserId());
                                contentValues.put("json", gson.toJson(value));
                                return contentValues;
                            })
                    .build();
        }
        return record3Contract;
    }

    public static DatabaseContract<User> getUserContract() {
        if (userContract == null) {
            final Gson gson = new Gson();
            userContract = new DatabaseContractBase.Builder<User>()
                    .setTableName("users")
                    .setProjection(new String[]{"id", "json"})
                    .setCreateTableSqlFunc(DatabaseUtils.createJsonIdTableSqlFunc)
                    .setDropTableSqlFunc(DatabaseUtils.dropTableSqlFunc)
                    .setReadFunc(DatabaseUtils.readJsonFunc(
                            gson, new TypeToken<User>() {
                            }))
                    .setGetContentValuesForItemFunc(
                            DatabaseUtils.getContentValuesForItemIdJsonFunc(
                                    gson, user -> String.valueOf(user.getId())))
                    .build();
        }
        return userContract;
    }

    public static DatabaseRouteBase getRecordRootRoute() {
        if (recordRootRoute == null) {
            recordRootRoute = new DatabaseRouteBase.Builder(getRecordContract())
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.rxandroidstores.example.pojo.record")
                    .setPathFunc(DatabaseUtils.getRootPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereRootFunc)
                    .build();
        }
        return recordRootRoute;
    }

    public static DatabaseRouteBase getUserRootRoute() {
        if (userRootRoute == null) {
            userRootRoute = new DatabaseRouteBase.Builder(getUserContract())
                    .setMimeType("vnd.android.cursor.dir/vnd.tehmou.rxandroidstores.example.pojo.user")
                    .setPathFunc(DatabaseUtils.getRootPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereRootFunc)
                    .build();
        }
        return userRootRoute;
    }

    public static DatabaseQueryRoute getUserForRecordIdRoute() {
        if (userForRecordIdRoute == null) {
            userForRecordIdRoute = new DatabaseQueryRoute() {
                @Override
                public String getSortOrder() {
                    return "id ASC";
                }

                @Override
                public String getMimeType() {
                    return "vnd.android.cursor.item/vnd.tehmou.rxandroidstores.example.pojo.user";
                }

                @Override
                public String getPath() {
                    return "records/*/user";
                }

                @Override
                public String getTableName() {
                    return "records LEFT OUTER JOIN users ON (records.user_id = users.id)";
                }

                @Override
                public String getWhere(Uri uri) {
                    return null;
                }

                @Override
                public Map<String, String> getProjectionMap() {
                    Map<String, String> projectionMap = new HashMap<>();
                    projectionMap.put("users.id", "users.id AS id");
                    projectionMap.put("users.json", "users.json AS json");
                    return projectionMap;
                }
            };
        }
        return userForRecordIdRoute;
    }
}
