package io.jenkins.plugins.sprintsdata;

import hudson.model.InvisibleAction;

public class BuildBlockedAction extends InvisibleAction {

    private long timeStartBlocked;
    private long timeReleased;
    public BuildBlockedAction() {
        this.timeStartBlocked = System.currentTimeMillis();
    }

    public BuildBlockedAction(long timeStartBlocked) {
        this.timeStartBlocked = timeStartBlocked;
    }
    /**
     * Gets the time the item was released.
     *
     * @return the time the item was released
     */
    public long getTimeReleased() {
        return timeReleased;
    }

    /**
     * Sets the time the item was released.
     *
     * @param timeReleased the time the item was released
     */
    public void setTimeReleased(long timeReleased) {
        this.timeReleased = timeReleased;
    }

    /**
     * Gets the amount of time the item spent blocked.
     *
     * @return the amount of time blocked, in milliseconds
     */
    public long getTimeBlocked() {
        return timeReleased - timeStartBlocked;
    }
}
