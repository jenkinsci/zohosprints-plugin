package io.jenkins.plugins.jenkinswork;

import hudson.Extension;
import hudson.model.RootAction;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.util.Util;
import jenkins.model.Jenkins;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
@Extension
public class SprintsRootAction implements RootAction {

    /**
     *
     * @return Root Action IconFileName
     */
    @CheckForNull
    @Override
    public String getIconFileName() {
        return Util.getSprintsIconByAuth();
    }

    /**
     *
     * @return Root Action Dosplay Name
     */
    @CheckForNull
    @Override
    public String getDisplayName() {
        return "Sprints";
    }

    /**
     *
     * @return Root Action url
     */
    @CheckForNull
    @Override
    public String getUrlName() {
            List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().
                                                                getExtensionList(SprintsConnectionConfig.class));
            SprintsConnectionConfig conf = extnList.get(0);
            return conf.getDoamin() == null ? "" : conf.getDoamin();
    }
}
