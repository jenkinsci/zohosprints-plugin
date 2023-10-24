package io.jenkins.plugins.api;

import io.jenkins.plugins.model.FeedStatus;
import io.jenkins.plugins.sprints.ZohoClient;

public final class FeedStatusAPI {
    private static final String FEED_PUSH_API = "/projects/no-$1/feed/status/";

    public String addFeed(FeedStatus feed) throws Exception {
        ZohoClient client = new ZohoClient(FEED_PUSH_API, ZohoClient.METHOD_POST, feed.getProjectNumber())
                .addParameter("name", feed.getFeed());
        client.execute();
        if (client.isSuccessRequest()) {
            return "Feed status successfully added";
        }
        return null;
    }
}
