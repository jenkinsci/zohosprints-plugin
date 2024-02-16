package io.jenkins.plugins.actions.pipeline.step;

import java.io.IOException;
import java.util.function.Function;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.BaseModel;

public abstract class PipelineStep extends Step {
    private BaseModel form;

    public BaseModel getForm() {
        return form;
    }

    public String getProjectNumber() {
        return form.getProjectNumber();
    }

    public PipelineStep(BaseModel form) {
        this.form = form;
    }

    public abstract StepExecution execute(StepContext context, Function<String, String> replacer)
            throws Exception;

    @Override
    public final StepExecution start(StepContext context) throws Exception {
        TaskListener listener = context.get(TaskListener.class);
        Run<?, ?> run = context.get(Run.class);
        return execute(context, (key) -> {
            try {
                if (!key.startsWith("$")) {
                    return key;
                }
                return run.getEnvironment(listener).expand(key);
            } catch (IOException | InterruptedException e) {
                throw new ZSprintsException(e.getMessage());
            }
        });
    }

}
