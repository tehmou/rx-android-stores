package com.tehmou.rxandroidstores.example.example3;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.example.pojo.User;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;
import com.tehmou.rxandroidstores.route.DatabaseRouteBase;
import com.tehmou.rxandroidstores.utils.DatabaseUtils;

/**
 * Created by ttuo on 13/06/15.
 */
public class Record3ExampleContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.example3.Record3ExampleContentProvider";
    private static final String DATABASE_NAME = "contract3_database";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseContractBase<Record> record3Contract;
    private static DatabaseContractBase<User> userContract;
    private static DatabaseRouteBase recordIdRoute;
    private static DatabaseRouteBase userIdRoute;
    private static DatabaseRouteBase userForRecordIdRoute;

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


    }

    public static DatabaseContract<Record> getRecordContract() {
        if (record3Contract == null) {
            record3Contract = new DatabaseContractBase.Builder<Record>()
                    .setTableName("records")
                    .setCreateTableSqlFunc(DatabaseUtils.createJsonIdTableSqlFunc)
                    .setDropTableSqlFunc(DatabaseUtils.dropTableSqlFunc)
                    .build();
        }
        return record3Contract;
    }

    public static DatabaseContract<User> getUserContract() {
        if (userContract == null) {
            userContract = new DatabaseContractBase.Builder<User>()
                    .setTableName("users")
                    .setCreateTableSqlFunc(DatabaseUtils.createJsonIdTableSqlFunc)
                    .setDropTableSqlFunc(DatabaseUtils.dropTableSqlFunc)
                    .build();
        }
        return userContract;
    }

    public static DatabaseRouteBase getRecordIdRoute() {
        if (recordIdRoute == null) {
            recordIdRoute = new DatabaseRouteBase.Builder(getRecordContract())
                    .setMimeType("vnd.android.cursor.item/vnd.tehmou.rxandroidstores.example.pojo.record")
                    .setPathFunc(DatabaseUtils.getIdPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereByIdFunc)
                    .build();
        }
        return recordIdRoute;
    }

    public static DatabaseRouteBase getUserIdRoute() {
        if (userIdRoute == null) {
            userIdRoute = new DatabaseRouteBase.Builder(getUserContract())
                    .setMimeType("vnd.android.cursor.item/vnd.tehmou.rxandroidstores.example.pojo.user")
                    .setPathFunc(DatabaseUtils.getIdPathFunc)
                    .setGetWhereFunc(DatabaseUtils.getWhereByIdFunc)
                    .build();
        }
        return userIdRoute;
    }

    public static DatabaseRouteBase getUserForRecordIdRoute() {
        return userForRecordIdRoute;
    }
}
