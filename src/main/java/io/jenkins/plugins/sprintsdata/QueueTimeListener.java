package io.jenkins.plugins.sprintsdata;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.queue.QueueListener;

import java.util.logging.Logger;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
@Extension
public class QueueTimeListener extends QueueListener {
    private Long timeInQueue;
    private String jobName;
    private static final Logger LOGGER = Logger.getLogger(QueueTimeListener.class.getName());
    public QueueTimeListener() {

    }

   /* @Override
    public void onEnterBlocked(Queue.BlockedItem item) {
        if (!(item.task instanceof ExecutorStepExecution.PlaceholderTask)) {
            return;
        }
        Run run = ((ExecutorStepExecution.PlaceholderTask) item.task).run();

        if (run != null) {
            run.addAction(new BuildBlockedAction(System.currentTimeMillis()));
            LOGGER.info(System.currentTimeMillis() + "");
        }
    }

    @Override
    public void onLeaveBlocked(Queue.BlockedItem item) {
        if (!(item.task instanceof ExecutorStepExecution.PlaceholderTask)) {
            return;
        }
        Run run = ((ExecutorStepExecution.PlaceholderTask) item.task).run();

        if (run != null) {
            BuildBlockedAction action = run.getAction(BuildBlockedAction.class);
            if (action != null) {
                action.setTimeReleased(System.currentTimeMillis());
                LOGGER.info(System.currentTimeMillis() + "");
            }
        }
    }*/
    /**
     *
     * @return name of the job
     */
    public String getJobName() {
        return jobName;
    }

    /**
     *
     * @return How long does the job in queue
     */
    public Long getTimeInQueue() {
        return timeInQueue;
    }

    /**
     *
     * @param li
     */
    @Override
    public void onLeft(final Queue.LeftItem li) {
        timeInQueue = System.currentTimeMillis() - li.getInQueueSince(); // Its in milliseconds need to convert
        jobName = li.task.getFullDisplayName();
    }

}