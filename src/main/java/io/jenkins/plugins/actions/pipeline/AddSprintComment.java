package io.jenkins.plugins.actions.pipeline;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.pipeline.descriptor.PipelineStepDescriptor;
import io.jenkins.plugins.actions.pipeline.executor.PipelineStepExecutor;
import io.jenkins.plugins.actions.pipeline.step.SprintsPipelineStep;
import io.jenkins.plugins.api.SprintAPI;
import io.jenkins.plugins.exception.ZSprintsException;

public class AddSprintComment extends SprintsPipelineStep {
    @DataBoundConstructor
    public AddSprintComment(String projectNumber, String sprintNumber, String note) {
        super(projectNumber, sprintNumber, note);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        setEnvironmentVariableReplacer(context);
        Function<String, String> executor = (key) -> {
            try {
                return SprintAPI.getInstance().addComment(getForm());
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
            return "addSprintComment";
        }

        @Override
        public String getDisplayName() {
            return Messages.add_sprint_comment();
        }
    }

}
