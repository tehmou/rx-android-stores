package com.tehmou.rxandroidstores.example.pojo;

/**
 * Created by ttuo on 11/06/15.
 */
public class CountryIdKey {
    private final String country;
    private final int id;

    public CountryIdKey(String country, int id) {
        this.id = id;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }
}
