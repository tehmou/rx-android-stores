package com.tehmou.rxandroidstores.contract;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import rx.android.internal.Preconditions;
import rx.functions.Func1;

/**
 * Created by ttuo on 04/06/15.
 */
public class DatabaseContractBase<T> implements DatabaseContract<T> {
    final private String createTableSql;
    final private String dropTableSql;
    final private String tableName;
    final private Func1<Cursor, T> readFunc;
    final private Func1<T, ContentValues> getContentValuesForItemFunc;
    final private String[] projection;
    final private String defaultSortOrder;
    final Func1<Uri, String> getDefaultWhereFunc;

    private DatabaseContractBase(String createTableSql,
                                 String dropTableSql,
                                 String tableName,
                                 Func1<Cursor, T> readFunc,
                                 Func1<T, ContentValues> getContentValuesForItemFunc,
                                 String[] projection,
                                 String defaultSortOrder,
                                 Func1<Uri, String> getDefaultWhereFunc) {
        Preconditions.checkNotNull(tableName, "Missing tableName");
        Preconditions.checkNotNull(createTableSql, "Missing createTableSql");
        Preconditions.checkNotNull(dropTableSql, "Missing dropTableSql");
        this.createTableSql = createTableSql;
        this.dropTableSql = dropTableSql;
        this.tableName = tableName;
        this.readFunc = readFunc;
        this.getContentValuesForItemFunc = getContentValuesForItemFunc;
        this.projection = projection;
        this.defaultSortOrder = defaultSortOrder;
        this.getDefaultWhereFunc = getDefaultWhereFunc;
    }

    @Override
    public String getCreateTable() {
        return createTableSql;
    }

    @Override
    public String getDropTable() {
        return dropTableSql;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public T read(Cursor cursor) {
        return readFunc.call(cursor);
    }

    @Override
    public ContentValues getContentValuesForItem(T item) {
        return getContentValuesForItemFunc.call(item);
    }

    @Override
    public String[] getProjection() {
        return projection;
    }

    @Override
    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    @Override
    public Func1<Uri, String> getDefaultWhereFunc() {
        return getDefaultWhereFunc;
    }

    public static class Builder<T> {
        private String createTableSql;
        private String dropTableSql;
        private String tableName;
        private Func1<Cursor, T> readFunc;
        private Func1<T, ContentValues> getContentValuesForItemFunc;
        private String[] projection;
        private String defaultSortOrder;
        private Func1<Uri, String> getDefaultWhereFunc;

        public Builder<T> setCreateTableSql(String createTableSql) {
            this.createTableSql = createTableSql;
            return this;
        }

        public Builder<T> setDropTableSql(String dropTableSql) {
            this.dropTableSql = dropTableSql;
            return this;
        }

        public Builder<T> setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder<T> setReadFunc(Func1<Cursor, T> readFunc) {
            this.readFunc = readFunc;
            return this;
        }

        public Builder<T> setGetContentValuesForItemFunc(Func1<T, ContentValues> getContentValuesForItemFunc) {
            this.getContentValuesForItemFunc = getContentValuesForItemFunc;
            return this;
        }

        public Builder<T> setProjection(String[] projection) {
            this.projection = projection;
            return this;
        }

        public Builder<T> setDefaultSortOrder(String defaultSortOrder) {
            this.defaultSortOrder = defaultSortOrder;
            return this;
        }

        public Builder<T> setGetDefaultWhereFunc(Func1<Uri, String> getDefaultWhereFunc) {
            this.getDefaultWhereFunc = getDefaultWhereFunc;
            return this;
        }

        public DatabaseContractBase<T> build() {
            return new DatabaseContractBase<T>(
                    createTableSql,
                    dropTableSql,
                    tableName,
                    readFunc,
                    getContentValuesForItemFunc,
                    projection,
                    defaultSortOrder,
                    getDefaultWhereFunc);
        }
    }
}
