package io.jenkins.plugins.actions.postbuild.builder;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.BaseModel;

public abstract class PostBuild extends Recorder {
    protected static final Logger LOGGER = Logger.getLogger(PostBuild.class.getName());
    private BaseModel form;

    public BaseModel getForm() {
        return form;
    }

    public String getProjectNumber() {
        return form.getProjectNumber();
    }

    public PostBuild(BaseModel form) {
        this.form = form;
    }

    public abstract String perform(Function<String, String> replacer) throws Exception;

    private final boolean perform(AbstractBuild<?, ?> build, BuildListener listener) {
        try {
            String message = perform((key) -> {
                try {
                    if (!key.startsWith("$")) {
                        return key;
                    }
                    return build.getEnvironment(listener).expand(key);
                } catch (IOException | InterruptedException e) {
                    throw new ZSprintsException(e.getMessage(), e);
                }
            });
            listener.getLogger().println("[Zoho Sprints] " + message);
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
