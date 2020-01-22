package io.jenkins.plugins.postbuild;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import io.jenkins.plugins.jenkinswork.postbuild.FeedStatus;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeedStatusTest {
    @Test(expected = NullPointerException.class)
    public void checkPrefixisNull() throws IOException, InterruptedException {
        AbstractBuild build = mock(AbstractBuild.class);
        Launcher launcher = mock(Launcher.class);
        BuildListener listener = mock(BuildListener.class);
        EnvVars env = mock(EnvVars.class);
        AbstractProject project = mock(AbstractProject.class);
        PrintStream logger = mock(PrintStream.class);

        when(build.getParent()).thenReturn(project);
        when(build.getProject()).thenReturn(project);
        when(build.getEnvironment(listener)).thenReturn(env);
        when(listener.getLogger()).thenReturn(logger);
        FeedStatus feedStatus = new FeedStatus(null,"hai");
        feedStatus.perform(build,launcher,listener);
        assertTrue("Feed status not added", build.getResult() == Result.FAILURE);
    }
    @Test(expected = NullPointerException.class)
    public void checkFeedStatusNull() throws IOException, InterruptedException {
        AbstractBuild build = mock(AbstractBuild.class);
        Launcher launcher = mock(Launcher.class);
        BuildListener listener = mock(BuildListener.class);
        EnvVars env = mock(EnvVars.class);
        AbstractProject project = mock(AbstractProject.class);
        PrintStream logger = mock(PrintStream.class);

        when(build.getParent()).thenReturn(project);
        when(build.getProject()).thenReturn(project);
        when(build.getEnvironment(listener)).thenReturn(env);
        when(listener.getLogger()).thenReturn(logger);
        FeedStatus feedStatus = new FeedStatus("P1",null);
        feedStatus.perform(build,launcher,listener);
        assertTrue("Feed status not added", build.getResult() == Result.FAILURE);
    }
}
