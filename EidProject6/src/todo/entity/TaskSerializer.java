package todo.entity;

import db.Serializer;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TaskSerializer implements Serializer<Task> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public String serialize(Task task) {
        return String.join(",",
                String.valueOf(task.id),
                task.title,
                task.description,
                task.dueDate != null ? formatter.format(task.dueDate) : "",
                task.status.toString(),
                task.getCreationDate() != null ? formatter.format(task.getCreationDate()) : "",
                task.getLastModificationDate() != null ? formatter.format(task.getLastModificationDate()) : ""
        );
    }

    @Override
    public Task deserialize(String serialized) {
        String[] parts = serialized.split(",");
        if (parts.length == 7) {
            try {
                Task task = new Task(parts[1], parts[2], parts[3].isEmpty() ? null : formatter.parse(parts[3]));
                task.id = Integer.parseInt(parts[0]);
                task.status = Task.Status.valueOf(parts[4]);
                task.setCreationDate(parts[5].isEmpty() ? null : formatter.parse(parts[5]));
                task.setLastModificationDate(parts[6].isEmpty() ? null : formatter.parse(parts[6]));
                return task;
            } catch (ParseException | IllegalArgumentException e) {
                System.err.println("Error deserializing Task: " + serialized + " - " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Invalid Task serialization format: " + serialized);
            return null;
        }
    }
}
