package com.tehmou.rxandroidstores.example;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tehmou.bettercontentprovider.R;
import com.tehmou.rxandroidstores.example.pojo.CountryIdKey;
import com.tehmou.rxandroidstores.example.pojo.Foobar;
import com.tehmou.rxandroidstores.example.pojo.Foobar2;
import com.tehmou.rxandroidstores.example.provider.Foobar2ByCountryStore;
import com.tehmou.rxandroidstores.example.provider.Foobar2ExampleContentProvider;
import com.tehmou.rxandroidstores.example.provider.Foobar2IdStore;
import com.tehmou.rxandroidstores.example.provider.FoobarExampleContentProvider;
import com.tehmou.rxandroidstores.example.provider.FoobarIdStore;

import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        // Clean up the DB
        getContentResolver().delete(
                Uri.parse("content://" + FoobarExampleContentProvider.PROVIDER_NAME + "/foobars"),
                null, null);
        getContentResolver().delete(
                Uri.parse("content://" + Foobar2ExampleContentProvider.PROVIDER_NAME + "/foobars2"),
                null, null);

        // Foobar stores
        FoobarIdStore foobarIdStore = new FoobarIdStore(getContentResolver(),
                FoobarExampleContentProvider.getFoobarContract());
        foobarIdStore.getStream(1234)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foobar -> {
                    Log.d(TAG, "Foobar: " + foobar.getValue());
                    ((TextView) findViewById(R.id.text1)).setText(foobar.getValue());
                });
        foobarIdStore.put(new Foobar(1234, "Success"));

        // Foobar2 stores
        Foobar2IdStore foobar2IdStore = new Foobar2IdStore(getContentResolver(),
                Foobar2ExampleContentProvider.getFoobar2Contract());
        Foobar2ByCountryStore countryStore = new Foobar2ByCountryStore(getContentResolver(),
                Foobar2ExampleContentProvider.getFoobar2Contract());
        countryStore.getStream("FIN")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foobarList -> {
                    Log.d(TAG, "Foobar2 list: " + foobarList.size());
                    ((TextView) findViewById(R.id.text2)).setText(
                            foobarList.size() > 0 ? "Success" : "Fail");
                });
        foobar2IdStore.getStream(new CountryIdKey("FIN", 1234))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foobar2 -> {
                    Log.d(TAG, "Foobar2: " + foobar2.getValue());
                    ((TextView) findViewById(R.id.text3)).setText(foobar2.getValue());
                });
        foobar2IdStore.put(new Foobar2(1234, "FIN", 100, "Success"));
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
