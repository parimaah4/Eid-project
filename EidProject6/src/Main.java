import db.Database;
import db.Entity;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.StepSerializer;
import todo.entity.Task;
import todo.entity.TaskSerializer;
import todo.service.StepService;
import todo.service.TaskService;
import todo.validator.StepValidator;
import todo.validator.TaskValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // ثبت Validatorها
        Database.registerValidator(Task.TASK_ENTITY_CODE, new TaskValidator());
        Database.registerValidator(Step.STEP_ENTITY_CODE, new StepValidator());

        // ثبت Serializerها
        Database.registerSerializer(Task.TASK_ENTITY_CODE, new TaskSerializer());
        Database.registerSerializer(Step.STEP_ENTITY_CODE, new StepSerializer());

        // بارگیری اطلاعات در ابتدای برنامه
        Database.load();

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim();
            try {
                switch (command.toLowerCase()) {
                    case "add task":
                        handleAddTask();
                        break;
                    case "add step":
                        handleAddStep();
                        break;
                    case "delete":
                        handleDelete();
                        break;
                    case "update task":
                        handleUpdateTask();
                        break;
                    case "update step":
                        handleUpdateStep();
                        break;
                    case "get task-by-id":
                        handleGetTaskById();
                        break;
                    case "get all-tasks":
                        handleGetAllTasks();
                        break;
                    case "get incomplete-tasks":
                        handleGetIncompleteTasks();
                        break;
                    case "save":
                        Database.save();
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Unknown command: " + command);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void handleAddTask() throws InvalidEntityException {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Due date (yyyy-MM-dd): ");
        String dueDateStr = scanner.nextLine();
        Task task = new Task(title, description, TaskService.parseDate(dueDateStr));
        Database.add(task);
        System.out.println("Task saved successfully.");
        System.out.println("ID: " + task.id);
    }

    private static void handleAddStep() throws InvalidEntityException {
        System.out.print("TaskID: ");
        int taskId = Integer.parseInt(scanner.nextLine());
        System.out.print("Title: ");
        String title = scanner.nextLine();
        StepService.saveStep(taskId, title);
    }

    private static void handleDelete() throws EntityNotFoundException {
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        try {
            Task task = (Task) Database.get(id);
            ArrayList<Entity> steps = Database.getAll(Step.STEP_ENTITY_CODE);
            for (Entity e : steps) {
                Step step = (Step) e;
                if (step.taskRef == id) {
                    Database.delete(step.id);
                }
            }
        } catch (ClassCastException ignored) {}
        Database.delete(id);
        System.out.println("Entity with ID=" + id + " successfully deleted.");
    }

    private static void handleUpdateTask() throws EntityNotFoundException, InvalidEntityException {
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Field: ");
        String field = scanner.nextLine();
        System.out.print("New Value: ");
        String newValue = scanner.nextLine();
        TaskService.updateTaskField(id, field, newValue);
    }

    private static void handleUpdateStep() throws EntityNotFoundException, InvalidEntityException {
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Field: ");
        String field = scanner.nextLine();
        System.out.print("New Value: ");
        String newValue = scanner.nextLine();
        StepService.updateStepField(id, field, newValue);
    }

    private static void handleGetTaskById() throws EntityNotFoundException {
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        Task task = (Task) Database.get(id);
        printTask(task);
    }

    private static void handleGetAllTasks() {
        ArrayList<Entity> tasks = Database.getAll(Task.TASK_ENTITY_CODE);
        tasks.sort(Comparator.comparing(t -> ((Task) t).dueDate));
        for (Entity e : tasks) {
            printTask((Task) e);
            System.out.println();
        }
    }

    private static void handleGetIncompleteTasks() {
        ArrayList<Entity> tasks = Database.getAll(Task.TASK_ENTITY_CODE);
        for (Entity e : tasks) {
            Task task = (Task) e;
            if (task.status != Task.Status.Completed) {
                printTask(task);
                System.out.println();
            }
        }
    }

    private static void printTask(Task task) {
        System.out.println("ID: " + task.id);
        System.out.println("Title: " + task.title);
        System.out.println("Due Date: " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(task.dueDate));
        System.out.println("Status: " + task.status);
        ArrayList<Entity> steps = Database.getAll(Step.STEP_ENTITY_CODE);
        boolean hasSteps = false;
        for (Entity e : steps) {
            Step step = (Step) e;
            if (step.taskRef == task.id) {
                if (!hasSteps) {
                    System.out.println("Steps:");
                    hasSteps = true;
                }
                System.out.println("    + " + step.title + ":");
                System.out.println("        ID: " + step.id);
                System.out.println("        Status: " + step.status);
            }
        }
    }
}