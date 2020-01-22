package io.jenkins.plugins.jenkinswork.statusparameter;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.Run;
import hudson.util.VariableResolver;
import org.kohsuke.stapler.DataBoundConstructor;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class StatusParamValue extends ParameterValue {

    private String status;

    /**
     *
     * @param name Name of the Param
     * @param status value of the Param
     */
    @DataBoundConstructor
    public StatusParamValue(final String name, final String status) {
        super(name);
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    /**
     *
     * @param run Run Object of the build
     * @param env Environment Variable
     */
    @Override
    public void buildEnvironment(final Run<?, ?> run, EnvVars env) {
        env.put(getName(), getStatus());
    }

    /**
     *
     * @param build AbstarctBuild Object Of build
     * @return Variableresolver instance
     */
    @Override
    public VariableResolver<String> createVariableResolver(final AbstractBuild<?, ?> build) {
        //return name1 -> StatusParamValue.this.name.equals(name) ? getStatus() : null;
        return name1 -> StatusParamValue.this.name != null ? getStatus() : null;
    }

    /**
     *
     * @param status Status Value of the param
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return Status Value of the param
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @return Status Value of the param
     */
    @Override
    public Object getValue() {
        return getStatus();
    }

    /**
     *
     * @return Convertion of variable to String
     */
    @Override
    public String toString() {
        return "(SprintsStatusParamValue) " + getName() + "='" + status + "'";
    }
}
