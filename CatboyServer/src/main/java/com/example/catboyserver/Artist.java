/**
 * Artist class is used to store the data of the artist.
 * */

package com.example.catboyserver;

public class Artist {
    private String url;
    private String artist;
    private String artist_url;
    private String source_url;
    private String error;
    private long latency;

    public Artist(String url, String artist, String artist_url, String source_url, String error, long latency) {
        this.url = url;
        this.artist = artist;
        this.artist_url = artist_url;
        this.source_url = source_url;
        this.error = error;
        this.latency = latency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist_url() {
        return artist_url;
    }

    public void setArtist_url(String artist_url) {
        this.artist_url = artist_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }
}
