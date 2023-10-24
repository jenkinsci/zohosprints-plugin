package io.jenkins.plugins.actions.postbuild.builder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.model.BaseModel;

public abstract class PostBuild extends Recorder {
    private static final Logger LOGGER = Logger.getLogger(PostBuild.class.getName());
    private BaseModel form;

    public BaseModel getForm() {
        return form;
    }

    public String getProjectNumber() {
        return form.getProjectNumber();
    }

    public String getItemNumber() {
        return form.getItemNumber();
    }

    public String getSprintNumber() {
        return form.getSprintNumber();
    }

    public String getReleaseNumber() {
        return form.getReleaseNumber();
    }

    public PostBuild(BaseModel form) {
        this.form = form;
    }

    public abstract String perform() throws Exception;

    private final boolean perform(AbstractBuild<?, ?> build, BuildListener listener) {
        try {
            form.setEnviroinmentVaribaleReplacer((key) -> {
                try {
                    return build.getEnvironment(listener).expand(key);
                } catch (IOException | InterruptedException e) {
                    return key;
                }
            });
            String message = perform();
            listener.getLogger().println("[Zoho Sprints] " + message);
            form.setEnviroinmentVaribaleReplacer(null);
            return true;
        } catch (Exception e) {
            listener.error(e.getMessage());
            LOGGER.log(Level.WARNING, "", e);
        }
        return false;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        return perform(build, listener);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public PostBuildDescriptor getDescriptor() {
        return (PostBuildDescriptor) super.getDescriptor();
    }

}
