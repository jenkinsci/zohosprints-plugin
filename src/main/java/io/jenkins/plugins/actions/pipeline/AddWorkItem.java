package io.jenkins.plugins.actions.pipeline;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.pipeline.descriptor.PipelineStepDescriptor;
import io.jenkins.plugins.actions.pipeline.executor.PipelineStepExecutor;
import io.jenkins.plugins.actions.pipeline.step.ItemPipelineStep;
import io.jenkins.plugins.api.WorkItemAPI;
import io.jenkins.plugins.exception.ZSprintsException;

public class AddWorkItem extends ItemPipelineStep {

    @DataBoundConstructor
    public AddWorkItem(String projectNumber, String sprintNumber, String name,
            String description, String status, String type, String priority,
            String duration, String assignee, String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, null, name, description, status, type, priority, duration, assignee,
                startdate, enddate,
                customFields);
    }

    @Override
    public StepExecution execute(StepContext context, Function<String, String> replacer)
            throws Exception {
        Function<String, String> executor = (key) -> {
            try {
                return WorkItemAPI.getInstance(replacer)
                        .addItem(getForm());
            } catch (Exception e) {
                throw new ZSprintsException(e.getMessage(), e);
            }

        };
        return new PipelineStepExecutor(executor, context);
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends PipelineStepDescriptor {

        @Override
        public String getFunctionName() {
            return "sprintsAddWorkItem";
        }

        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

    }
}
