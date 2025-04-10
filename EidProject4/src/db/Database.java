package db;

import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database {
    private static ArrayList<Entity> entities = new ArrayList<>();
    private static HashMap<Integer, Validator> validators = new HashMap<>();
    private static int idCounter = 1;

    private Database() {} // جلوگیری از ساخت instance

    public static void registerValidator(int entityCode, Validator validator) {
        if (validators.containsKey(entityCode)) {
            throw new IllegalArgumentException("A validator for entity code " + entityCode + " already exists");
        }
        validators.put(entityCode, validator);
    }

    public static void add(Entity e) throws InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if (validator != null) {
            validator.validate(e);
        }
        e.id = idCounter++;
        if (e instanceof Trackable) {
            Trackable trackable = (Trackable) e;
            Date now = new Date();
            trackable.setCreationDate(now);
            trackable.setLastModificationDate(now);
        }
        entities.add(e.copy());
    }

    public static Entity get(int id) throws EntityNotFoundException {
        for (Entity e : entities) {
            if (e.id == id) {
                return e.copy();
            }
        }
        throw new EntityNotFoundException(id);
    }

    public static void delete(int id) throws EntityNotFoundException {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).id == id) {
                entities.remove(i);
                return;
            }
        }
        throw new EntityNotFoundException(id);
    }

    public static void update(Entity e) throws EntityNotFoundException, InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if (validator != null) {
            validator.validate(e);
        }
        if (e instanceof Trackable) {
            Trackable trackable = (Trackable) e;
            trackable.setLastModificationDate(new Date());
        }
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).id == e.id) {
                entities.set(i, e.copy());
                return;
            }
        }
        throw new EntityNotFoundException(e.id);
    }
}