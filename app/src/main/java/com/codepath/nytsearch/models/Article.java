package com.codepath.nytsearch.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;


@Parcel
public class Article {
    String webUrl;
    String snippet;
    String leadParagraph;
    String newsDesk;
    Headline headline;
    List<ArticleImage> multimedia;

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

}
