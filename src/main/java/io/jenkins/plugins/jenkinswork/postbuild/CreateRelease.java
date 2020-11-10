package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.*;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNumeric;

public class CreateRelease extends Recorder implements MatrixAggregatable {
    private static final Logger LOGGER = Logger.getLogger(CreateRelease.class.getName());
    private String prefix = null, itemPrefix = null, releaseName = null, description = null, stage = null, owner = null, period = null;
    public String getPrefix() {
        return prefix;
    }

    public String getItemPrefix() {
        return itemPrefix;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getDescription() {
        return description;
    }

    public String getStage() {
        return stage;
    }

    public String getOwner() {
        return owner;
    }

    public String getPeriod() {
        return period;
    }

    @DataBoundConstructor
    public CreateRelease(String prefix, String itemPrefix, String releaseName, String description, String stage, String owner, String period) {
        this.prefix = prefix;
        this.itemPrefix = itemPrefix;
        this.releaseName = releaseName;
        this.description = description;
        this.stage = stage;
        this.owner = owner;
        this.period = period;
    }
    //This method will run this action only in Matrix parent job
    public MatrixAggregator createAggregator(MatrixBuild matrixbuild,
                                             Launcher launcher, BuildListener buildlistener) {
        return new MatrixAggregator(matrixbuild, launcher, buildlistener) {
            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return CreateRelease.this._perform(this.build, this.launcher, this.listener);
            }

            @Override
            public boolean startBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return true;
            }
        };
    }
    /**
     *
     * @return Monitoring Service for BuildStep
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if(build instanceof MatrixRun) {
            return true;
        }
        return _perform(build,launcher,listener);
    }
    private boolean _perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return Release.getInstanceForCreate(build, listener, prefix, itemPrefix, releaseName, stage, description, owner, period).create();
    }
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Prefix valid check
         * @param prefix In which Sprint Item comment to be added
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {
            if(isEmpty(prefix)) {
                return FormValidation.validateRequired(prefix);
            } else if (prefix.matches(Util.PROJECT_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.validateRequired(prefix);
            //return FormValidation.error(Messages.prefix_message("Project"));
        }

        public FormValidation doCheckReleaseName(@QueryParameter final String releaseName) {
            if(!releaseName.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.validateRequired(releaseName);
            //return FormValidation.error(Messages.item_name_message("Release"));
        }
        public FormValidation doCheckReleasePrefix(@QueryParameter final String releasePrefix) {
            if(isEmpty(releasePrefix)) {
                return FormValidation.validateRequired(releasePrefix);
            } else if (releasePrefix.matches(Util.RELEASE_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.validateRequired(releasePrefix);
            //return FormValidation.error(Messages.prefix_message("Release"));
        }

        public FormValidation doCheckItemPrefix(@QueryParameter final String itemPrefix) {
            if(isEmpty(itemPrefix)) {
                return FormValidation.ok();
            }
            else if (itemPrefix.matches(Util.ITEM_REGEX)) {
                return FormValidation.ok();
            }
            //return FormValidation.validateRequired(itemPrefix);
            return FormValidation.error(Messages.prefix_message("Item"));
        }

        public FormValidation doCheckStage(@QueryParameter final String stage) {
            if (!isEmpty(stage)) {
                return FormValidation.ok();
            }
            return FormValidation.validateRequired(stage);
            //return FormValidation.error(Messages.item_name_message("Stage"));
        }

        public FormValidation doCheckDescription(@QueryParameter final String description) {
            if (!isEmpty(description)) {
                return FormValidation.ok();
            }
            return FormValidation.validateRequired(description);
            //return FormValidation.error(Messages.description_message());
        }

        public FormValidation doCheckOwner(@QueryParameter final String owner) {
            boolean isValid = false;
            if(isEmpty(owner)){
                return FormValidation.validateRequired(owner);
            }
            if(!isEmpty(owner) && owner.contains(",")) {
                String[] mails = owner.split(",");
                for(int ms = 0; ms < mails.length; ms++) {
                    if(mails[ms].matches(Util.MAIL_REGEX)){
                        isValid = true;
                    } else {
                        isValid = false;
                    }
                }
            } else if(!isEmpty(owner) && owner.matches(Util.MAIL_REGEX)) {
                isValid = true;
            }
            if (isValid) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.mail_message());
        }
        public FormValidation doCheckPeriod(@QueryParameter final String period) {
            if(isEmpty(period)) {
                return FormValidation.validateRequired(period);
            }
            if (!isNumeric(period)) {
                return FormValidation.error("Release Time should be a Number");
            } else if(Integer.parseInt(period) > 3) {
                return FormValidation.ok();
            }
            return FormValidation.error("Release Time should be a minimum of three days");
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.release_create();
        }
    }
}
