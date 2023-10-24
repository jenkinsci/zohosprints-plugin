package io.jenkins.plugins.actions.pipeline.descriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.kohsuke.stapler.QueryParameter;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import io.jenkins.plugins.Util;

public abstract class PipelineStepDescriptor extends StepDescriptor {
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
        Set<Class<?>> context = new HashSet<>();
        Collections.addAll(context, Run.class, TaskListener.class);
        return Collections.unmodifiableSet(context);
    }

    public FormValidation doCheckPrefix(@QueryParameter final String prefix) {
        return Util.validateRequired(prefix);
    }

    public FormValidation doCheckNote(@QueryParameter final String note) {
        return Util.validateRequired(note);
    }
}
