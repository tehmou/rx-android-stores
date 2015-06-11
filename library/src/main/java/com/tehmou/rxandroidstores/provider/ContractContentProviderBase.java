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
import com.tehmou.rxandroidstores.route.DatabaseInsertRoute;
import com.tehmou.rxandroidstores.route.DatabaseQueryRoute;
import com.tehmou.rxandroidstores.route.DatabaseRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttuo on 10/01/15.
 */
abstract public class ContractContentProviderBase extends ContentProviderBase {
    private static final String TAG = ContractContentProviderBase.class.getSimpleName();
    private final List<DatabaseContract> databaseContracts = new ArrayList<>();
    private final List<DatabaseInsertRoute> databaseInsertRoutes = new ArrayList<>();
    private final List<DatabaseDeleteRoute> databaseDeleteRoutes = new ArrayList<>();
    private final List<DatabaseQueryRoute> databaseQueryRoutes = new ArrayList<>();

    protected void addDatabaseContract(DatabaseContract databaseContract) {
        assert(databaseHelper == null);
        databaseContracts.add(databaseContract);
    }

    protected void addDatabaseInsertRoute(DatabaseInsertRoute databaseRoute) {
        assert(databaseHelper == null);
        databaseInsertRoutes.add(databaseRoute);
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
    protected String getTableNameInsert(int match) {
        return getDatabaseInsertRouteForMatch(match).getTableName();
    }

    @Override
    protected String getTableNameDelete(int match) {
        return getDatabaseDeleteRouteForMatch(match).getTableName();
    }

    @Override
    protected String getTableNameQuery(int match) {
        return getDatabaseQueryRouteForMatch(match).getTableName();
    }

    @Override
    protected String getWhereInsert(int match, Uri uri) {
        return getDatabaseInsertRouteForMatch(match).getWhere(uri);
    }

    @Override
    protected String getWhereDelete(int match, Uri uri) {
        return getDatabaseDeleteRouteForMatch(match).getWhere(uri);
    }

    @Override
    protected String getWhereQuery(int match, Uri uri) {
        return getDatabaseQueryRouteForMatch(match).getWhere(uri);
    }

    @Override
    protected String getDefaultSortOrderQuery(int match) {
        return getDatabaseQueryRouteForMatch(match).getSortOrder();
    }

    @Override
    public String getType(Uri uri) {
        // We assume the query uri makes sense here
        final int match = URI_MATCHER_QUERY.match(uri);
        return getDatabaseQueryRouteForMatch(match).getMimeType();
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper(Context context) {
        return new DatabaseHelper(context, getDatabaseName(),
                getDatabaseVersion(), databaseContracts);
    }

    @Override
    protected void createUriMatchers() {
        URI_MATCHER_INSERT = createUriMatcher(databaseInsertRoutes);
        URI_MATCHER_DELETE = createUriMatcher(databaseDeleteRoutes);
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

    protected DatabaseInsertRoute getDatabaseInsertRouteForMatch(final int match) {
        return databaseInsertRoutes.get(match);
    }

    protected DatabaseDeleteRoute getDatabaseDeleteRouteForMatch(final int match) {
        return databaseDeleteRoutes.get(match);
    }

    protected DatabaseQueryRoute getDatabaseQueryRouteForMatch(final int match) {
        return databaseQueryRoutes.get(match);
    }

    @Override
    protected void notifyChange(int match, Uri uri) {
        getDatabaseDeleteRouteForMatch(match).notifyChange(uri,
                (value) -> getContext().getContentResolver().notifyChange(value, null));
    }

    @Override
    protected void notifyChange(int match, ContentValues contentValues, Uri uri) {
        getDatabaseInsertRouteForMatch(match).notifyChange(contentValues, uri,
                (value) -> getContext().getContentResolver().notifyChange(value, null));
    }

    abstract protected String getProviderName();
    abstract protected String getDatabaseName();
    abstract protected int getDatabaseVersion();
}
