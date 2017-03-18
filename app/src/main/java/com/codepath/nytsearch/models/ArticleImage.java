package com.codepath.nytsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by usarfraz on 3/17/17.
 */

public class ArticleImage implements Parcelable{
    int width;
    int height;
    String url;
    String subtype;

    public ArticleImage() {

    }

    protected ArticleImage(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
        subtype = in.readString();
    }

    public static final Creator<ArticleImage> CREATOR = new Creator<ArticleImage>() {
        @Override
        public ArticleImage createFromParcel(Parcel in) {
            return new ArticleImage(in);
        }

        @Override
        public ArticleImage[] newArray(int size) {
            return new ArticleImage[size];
        }
    };

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return String.format("%s%s", "http://www.nytimes.com/", url);
    }

    public String getSubtype() {
        return subtype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
        dest.writeString(subtype);
    }
}
