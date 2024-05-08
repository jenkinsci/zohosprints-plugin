package io.jenkins.plugins.model;

public class FeedStatus extends BaseModel {
    private String feed;

    private FeedStatus(String projectNumber, String feed) {
        super(projectNumber);
        this.feed = feed;
    }

    public String getFeed() {
        return feed;
    }

    public static FeedStatus getInstance(String projectNumber, String feed) {
        return new FeedStatus(projectNumber, feed);
    }
}
