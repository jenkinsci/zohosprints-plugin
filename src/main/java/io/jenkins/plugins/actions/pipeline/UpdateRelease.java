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

public class UpdateRelease extends ReleasePipelineStep {
    @DataBoundConstructor
    public UpdateRelease(String projectNumber, String releaseNumber, String name, String goal, String stage,
            String startdate, String enddate, String customFields) {
        super(projectNumber, releaseNumber, name, null, goal, stage, startdate, enddate, customFields);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        setEnvironmentVariableReplacer(context);
        Function<String, String> executor = (key) -> {
            try {
                return ReleaseAPI.getInstance().update(getForm());
            } catch (Exception e) {
                throw new ZSprintsException(e.getMessage());
            }

        };
        return new PipelineStepExecutor(executor, context);
    }

    @Extension
    public static final class DescriptorImpl extends PipelineStepDescriptor {
        @Override
        public String getFunctionName() {
            return "sprintsUpdateRelease";
        }

        @Override
        public String getDisplayName() {
            return Messages.release_update();
        }
    }
}
