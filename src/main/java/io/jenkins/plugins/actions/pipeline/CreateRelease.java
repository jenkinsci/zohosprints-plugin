package io.jenkins.plugins.actions.pipeline;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.pipeline.descriptor.PipelineStepDescriptor;
import io.jenkins.plugins.actions.pipeline.executor.PipelineStepExecutor;
import io.jenkins.plugins.actions.pipeline.step.ReleasePipelineStep;
import io.jenkins.plugins.api.ReleaseAPI;
import io.jenkins.plugins.exception.ZSprintsException;

public class CreateRelease extends ReleasePipelineStep {

    @DataBoundConstructor
    public CreateRelease(String projectNumber, String name, String goal, String stage, String owners,
            String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, owners, goal, stage, startdate, enddate, customFields);
    }

    @Override
    public StepExecution execute(StepContext context, Function<String, String> replacer)
            throws Exception {
        Function<String, String> executor = (key) -> {
            try {
                return ReleaseAPI.getInstance(replacer)
                        .create(getForm());
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
            return "sprintsCreateRelease";
        }

        @Override
        public String getDisplayName() {
            return Messages.release_create();
        }
    }
}
