package io.jenkins.plugins.jenkinswork.statusparameter;

import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.FormValidation;
import io.jenkins.plugins.sprints.SprintsWebHook;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.List;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class SprintsParameter extends ParameterDefinition {

    private String sprintsProjectKey;
    private String name;

    /**
     *
     * @return Name of the Param
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param fromname name of the param
     */
    public void setName(final String fromname) {
        this.name = fromname;
    }

    /**
     *
     * @return project key
     */
    public String getSprintsProjectKey() {
        return sprintsProjectKey;
    }

    /**
     *
     * @param fromname Name of the param
     * @param fromdescription description of the param
     * @param fromsprintsProjectKey value of the Param {project key prefix}
     */
    @DataBoundConstructor
    public SprintsParameter(final String fromname, final String fromdescription, final String fromsprintsProjectKey) {
        super(fromname, fromdescription);
        this.sprintsProjectKey = fromsprintsProjectKey;
        this.name = fromname;
    }

    /**
     *
     * @param command command from cli
     * @param value value for the param
     * @return StatusParamValue Class instance
     * @throws IOException throws when read/write error occurs at run time
     * @throws InterruptedException Clears interrupted status of thread
     */
    @Override
    public ParameterValue createValue(CLICommand command, String value) throws IOException, InterruptedException {
        return new StatusParamValue(getName(), value);
    }
    /**
     *
     * @param req request Object
     * @param jo Contains value and key
     * @return ParameterValue
     */
    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StatusParamValue st = req.bindJSON(StatusParamValue.class, jo);
        return st;
    }

    /**
     *
     * @param req request Object
     * @return ParameterValue instance
     */
    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String[] values = req.getParameterValues(getName());
        if (values == null || values.length != 1) {
            return null;
        }

        return new StatusParamValue(getName(), values[0]);
    }

    /**
     *
     * @return List Of Project Object
     * @throws Exception To throw error when error ocured in run time
     */
    public List<String> getStatus() throws Exception {
        if (!StringUtils.isAlphanumeric(sprintsProjectKey)) {
            throw new IllegalArgumentException("projectkey should be ");
        }
        return SprintsWebHook.getInstanceForFetchStatus(sprintsProjectKey).fetchStatus();
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    //@Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        /**
         *
         * @return Display Name of the Action
         */
        @Override
        public String getDisplayName() {
            return "[Sprints] Status Parameter";
        }

        /**
         *
         * @param value projectkey value
         * @return if projectKey is not empty and not alpha numeric then Ok else Error
         */
        public FormValidation doCheckSprintsProjectKey(@QueryParameter final String value) {
            if (StringUtils.isAlphanumeric(value)) {
                return FormValidation.ok();
            }
            return FormValidation.error("Only Alpha Numaric allowed");
        }
    }

}
