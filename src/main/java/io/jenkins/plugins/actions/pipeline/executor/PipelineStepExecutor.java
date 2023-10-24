package io.jenkins.plugins.actions.pipeline.executor;

import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import hudson.model.TaskListener;
import io.jenkins.plugins.model.BaseModel;

public class PipelineStepExecutor extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 8003836385443570257L;
    transient BaseModel form;

    transient Function<String, String> executor;

    public PipelineStepExecutor(BaseModel form, StepContext context) {
        super(context);
        this.form = form;
        this.executor = null;
    }

    public PipelineStepExecutor(Function<String, String> executor, StepContext context) {
        super(context);
        this.executor = executor;
    }

    public BaseModel getForm() {
        return form;
    }

    protected String execute() throws Exception {
        return null;
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
