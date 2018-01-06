package com.randomrobotics.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Recipe Step
 */

public class Step implements Parcelable {
    private String shortDescription;
    private String description;
    private String thumbnailURL;
    private String videoURL;

    /**
     * Get the short description of the {@link Step}
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Get the full description of the {@link Step}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the URL of the {@link Step} thumbnail image
     */
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * Get the URL of the {@link Step} video
     */
    public String getVidedoURL() {
        return videoURL;
    }

    /**
     * Create a new {@link Recipe} {@link Step}
     *
     * @param shortDescription Short description
     * @param description      Full description
     * @param thumbnailURL     URL of thumbnail image
     * @param videoURL         URL of video
     */
    public Step(String shortDescription, String description, String thumbnailURL, String videoURL) {
        this.shortDescription = shortDescription;
        this.description = description;
        this.thumbnailURL = thumbnailURL;
        this.videoURL = videoURL;
    }

    /**
     * Generate a string that describes the {@link Step} and its properties
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s", shortDescription)).append("\r\n");
        if (!videoURL.isEmpty())
            sb.append(String.format("Video: %s", videoURL)).append("\r\n");
        if (!thumbnailURL.isEmpty())
            sb.append(String.format("Thumbnail: %s", thumbnailURL)).append("\r\n");
        sb.append(String.format("%s", description)).append("\r\n");
        return sb.toString();
    }

    /**
     * Returns TRUE if the {@link Step} has a descriptive video
     */
    public boolean hasVideo() {
        return (!(videoURL.isEmpty() || videoURL.equals("")));
    }

    /**
     * Returns TRUE if the {@link Step} has a thumbnail image
     */
    public boolean hasThumbnail() {
        return (!(thumbnailURL.isEmpty() || thumbnailURL.equals("")));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(thumbnailURL);
        parcel.writeString(videoURL);
    }

    /**
     * Create a new {@link Recipe} {@link Step} using the saved parcel
     */
    private Step(Parcel in) {
        shortDescription = in.readString();
        description = in.readString();
        thumbnailURL = in.readString();
        videoURL = in.readString();
    }

    static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
