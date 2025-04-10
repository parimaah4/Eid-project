package todo.service;

import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

import java.util.ArrayList;
import java.util.Date;


public class TaskService {
    public static void setAsCompleted(int taskId) throws EntityNotFoundException, InvalidEntityException {
        Task task = (Task) Database.get(taskId);
        task.status = Task.Status.Completed;
        Database.update(task);
        updateStepsOnTaskCompletion(taskId);
    }

    public static void updateTaskField(int taskId, String field, String newValue) throws EntityNotFoundException, InvalidEntityException {
        Task task = (Task) Database.get(taskId);
        String oldValue = "";
        switch (field.toLowerCase()) {
            case "title":
                oldValue = task.title;
                task.title = newValue;
                break;
            case "description":
                oldValue = task.description;
                task.description = newValue;
                break;
            case "duedate":
                task.dueDate = parseDate(newValue);
                break;
            case "status":
                oldValue = task.status.toString();
                task.status = Task.Status.valueOf(newValue);
                if (task.status == Task.Status.Completed) {
                    updateStepsOnTaskCompletion(taskId);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + field);
        }
        Database.update(task);
        System.out.println("Successfully updated the task.");
        System.out.println("Field: " + field);
        System.out.println("Old Value: " + oldValue);
        System.out.println("New Value: " + newValue);
        System.out.println("Modification Date: " + task.getLastModificationDate());
    }

    private static void updateStepsOnTaskCompletion(int taskId) throws EntityNotFoundException, InvalidEntityException {
        ArrayList<Entity> steps = Database.getAll(Step.STEP_ENTITY_CODE);
        for (Entity e : steps) {
            Step step = (Step) e;
            if (step.taskRef == taskId) {
                step.status = Step.Status.Completed;
                Database.update(step);
            }
        }
    }

    // تغییر از private به public
    public static Date parseDate(String dateStr) {
        try {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }
}