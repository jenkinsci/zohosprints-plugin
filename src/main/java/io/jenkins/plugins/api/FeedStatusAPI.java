package io.jenkins.plugins.api;

import java.util.function.Function;

import io.jenkins.plugins.model.FeedStatus;
import io.jenkins.plugins.sprints.ZohoClient;

public final class FeedStatusAPI {
    private static final String FEED_PUSH_API = "/projects/no-$1/feed/status/";

    public String addFeed(FeedStatus feed, Function<String, String> replacer) throws Exception {
        new ZohoClient.Builder(FEED_PUSH_API, ZohoClient.METHOD_POST, replacer, feed.getProjectNumber())
                .addParameter("name", feed.getFeed())
                .build()
                .execute();
        return "Feed status successfully added";
    }
}
