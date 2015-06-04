package com.tehmou.rxandroidarchitecture.contract;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by ttuo on 13/01/15.
 */
abstract public class SerializedJsonContract {
    private static final String TAG = SerializedJsonContract.class.getSimpleName();

    public static final String ID = "id";
    public static final String JSON = "json";

    private static final String[] PROJECTION = new String[]{ ID, JSON };

    private static String getCreateTable(final String tableName,
                                         final String idColumnType,
                                         final String jsonColumn) {
        return " CREATE TABLE " + tableName
                + " (" + ID + " " + idColumnType + ", "
                + jsonColumn + " TEXT NOT NULL);";
    }

    private static String getDropTable(final String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public static <T> DatabaseContractBase.Builder<T> createBuilder(final String tableName,
                                                                    final Type type) {
        return createBuilder(tableName, "INTEGER", type);
    }

    public static <T> DatabaseContractBase.Builder<T> createBuilder(final String tableName,
                                                                    final String idColumnType,
                                                                    final Type type) {
        Log.v(TAG, "createBuilder(" + tableName + ", " + idColumnType + ", " + type);
        return new DatabaseContractBase.Builder<T>()
                .setTableName(tableName)
                .setCreateTableSql(getCreateTable(tableName, idColumnType, JSON))
                .setProjection(PROJECTION)
                .setDropTableSql(getDropTable(tableName))
                .setGetDefaultWhereFunc(
                        uri -> SerializedJsonContract.ID + " = " + uri.getLastPathSegment())
                .setReadFunc(cursor -> {
                    final String json = cursor.getString(cursor.getColumnIndex(JSON));
                    return new Gson().fromJson(json, type);
                });
    }
}
