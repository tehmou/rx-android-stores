package com.tehmou.rxandroidstores.route;

import android.content.ContentValues;
import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;

import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Func1;

/**
 * Created by ttuo on 04/05/15.
 */
public class DatabaseRouteBase implements DatabaseQueryRoute, DatabaseInsertUpdateRoute, DatabaseDeleteRoute {
    private final String tableName;
    private final Func1<String, String> pathFunc;
    private final String sortOrder;
    private final Func1<Uri, String> getWhereFunc;
    private final String mimeType;
    private Action3<ContentValues, Uri, Action1<Uri>> notifyChangeInsertFunc;
    private Action2<Uri, Action1<Uri>> notifyChangeFunc;

    private DatabaseRouteBase(Builder builder) {
        this.tableName = builder.tableName;
        this.pathFunc = builder.pathFunc;
        this.sortOrder = builder.sortOrder;
        this.getWhereFunc = builder.getWhereFunc;
        this.mimeType = builder.mimeType;
        this.notifyChangeInsertFunc = builder.notifyChangeInsertFunc;
        this.notifyChangeFunc = builder.notifyChangeFunc;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void notifyChange(Uri uri, Action1<Uri> notifyChange) {
        notifyChangeFunc.call(uri, notifyChange);
    }

    @Override
    public void notifyChange(ContentValues contentValues, Uri uri, Action1<Uri> notifyChange) {
        // First check the more specific notifyChange function
        if (notifyChangeInsertFunc != null) {
            notifyChangeInsertFunc.call(contentValues, uri, notifyChange);
        } else {
            notifyChangeFunc.call(uri, notifyChange);
        }
    }

    @Override
    public String getPath() {
        return pathFunc.call(tableName);
    }

    @Override
    public String getSortOrder() {
        return sortOrder;
    }

    @Override
    public String getWhere(Uri uri) {
        return getWhereFunc.call(uri);
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    public static class Builder {
        private String tableName;
        private Func1<String, String> pathFunc;
        private String sortOrder;
        private Func1<Uri, String> getWhereFunc;
        private String mimeType;
        private Action3<ContentValues, Uri, Action1<Uri>> notifyChangeInsertFunc;
        private Action2<Uri, Action1<Uri>> notifyChangeFunc =
                (uri, notifyChange) -> notifyChange.call(uri);

        public Builder(DatabaseContract databaseContract) {
            this.tableName = databaseContract.getTableName();
            this.sortOrder = databaseContract.getDefaultSortOrder();
            this.getWhereFunc = databaseContract.getDefaultWhereFunc();
        }

        public Builder setNotifyChangeInsertFunc(
                Action3<ContentValues, Uri, Action1<Uri>> notifyChangeInsertFunc) {
            this.notifyChangeInsertFunc = notifyChangeInsertFunc;
            return this;
        }

        public Builder setNotifyChangeFunc(
                Action2<Uri, Action1<Uri>> notifyChangeFunc) {
            this.notifyChangeFunc = notifyChangeFunc;
            return this;
        }

        public Builder setPathFunc(Func1<String, String> pathFunc) {
            this.pathFunc = pathFunc;
            return this;
        }

        public Builder setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder setGetWhereFunc(Func1<Uri, String> getWhereFunc) {
            this.getWhereFunc = getWhereFunc;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public DatabaseRouteBase build() {
            return new DatabaseRouteBase(this);
        }
    }
}
