package io.jenkins.plugins.actions.pipeline;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.pipeline.descriptor.PipelineStepDescriptor;
import io.jenkins.plugins.actions.pipeline.executor.PipelineStepExecutor;
import io.jenkins.plugins.actions.pipeline.step.PipelineStep;
import io.jenkins.plugins.api.SprintAPI;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.BaseModel;
import io.jenkins.plugins.model.Sprint;

public class CompleteSprint extends PipelineStep {
    @DataBoundConstructor
    public CompleteSprint(String projectNumber, String sprintNumber) {
        super(Sprint.getInstance(projectNumber, sprintNumber));
    }

    public Sprint getForm() {
        return (Sprint) super.getForm();
    }

    public StepExecution start(StepContext context) throws Exception {
        setEnvironmentVariableReplacer(context);
        Function<String, String> executor = (key) -> {
            try {
                return SprintAPI.getInstance().complete(getForm());
            } catch (Exception e) {
                throw new ZSprintsException(e.getMessage());
            }

        };
        return new PipelineStepExecutor(executor, context);
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends PipelineStepDescriptor {
        @Override
        public String getFunctionName() {
            return "CompleteSprint";
        }

        @Override
        public String getDisplayName() {
            return Messages.update_sprint_complete();
        }
    }

    public static class CompleteSprintExecutor extends PipelineStepExecutor {
        protected CompleteSprintExecutor(BaseModel form, StepContext context) {
            super(form, context);
        }

        protected String execute() throws Exception {
            return SprintAPI.getInstance().complete((Sprint) getForm());
        }
    }

}
