package com.tehmou.rxandroidstores.example.pojo;

/**
 * Created by ttuo on 11/06/15.
 */
public class Record {
    private final int id;
    private final String country;
    private final int userId;
    private final String value;

    public Record(int id,
                  String country,
                  int userId,
                  String value) {
        this.id = id;
        this.country = country;
        this.userId = userId;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public int getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
    }
}
