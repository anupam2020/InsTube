package com.sbdev.insta_youtube_video_downloader;

public class YoutubeModel {

    String ytURL,quality;

    public YoutubeModel(String ytURL, String quality) {
        this.ytURL = ytURL;
        this.quality = quality;
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
}
