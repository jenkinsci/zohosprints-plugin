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

public class CreateSprint extends SprintsPipelineStep {

    @DataBoundConstructor
    public CreateSprint(String projectNumber, String name, String description, String scrummaster, String users,
            String duration, String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, description, scrummaster, users, duration, startdate, enddate, customFields);
    }

    @Override
    public StepExecution execute(StepContext context, Function<String, String> replacer)
            throws Exception {
        Function<String, String> executor = (key) -> {
            try {
                return SprintAPI.getInstance(replacer)
                        .create(getForm());
            } catch (Exception e) {
                throw new ZSprintsException(e.getMessage(), e);
            }

        };
        return new PipelineStepExecutor(executor, context);
    }

    @Extension
    public static final class DescriptorImpl extends PipelineStepDescriptor {
        @Override
        public String getFunctionName() {
            return "createSprints";
        }

        @Override
        public String getDisplayName() {
            return Messages.sprint_create();
        }
    }

}
