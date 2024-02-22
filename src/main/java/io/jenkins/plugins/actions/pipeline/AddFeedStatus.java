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
import io.jenkins.plugins.api.FeedStatusAPI;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.FeedStatus;

public class AddFeedStatus extends PipelineStep {
    @DataBoundConstructor
    public AddFeedStatus(String projectNumber, String feed) {
        super(FeedStatus.getInstance(projectNumber, feed));
    }

    public String getFeed() {
        return getForm().getFeed();
    }

    public FeedStatus getForm() {
        return (FeedStatus) super.getForm();
    }

    @Override
    public StepExecution execute(StepContext context, Function<String, String> replacer)
            throws Exception {
        Function<String, String> executor = (key) -> {
            try {
                return new FeedStatusAPI().addFeed((FeedStatus) getForm(), replacer);
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
            return "sprintsAddFeedStatus";
        }

        @Override
        public String getDisplayName() {
            return Messages.add_feed_status();
        }
    }
}
