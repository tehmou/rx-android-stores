package com.tehmou.rxandroidstores.example.example3;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.contract.DatabaseContractBase;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.example.pojo.User;
import com.tehmou.rxandroidstores.provider.ContractContentProviderBase;

/**
 * Created by ttuo on 13/06/15.
 */
public class Record3ExampleContentProvider extends ContractContentProviderBase {
    public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.example3.Record3ExampleContentProvider";
    private static final String DATABASE_NAME = "contract3_database";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseContractBase<Record> record3Contract;
    private static DatabaseContractBase<User> userContract;

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
                    .setCreateTableSql("CREATE TABLE records (id INTEGER, json TEXT NOT NULL)")
                    .setDropTableSql("DROP TABLE IF EXISTS records")
                    .build();
        }
        return record3Contract;
    }

    public static DatabaseContract<User> getUserContract() {
        if (userContract == null) {
            userContract = new DatabaseContractBase.Builder<User>()
                    .setTableName("users")
                    .setCreateTableSql("CREATE TABLE users (id INTEGER, json TEXT NOT NULL)")
                    .setDropTableSql("DROP TABLE IF EXISTS users")
                    .build();
        }
        return userContract;
    }
}
