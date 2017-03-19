package com.codepath.nytsearch.models;

import org.parceler.Parcel;


@Parcel
public class ResponseMeta {
    int hits;
    int time;
    int offset;


    public int getHits() {
        return hits;
    }

    public int getTime() {
        return time;
    }

    public int getOffset() {
        return offset;
    }
}
