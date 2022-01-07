package com.sbdev.insta_youtube_video_downloader;

public class YoutubeMP3Model {

    String imgURL,ytURL,quality,type,title;

    public YoutubeMP3Model(String imgURL, String ytURL, String quality, String type, String title) {
        this.imgURL = imgURL;
        this.ytURL = ytURL;
        this.quality = quality;
        this.type = type;
        this.title=title;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getYtURL() {
        return ytURL;
    }

    public void setYtURL(String ytURL) {
        this.ytURL = ytURL;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getTime() {
        return type;
    }

    public void setTime(String time) {
        this.type = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
