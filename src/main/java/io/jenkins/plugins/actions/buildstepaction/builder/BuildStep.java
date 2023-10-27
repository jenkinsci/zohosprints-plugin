package io.jenkins.plugins.actions.buildstepaction.builder;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.BaseModel;

public abstract class BuildStep extends Builder {
    private static final Logger LOGGER = Logger.getLogger(BuildStep.class.getName());
    private BaseModel form;

    public BaseModel getForm() {
        return form;
    }

    public String getProjectNumber() {
        return form.getProjectNumber();
    }

    public BuildStep(BaseModel form) {
        this.form = form;
    }

    public abstract String perform(Function<String, String> replacer) throws Exception;

    @Override
    public final boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
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
    public BuildStepDescriptorImpl getDescriptor() {
        return (BuildStepDescriptorImpl) super.getDescriptor();
    }
}
