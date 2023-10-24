package io.jenkins.plugins.actions.pipeline.step;

import java.io.IOException;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.model.BaseModel;

public abstract class PipelineStep extends Step {
    private BaseModel form;

    public BaseModel getForm() {
        return form;
    }

    public String getProjectNumber() {
        return form.getProjectNumber();
    }

    public String getItemNumber() {
        return form.getItemNumber();
    }

    public String getSprintNumber() {
        return form.getSprintNumber();
    }

    public String getReleaseNumber() {
        return form.getReleaseNumber();
    }

    public PipelineStep(BaseModel form) {
        this.form = form;
    }

    protected void setEnvironmentVariableReplacer(StepContext context) throws IOException, InterruptedException {
        TaskListener listener = context.get(TaskListener.class);
        Run<?, ?> run = context.get(Run.class);
        form.setEnviroinmentVaribaleReplacer((key) -> {
            try {
                return run.getEnvironment(listener).expand(key);
            } catch (IOException | InterruptedException e) {
                return key;
            }
        });
    }

}
