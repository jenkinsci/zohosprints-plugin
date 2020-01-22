package io.jenkins.plugins.postbuild;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import io.jenkins.plugins.jenkinswork.buildstepaction.UpdatePriority;
import io.jenkins.plugins.jenkinswork.postbuild.CreateSprintsItem;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateSprintItemTest {
    @Test(expected = NullPointerException.class)
    public void checkPrefixIsNull() throws IOException, InterruptedException {
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
        CreateSprintsItem createSprintsItem = new CreateSprintsItem("test",null,"test description","Bug",null,"false");
        createSprintsItem.perform(build,launcher,listener);
        assertTrue("Comment not added", build.getResult() == Result.FAILURE);
    }

    @Test(expected = NullPointerException.class)
    public void checkItemIsNull() throws IOException, InterruptedException {
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
        CreateSprintsItem createSprintsItem = new CreateSprintsItem(null,"P1","test description","Bug",null,"false");
        createSprintsItem.perform(build,launcher,listener);
        assertTrue("Comment not added", build.getResult() == Result.FAILURE);
    }
    @Test(expected = NullPointerException.class)
    public void checkItemDescriptionIsNull() throws IOException, InterruptedException {
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
        CreateSprintsItem createSprintsItem = new CreateSprintsItem(null,"P1",null,"Bug",null,"false");
        createSprintsItem.perform(build,launcher,listener);
        assertTrue("Comment not added", build.getResult() == Result.FAILURE);
    }
    @Test(expected = NullPointerException.class)
    public void checkItemTypeIsNull() throws IOException, InterruptedException {
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
        CreateSprintsItem createSprintsItem = new CreateSprintsItem(null,"P1","test description",null,null,"false");
        createSprintsItem.perform(build,launcher,listener);
        assertTrue("Comment not added", build.getResult() == Result.FAILURE);
    }
}
