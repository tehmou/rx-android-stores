package com.tehmou.rxandroidstores.route;

import android.net.Uri;

import com.tehmou.rxandroidstores.contract.DatabaseContract;

import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

/**
 * Created by ttuo on 04/05/15.
 */
public class DatabaseRouteBase implements DatabaseQueryRoute {
    private final String tableName;
    private final String path;
    private final String sortOrder;
    private final Func1<Uri, String> getWhereFunc;
    private final String mimeType;
    private Action2<Uri, Action1<Uri>> notifyChangeFunc;

    private DatabaseRouteBase(Builder builder) {
        this.tableName = builder.tableName;
        this.path = builder.path;
        this.sortOrder = builder.sortOrder;
        this.getWhereFunc = builder.getWhereFunc;
        this.mimeType = builder.mimeType;
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
    public String getPath() {
        return path;
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
        private String path;
        private String sortOrder;
        private Func1<Uri, String> getWhereFunc;
        private String mimeType;
        private Action2<Uri, Action1<Uri>> notifyChangeFunc =
                (uri, notifyChange) -> notifyChange.call(uri);

        public Builder(DatabaseContract databaseContract) {
            this.tableName = databaseContract.getTableName();
            this.sortOrder = databaseContract.getDefaultSortOrder();
            this.getWhereFunc = databaseContract.getDefaultWhereFunc();
        }

        public Builder setNotifyChangeFunc(Action2<Uri, Action1<Uri>> notifyChangeFunc) {
            this.notifyChangeFunc = notifyChangeFunc;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
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

        public DatabaseQueryRoute build() {
            return new DatabaseRouteBase(this);
        }
    }
}
