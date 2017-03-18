package com.codepath.nytsearch.models;

import org.parceler.Parcel;

@Parcel
public class ArticleImage {
    int width;
    int height;
    String url;
    String subtype;

    public ArticleImage() {

    }

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

}
