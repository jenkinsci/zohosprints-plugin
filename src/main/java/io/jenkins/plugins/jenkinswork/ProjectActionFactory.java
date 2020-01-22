package io.jenkins.plugins.jenkinswork;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.Collection;

import static java.util.Collections.singleton;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
//@Extension
public class ProjectActionFactory extends TransientProjectActionFactory {
    /**
     *
     * @param target target project Object
     * @return singleton instance of Project Job Action
     */
    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        //return singleton(new ProjectJobAction(target));
        return null;
    }
}
