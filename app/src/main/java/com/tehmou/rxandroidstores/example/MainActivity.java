package com.tehmou.rxandroidstores.example;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tehmou.bettercontentprovider.R;
import com.tehmou.rxandroidstores.example.example3.Record3ExampleContentProvider;
import com.tehmou.rxandroidstores.example.pojo.CountryIdKey;
import com.tehmou.rxandroidstores.example.pojo.Record;
import com.tehmou.rxandroidstores.example.example2.Record2ByCountryStore;
import com.tehmou.rxandroidstores.example.example2.Record2ExampleContentProvider;
import com.tehmou.rxandroidstores.example.example2.Record2IdStore;
import com.tehmou.rxandroidstores.example.example1.RecordExampleContentProvider;
import com.tehmou.rxandroidstores.example.example1.RecordIdStore;
import com.tehmou.rxandroidstores.example.example1.RecordRootStore;
import com.tehmou.rxandroidstores.example.pojo.User;

import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        example1();
        example2();
        example3();
    }

    private void example1() {
        // Clean up the DB
        getContentResolver().delete(
                Uri.parse("content://" + RecordExampleContentProvider.PROVIDER_NAME + "/records"),
                null, null);

        // Record root store
        RecordRootStore recordRootStore = new RecordRootStore(getContentResolver(),
                RecordExampleContentProvider.getRecordContract());
        recordRootStore.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordList -> {
                    Log.d(TAG, "Record root: " + recordList.size());
                    ((TextView) findViewById(R.id.text1)).setText(
                            recordList.size() > 0 ? "Success" : "Fail");
                });

        // Record store by id
        RecordIdStore recordIdStore = new RecordIdStore(getContentResolver(),
                RecordExampleContentProvider.getRecordContract());
        recordIdStore.getStream(5678)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(record -> {
                    Log.d(TAG, "Record: " + record.getValue());
                    ((TextView) findViewById(R.id.text2)).setText(record.getValue());
                });
        recordIdStore.put(new Record(5678, "GER", 200, "Success"));
    }

    private void example2() {
        // Clean up the DB
        getContentResolver().delete(
                Uri.parse("content://" + Record2ExampleContentProvider.PROVIDER_NAME + "/records"),
                null, null);

        // Record2 stores
        Record2IdStore record2IdStore = new Record2IdStore(getContentResolver(),
                Record2ExampleContentProvider.getRecord2Contract());
        Record2ByCountryStore countryStore = new Record2ByCountryStore(getContentResolver(),
                Record2ExampleContentProvider.getRecord2Contract());
        countryStore.getStream("FIN")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordList -> {
                    Log.d(TAG, "Record2 list: " + recordList.size());
                    ((TextView) findViewById(R.id.text3)).setText(
                            recordList.size() > 0 ? "Success" : "Fail");
                });
        record2IdStore.getStream(new CountryIdKey("FIN", 1234))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(record2 -> {
                    Log.d(TAG, "Record2: " + record2.getValue());
                    ((TextView) findViewById(R.id.text4)).setText(record2.getValue());
                });
        record2IdStore.put(new Record(1234, "FIN", 100, "Success"));
    }

    private void example3() {
        ContentResolver contentResolver = getContentResolver();

        // Clean up the DB
        contentResolver.delete(
                Uri.parse("content://" + Record3ExampleContentProvider.PROVIDER_NAME + "/records"),
                null, null);
        contentResolver.delete(
                Uri.parse("content://" + Record3ExampleContentProvider.PROVIDER_NAME + "/users"),
                null, null);

        final Record record = new Record(1234, "AU", 4, "Success");
        final User user = new User(4, "Peter", "eater@example.com");
        contentResolver.insert(
                Uri.parse("content://" + Record3ExampleContentProvider.PROVIDER_NAME + "/records"),
                Record3ExampleContentProvider.getRecordContract().getContentValuesForItem(record));
        contentResolver.insert(
                Uri.parse("content://" + Record3ExampleContentProvider.PROVIDER_NAME + "/users"),
                Record3ExampleContentProvider.getUserContract().getContentValuesForItem(user));
        Cursor cursor = contentResolver.query(
                Uri.parse("content://" + Record3ExampleContentProvider.PROVIDER_NAME + "/records/4/user"),
                null,
                null, null, null);
        cursor.moveToFirst();
        final User user2 = Record3ExampleContentProvider.getUserContract().read(cursor);
        cursor.close();
        if (user2 != null) {
            ((TextView) findViewById(R.id.text5)).setText("Success");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
