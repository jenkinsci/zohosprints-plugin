package io.jenkins.plugins.actions.buildstepaction.builder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
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

    public String getItemNumber() {
        return form.getItemNumber();
    }

    public String getSprintNumber() {
        return form.getSprintNumber();
    }

    public String getReleaseNumber() {
        return form.getReleaseNumber();
    }

    public BuildStep(BaseModel form) {
        this.form = form;
    }

    public abstract String perform() throws Exception;

    @Override
    public final boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
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
    public BuildStepDescriptorImpl getDescriptor() {
        return (BuildStepDescriptorImpl) super.getDescriptor();
    }
}
