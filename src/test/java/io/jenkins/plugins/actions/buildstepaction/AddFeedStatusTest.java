package io.jenkins.plugins.actions.buildstepaction;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import hudson.model.AbstractBuild;
import hudson.model.Result;

public class AddFeedStatusTest {
    @Test(expected = NullPointerException.class)
    public void perform() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        AddFeedStatus statusAction = new AddFeedStatus("1", "test");
        statusAction.perform(null);
        assertTrue("Unable to add Feed status.", build.getResult() == Result.FAILURE);
    }
}
