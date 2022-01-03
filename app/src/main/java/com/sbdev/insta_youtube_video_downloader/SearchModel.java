package com.sbdev.insta_youtube_video_downloader;

public class SearchModel {

    String mainImgURL, circleImgURL;

    String duration,title,author,views,daysAgo;

    String vidID;

    public SearchModel(String mainImgURL, String circleImgURL, String duration, String title, String author, String views, String daysAgo, String vidID) {
        this.mainImgURL = mainImgURL;
        this.circleImgURL = circleImgURL;
        this.duration = duration;
        this.title = title;
        this.author = author;
        this.views = views;
        this.daysAgo = daysAgo;
        this.vidID=vidID;
    }

    public String getMainImgURL() {
        return mainImgURL;
    }

    public void setMainImgURL(String mainImgURL) {
        this.mainImgURL = mainImgURL;
    }

    public String getCircleImgURL() {
        return circleImgURL;
    }

    public void setCircleImgURL(String circleImgURL) {
        this.circleImgURL = circleImgURL;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDaysAgo() {
        return daysAgo;
    }

    public void setDaysAgo(String daysAgo) {
        this.daysAgo = daysAgo;
    }

    public String getVidLink() {
        return vidID;
    }

    public void setVidLink(String vidLink) {
        this.vidID = vidLink;
    }
}
