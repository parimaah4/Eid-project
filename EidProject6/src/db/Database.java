package db;

import db.exception.EntityNotFoundException;
import db.exception.InvalidEntityException;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static ArrayList<Entity> entities = new ArrayList<>();
    private static HashMap<Integer, Validator> validators = new HashMap<>();
    private static HashMap<Integer, Serializer> serializers = new HashMap<>();
    private static int idCounter = 1;
    private static final String DB_FILE_NAME = "db.txt";

    private Database() {}

    public static void registerValidator(int entityCode, Validator validator) {
        if (validators.containsKey(entityCode)) {
            throw new IllegalArgumentException("A validator for entity code " + entityCode + " already exists");
        }
        validators.put(entityCode, validator);
    }

    public static void registerSerializer(int entityCode, Serializer serializer) {
        if (serializers.containsKey(entityCode)) {
            throw new IllegalArgumentException("A serializer for entity code " + entityCode + " already exists");
        }
        serializers.put(entityCode, serializer);
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

    public static ArrayList<Entity> getAll(int entityCode) {
        ArrayList<Entity> result = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getEntityCode() == entityCode) {
                result.add(e.copy());
            }
        }
        return result;
    }

    public static void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DB_FILE_NAME))) {
            for (Entity entity : entities) {
                int entityCode = entity.getEntityCode();
                Serializer serializer = serializers.get(entityCode);
                if (serializer != null) {
                    String serializedEntity = serializer.serialize(entity);
                    writer.write(entityCode + ":" + serializedEntity);
                    writer.newLine();
                } else {
                    System.err.println("Serializer not found for entity code: " + entityCode);
                }
            }
            System.out.println("Database saved to " + DB_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    public static void load() {
        entities.clear();
        idCounter = 1; // Reset id counter on load
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DB_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    int entityCode = Integer.parseInt(parts[0]);
                    String serializedEntity = parts[1];
                    Serializer serializer = serializers.get(entityCode);
                    if (serializer != null) {
                        Entity entity = (Entity) serializer.deserialize(serializedEntity);
                        if (entity != null) {
                            entities.add(entity);
                            if (entity.id > maxId) {
                                maxId = entity.id;
                            }
                        } else {
                            System.err.println("Error deserializing entity with code: " + entityCode + ", data: " + serializedEntity);
                        }
                    } else {
                        System.err.println("Serializer not found for entity code during load: " + entityCode);
                    }
                } else {
                    System.err.println("Invalid format in database file: " + line);
                }
            }
            idCounter = maxId + 1; // Update id counter after loading
            System.out.println("Database loaded from " + DB_FILE_NAME);
        } catch (FileNotFoundException e) {
            System.out.println("Database file not found. Starting with an empty database.");
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }
}