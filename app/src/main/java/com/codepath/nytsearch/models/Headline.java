package com.codepath.nytsearch.models;

import org.parceler.Parcel;

@Parcel
public class Headline {
    String main;


    String name;
    String printHeadline;

    public Headline() {

    }

    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public String getPrintHeadline() {
        return printHeadline;
    }
}
