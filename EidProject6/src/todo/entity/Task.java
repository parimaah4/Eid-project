package todo.entity;

import db.Entity;
import db.Trackable;

import java.util.Date;

public class Task extends Entity implements Trackable {
    public static final int TASK_ENTITY_CODE = 20;
    public enum Status { NotStarted, InProgress, Completed }

    public String title;
    public String description;
    public Date dueDate;
    public Status status;
    private Date creationDate;
    private Date lastModificationDate;

    public Task(String title, String description, Date dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = Status.NotStarted;
    }

    @Override
    public Task copy() {
        Task copyTask = new Task(this.title, this.description, this.dueDate != null ? new Date(this.dueDate.getTime()) : null);
        copyTask.id = this.id;
        copyTask.status = this.status;
        if (this.creationDate != null) {
            copyTask.creationDate = new Date(this.creationDate.getTime());
        }
        if (this.lastModificationDate != null) {
            copyTask.lastModificationDate = new Date(this.lastModificationDate.getTime());
        }
        return copyTask;
    }

    @Override
    public int getEntityCode() {
        return TASK_ENTITY_CODE;
    }

    @Override
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setLastModificationDate(Date date) {
        this.lastModificationDate = date;
    }

    @Override
    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }
}