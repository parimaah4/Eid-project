package todo.validator;

import db.Database;
import db.Entity;
import db.Validator;
import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import todo.entity.Step;

public class StepValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if (!(entity instanceof Step)) {
            throw new IllegalArgumentException("Validator only supports Step entities");
        }
        Step step = (Step) entity;
        if (step.title == null || step.title.trim().isEmpty()) {
            throw new InvalidEntityException("Step title cannot be empty");
        }
        try {
            Database.get(step.taskRef);
        } catch (EntityNotFoundException e) {
            throw new InvalidEntityException("Cannot find task with ID=" + step.taskRef);
        }
    }
}