package todo.service;

import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

import java.util.ArrayList;

public class StepService {
    public static void saveStep(int taskRef, String title) throws InvalidEntityException {
        Step step = new Step(title, taskRef);
        Database.add(step);
        System.out.println("Step saved successfully.");
        System.out.println("ID: " + step.id);
        System.out.println("Creation Date: " + ((Task) Database.get(taskRef)).getCreationDate());
    }

    public static void updateStepField(int stepId, String field, String newValue) throws EntityNotFoundException, InvalidEntityException {
        Step step = (Step) Database.get(stepId);
        String oldValue = "";
        switch (field.toLowerCase()) {
            case "title":
                oldValue = step.title;
                step.title = newValue;
                break;
            case "status":
                oldValue = step.status.toString();
                step.status = Step.Status.valueOf(newValue);
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + field);
        }
        Database.update(step);
        updateTaskStatusOnStepChange(step.taskRef);
        System.out.println("Successfully updated the step.");
        System.out.println("Field: " + field);
        System.out.println("Old Value: " + oldValue);
        System.out.println("New Value: " + newValue);
        System.out.println("Modification Date: " + ((Task) Database.get(step.taskRef)).getLastModificationDate());
    }

    private static void updateTaskStatusOnStepChange(int taskId) throws EntityNotFoundException, InvalidEntityException {
        Task task = (Task) Database.get(taskId);
        ArrayList<Entity> steps = Database.getAll(Step.STEP_ENTITY_CODE);
        boolean allCompleted = true;
        boolean anyCompleted = false;

        for (Entity e : steps) {
            Step step = (Step) e;
            if (step.taskRef == taskId) {
                if (step.status != Step.Status.Completed) {
                    allCompleted = false;
                } else {
                    anyCompleted = true;
                }
            }
        }

        if (allCompleted && steps.size() > 0) {
            task.status = Task.Status.Completed;
        } else if (anyCompleted && task.status == Task.Status.NotStarted) {
            task.status = Task.Status.InProgress;
        }
        Database.update(task);
    }
}