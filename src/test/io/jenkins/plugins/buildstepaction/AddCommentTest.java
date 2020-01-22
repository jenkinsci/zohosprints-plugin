package io.jenkins.plugins.buildstepaction;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import io.jenkins.plugins.jenkinswork.buildstepaction.AddComment;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class AddCommentTest {

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
      AddComment comment = new AddComment(null,"hai","false");
      comment.perform(build,launcher,listener);
      assertTrue("Comment not added", build.getResult() == Result.FAILURE);
   }
   @Test(expected = NullPointerException.class)
   public void checkCommentIsNull() throws IOException, InterruptedException {
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
      AddComment comment = new AddComment("P1#I1",null,"false");
      comment.perform(build,launcher,listener);
      assertTrue("Comment not added", build.getResult() == Result.FAILURE);
   }
}
