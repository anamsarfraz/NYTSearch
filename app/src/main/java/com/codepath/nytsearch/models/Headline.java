package com.codepath.nytsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by usarfraz on 3/17/17.
 */

public class Headline implements Parcelable{
    String main;
    String printHeadline;

    public Headline() {

    }

    protected Headline(Parcel in) {
        main = in.readString();
        printHeadline = in.readString();
    }

    public static final Creator<Headline> CREATOR = new Creator<Headline>() {
        @Override
        public Headline createFromParcel(Parcel in) {
            return new Headline(in);
        }

        @Override
        public Headline[] newArray(int size) {
            return new Headline[size];
        }
    };

    public String getMain() {
        return main;
    }

    public String getPrintHeadline() {
        return printHeadline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(main);
        dest.writeString(printHeadline);
    }
}
