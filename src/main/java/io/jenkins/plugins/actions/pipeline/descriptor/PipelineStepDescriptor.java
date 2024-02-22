package io.jenkins.plugins.actions.pipeline.descriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import hudson.model.Run;
import hudson.model.TaskListener;

public abstract class PipelineStepDescriptor extends StepDescriptor {
    @Override
    public Set<? extends Class<?>> getRequiredContext() {
        Set<Class<?>> context = new HashSet<>();
        Collections.addAll(context, Run.class, TaskListener.class);
        return Collections.unmodifiableSet(context);
    }
}
