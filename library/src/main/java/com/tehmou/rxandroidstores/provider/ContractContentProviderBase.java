package com.tehmou.rxandroidstores.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.tehmou.rxandroidstores.contract.DatabaseContract;
import com.tehmou.rxandroidstores.route.DatabaseDeleteRoute;
import com.tehmou.rxandroidstores.route.DatabaseInsertUpdateRoute;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 10/01/15.
 */
abstract public class ContractContentProviderBase extends ContentProviderBase {
    private static final String TAG = ContractContentProviderBase.class.getSimpleName();

    private UriMatcher URI_MATCHER_INSERT;
    private UriMatcher URI_MATCHER_UPDATE;
    private UriMatcher URI_MATCHER_DELETE;
    private UriMatcher URI_MATCHER_QUERY;

    private final List<DatabaseContract> databaseContracts = new ArrayList<>();

    private final List<DatabaseInsertUpdateRoute> databaseInsertRoutes = new ArrayList<>();
    private final List<DatabaseInsertUpdateRoute> databaseUpdateRoutes = new ArrayList<>();
    private final List<DatabaseDeleteRoute> databaseDeleteRoutes = new ArrayList<>();
    private final List<DatabaseQueryRoute> databaseQueryRoutes = new ArrayList<>();

    protected void addDatabaseContract(DatabaseContract databaseContract) {
        assert(databaseHelper == null);
        databaseContracts.add(databaseContract);
    }

    protected void addDatabaseInsertRoute(DatabaseInsertUpdateRoute databaseRoute) {
        assert(databaseHelper == null);
        databaseInsertRoutes.add(databaseRoute);
    }

    protected void addDatabaseUpdateRoute(DatabaseInsertUpdateRoute databaseRoute) {
        assert(databaseHelper == null);
        databaseUpdateRoutes.add(databaseRoute);
    }

    protected void addDatabaseDeleteRoute(DatabaseDeleteRoute databaseRoute) {
        assert(databaseHelper == null);
        databaseDeleteRoutes.add(databaseRoute);
    }

    protected void addDatabaseQueryRoute(DatabaseQueryRoute databaseRoute) {
        assert(databaseHelper == null);
        databaseQueryRoutes.add(databaseRoute);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        final List<DatabaseContract> databaseContracts;

        DatabaseHelper(Context context,
                       String databaseName,
                       int databaseVersion,
                       List<DatabaseContract> databaseContracts) {
            super(context, databaseName, null, databaseVersion);
            this.databaseContracts = databaseContracts;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, "onCreate");
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(TAG, "onUpgrade");
            for (DatabaseContract databaseContract : databaseContracts) {
                Log.v(TAG, "dropTable(" + databaseContract.getTableName() + ")");
                db.execSQL(databaseContract.getDropTable());
            }
            createTables(db);
        }

        private void createTables(SQLiteDatabase db) {
            for (DatabaseContract databaseContract : databaseContracts) {
                Log.v(TAG, "createTable(" + databaseContract.getTableName() + ")");
                db.execSQL(databaseContract.getCreateTable());
            }
        }
    }

    @Override
    protected DatabaseInsertUpdateRoute getInsertRoute(Uri uri) {
        final int match = URI_MATCHER_INSERT.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown insert URI: " + uri);
        }
        return databaseInsertRoutes.get(match);
    }

    @Override
    protected DatabaseInsertUpdateRoute getUpdateRoute(Uri uri) {
        final int match = URI_MATCHER_UPDATE.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown update URI: " + uri);
        }
        return databaseUpdateRoutes.get(match);
    }

    @Override
    protected DatabaseDeleteRoute getDeleteRoute(Uri uri) {
        final int match = URI_MATCHER_DELETE.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown delete URI: " + uri);
        }
        return databaseDeleteRoutes.get(match);
    }

    @Override
    protected DatabaseQueryRoute getQueryRoute(Uri uri) {
        final int match = URI_MATCHER_QUERY.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown query URI: " + uri);
        }
        return databaseQueryRoutes.get(match);
    }

    @Override
    public String getType(Uri uri) {
        // We assume the query uri makes sense here
        return getQueryRoute(uri).getMimeType();
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper(Context context) {
        return new DatabaseHelper(context, getDatabaseName(),
                getDatabaseVersion(), databaseContracts);
    }

    @Override
    protected void createUriMatchers() {
        Log.v(TAG, "Create insert matcher");
        URI_MATCHER_INSERT = createUriMatcher(databaseInsertRoutes);

        Log.v(TAG, "Create update matcher");
        URI_MATCHER_UPDATE = createUriMatcher(databaseUpdateRoutes);

        Log.v(TAG, "Create delete matcher");
        URI_MATCHER_DELETE = createUriMatcher(databaseDeleteRoutes);

        Log.v(TAG, "Create query matcher");
        URI_MATCHER_QUERY = createUriMatcher(databaseQueryRoutes);
    }

    protected UriMatcher createUriMatcher(List<? extends DatabaseRoute> databaseRoutes) {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        int i = 0;
        for (DatabaseRoute databaseRoute : databaseRoutes) {
            Log.v(TAG, "Add uri pattern " + getProviderName() + "/" + databaseRoute.getPath());
            uriMatcher.addURI(getProviderName(), databaseRoute.getPath(), i++);
        }
        return uriMatcher;
    }

    abstract protected String getProviderName();
    abstract protected String getDatabaseName();
    abstract protected int getDatabaseVersion();
}
