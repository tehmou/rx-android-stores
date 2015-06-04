rx-android-stores
=================

With this utility you can define an entire Android ContentProvider in one file with less than 100 lines. It also includes an RxJava wrapper, a Store.

It has some limitation for declaring exotic tables in a beautiful way, but overall it is extremely flexible.


Components
----------

DatabaseContract: Define the table in the database.
DatabaseRoute: Defines a uri scheme that can query items from the table. One table can have multiple routes - in this case you might want to implement custom notify function for tracking change.
SerializedJsonContract: Helper to make a table that serializes the items as json strings, queryable with an id.


Using the library
----------------

### Defining a contract

Here we create a json table of the name "foobars" and id column type integer. The value converter is the last, which adds the id field to the ContentValues.

    SerializedJsonContract.<Foobar>createBuilder(
            "foobars", "INTEGER", Foobar.class, value -> {
                ContentValues contentValues = new ContentValues();
                contentValues.put(SerializedJsonContract.ID, value.getId());
                return contentValues;
            })
            .build()


### Defining routes

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


### Defining the ContentProvider

For an entire ContentProvider check [ExampleContentProvider](https://github.com/tehmou/rx-android-stores/blob/master/app/src/main/java/com/tehmou/rxandroidstores/example/provider/ExampleContentProvider.java)


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
        compile 'com.tehmou:rx-android-stores:+'
    }
