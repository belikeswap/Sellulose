package com.sellulose.app;

/**
 * Created by swapn on 23-Jan-18.
 */

public class SalesforceTask {

    private String taskName;
    private String taskContact;
    private String dueDate;

    //Default Constructor
    public SalesforceTask() {
    }

    public SalesforceTask(String taskName, String taskContact, String dueDate) {

        this.taskName = taskName;
        this.taskContact = taskContact;
        this.dueDate = dueDate;

    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskContact() {
        return taskContact;
    }

    public void setTaskContact(String taskContact) {
        this.taskContact = taskContact;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
