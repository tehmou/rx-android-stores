package com.tehmou.rxandroidarchitecture.route;

import android.net.Uri;

import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by ttuo on 04/05/15.
 */
public class DatabaseRouteBase implements DatabaseRoute {
    private final String tableName;
    private final String path;
    private final String defaultSortOrder;
    private final Func1<Uri, String> getWhereFunc;
    private final String mimeType;
    private Action2<Uri, Action1<Uri>> notifyChangeFunc;

    private DatabaseRouteBase(Builder builder) {
        this.tableName = builder.tableName;
        this.path = builder.path;
        this.defaultSortOrder = builder.defaultSortOrder;
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
    public String getDefaultSortOrder() {
        return defaultSortOrder;
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
        private String defaultSortOrder;
        private Func1<Uri, String> getWhereFunc;
        private String mimeType;
        private Action2<Uri, Action1<Uri>> notifyChangeFunc =
                (uri, notifyChange) -> notifyChange.call(uri);

        public Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder setNotifyChangeFunc(Action2<Uri, Action1<Uri>> notifyChangeFunc) {
            this.notifyChangeFunc = notifyChangeFunc;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setDefaultSortOrder(String defaultSortOrder) {
            this.defaultSortOrder = defaultSortOrder;
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

        public DatabaseRoute build() {
            return new DatabaseRouteBase(this);
        }
    }
}
