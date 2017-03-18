package com.codepath.nytsearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Article implements Parcelable {
    String webUrl;
    String snippet;
    String leadParagraph;
    String newsDesk;
    Headline headline;
    List<ArticleImage> multimedia;

    protected Article(Parcel in) {
        webUrl = in.readString();
        snippet = in.readString();
        leadParagraph = in.readString();
        newsDesk = in.readString();
        headline = in.readParcelable(Headline.class.getClassLoader());
        multimedia = new ArrayList<>();
        in.readList(multimedia, ArticleImage.class.getClassLoader());
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getWebUrl() {
        return webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public String getNewsDesk() {
        return newsDesk;
    }

    public Headline getHeadline() {
        return headline;
    }

    public Article() {
        multimedia = new ArrayList<>();

    }

    @Override
    public String toString() {
        return newsDesk + " (" + leadParagraph + ")";
    }

    public List<ArticleImage> getMultimedia() {
        return multimedia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(webUrl);
        dest.writeString(snippet);
        dest.writeString(leadParagraph);
        dest.writeString(newsDesk);
        dest.writeParcelable(headline, flags);
        dest.writeList(multimedia);

    }
}
