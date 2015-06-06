rx-android-stores
=================

With this utility you can define an entire Android ContentProvider in one file with less than 100 lines. It also includes an RxJava wrapper, a Store. The definition of a ContentProvider is split into two parts: DatabaseContracts and DatabaseRoutes.


#### Contract

Defines a table structure


#### Route

Maintains a URI pattern and queries the tables based on it


Using the library
----------------

### Defining the ContentProvider

This is an entire ContentProvider declaration:

    public class ExampleContentProvider extends ContractContentProviderBase {
        public static final String PROVIDER_NAME = "com.tehmou.rxandroidstores.example.provider.ExampleContentProvider";
        private static final String DATABASE_NAME = "database";
        private static final int DATABASE_VERSION = 1;

        public ExampleContentProvider() {
            DatabaseContract<Foobar> contract = createFoobarContract();
            addDatabaseContract(contract);
            addDatabaseRoute(
                    new DatabaseRouteBase.Builder(contract)
                            .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar")
                            .setPath(contract.getTableName() + "/*")
                            .build());
        }

        public static DatabaseContract<Foobar> createFoobarContract() {
            return SerializedJsonContract.<Foobar>createBuilder(
                    "foobars", "INTEGER", Foobar.class, value -> {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(SerializedJsonContract.ID, value.getId());
                        return contentValues;
                    })
                    .build();
        }

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
    }


### Defining a contract

Contract represent a table in the database.

SerializedJsonContract is helper to make a table that serializes the items as json strings, queryable with an id.

Here we create a json table of the name "foobars" and id column type integer. The value converter is the last, which adds the id field to the ContentValues.

    SerializedJsonContract.<Foobar>createBuilder(
            "foobars", "INTEGER", Foobar.class, value -> {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SerializedJsonContract.ID, value.getId());
                return contentValues;
            })
            .build()


### Defining routes

A route defines a uri scheme that can query items from the table. One table can have multiple routes - in this case you might want to implement custom notify function for tracking change.

This would match any uri of form "foobars/1234":

    new DatabaseRouteBase.Builder(contract)
            .setMimeType("vnd.android.cursor.item/vnd.tehmou.android.rxandroidstores.foobar")
            .setPath(contract.getTableName() + "/*")
            .build());


This can be used for querying all foobars with "foobars":

    new DatabaseRouteBase.Builder(contract)
            .setMimeType("vnd.android.cursor.dir/vnd.tehmou.android.rxandroidstores.foobar")
            .setPath(contract.getTableName())
            .setGetWhereFunc(uri -> null)
            .build());


In the latter one we need to replace the default getWhere function since we want to query all foobards without any conditions.


### Creating a Store

A Store is an abstraction that uses an identifier to look for data items. The identifier is converted into path by appending to the contentUriBase.

Item inserted must contain the necessary identifying information, usually an integer id field.


    public class FoobarStore extends ContentProviderStoreBase<Integer, Foobar> {
        public FoobarStore(ContentResolver contentResolver) {
            super(contentResolver, ExampleContentProvider.createFoobarContract());
        }

        @Override
        protected Integer getIdFor(Foobar item) {
            return item.getId();
        }

        @Override
        protected Uri getContentUriBase() {
            return Uri.parse("content://" + ExampleContentProvider.PROVIDER_NAME + "/"
                    + databaseContract.getTableName());
        }
    }

Using the Store:

    FoobarStore store = new FoobarStore(getContentResolver());
    store.getStream(1234).subscribe(
            value -> Log.d(TAG, "onNext: " + value.getValue());
    store.put(new Foobar(1234, "My text"));

The subscriber set before the put will receive the value as soon as it is inserted. Since the value is written into the ContentProvider you would see the previous one immediately if you restart.


Importing the library
-----------------

    repositories {
        mavenCentral()
        maven {
            url 'https://raw.github.com/tehmou/rx-android-stores/snapshots/'
        }
    }

    dependencies {
        compile 'com.tehmou:rx-android-stores:0.0.1'
    }
