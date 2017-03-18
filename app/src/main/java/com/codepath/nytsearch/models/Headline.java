package com.codepath.nytsearch.models;

import org.parceler.Parcel;

@Parcel
public class Headline {
    String main;
    String printHeadline;

    public Headline() {

    }

    public String getMain() {
        return main;
    }

    public String getPrintHeadline() {
        return printHeadline;
    }
}
