package todo.entity;

import db.Serializer;

public class StepSerializer implements Serializer<Step> {
    @Override
    public String serialize(Step step) {
        return String.join(",",
                String.valueOf(step.id),
                step.title,
                step.status.toString(),
                String.valueOf(step.taskRef)
        );
    }

    @Override
    public Step deserialize(String serialized) {
        String[] parts = serialized.split(",");
        if (parts.length == 4) {
            try {
                Step step = new Step(parts[1], Integer.parseInt(parts[3]));
                step.id = Integer.parseInt(parts[0]);
                step.status = Step.Status.valueOf(parts[2]);
                return step;
            } catch (IllegalArgumentException e) {
                System.err.println("Error deserializing Step: " + serialized + " - " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Invalid Step serialization format: " + serialized);
            return null;
        }
    }
}
