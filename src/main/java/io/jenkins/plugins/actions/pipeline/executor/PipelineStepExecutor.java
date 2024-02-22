package io.jenkins.plugins.actions.pipeline.executor;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import hudson.model.TaskListener;

public class PipelineStepExecutor extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 8003836385443570257L;

    transient Function<String, String> executor;

    public PipelineStepExecutor(Function<String, String> executor, StepContext context) {
        super(context);
        this.executor = executor;
    }

    @Override
    protected Void run() throws Exception {
        TaskListener listener = getContext().get(TaskListener.class);
        try {
            String message = executor.apply(null);
            listener.getLogger().println("[Zoho Sprints] " + message);
        } catch (Exception e) {
            listener.error(e.getMessage());
        }
        return null;
    }

}
