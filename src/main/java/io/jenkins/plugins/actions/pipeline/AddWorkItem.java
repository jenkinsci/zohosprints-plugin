package io.jenkins.plugins.actions.pipeline;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.Util;
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
    public StepExecution start(StepContext context) throws Exception {
        setEnvironmentVariableReplacer(context);
        Function<String, String> executor = (key) -> {
            try {
                return WorkItemAPI.getInstance().addItem(getForm());
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
            return "sprintsAddWorkItem";
        }

        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

        public FormValidation doCheckName(@QueryParameter final String name) {
            return Util.validateRequired(name);
        }

        public FormValidation doCheckStatus(@QueryParameter final String status) {
            return Util.validateRequired(status);
        }

        public FormValidation doCheckType(@QueryParameter final String type) {
            return Util.validateRequired(type);
        }

        public FormValidation doCheckPriority(@QueryParameter final String priority) {
            return Util.validateRequired(priority);
        }

    }
}
