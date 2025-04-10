package db;

import db.exception.EntityNotFoundException;

import java.util.ArrayList;

public class Database {
    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static int lastId = 0;

    // جلوگیری ازساخت  instance
    private Database() {
    }

    public static void add(Entity e) {
        lastId++;
        e.id = lastId;
        entities.add(e);
    }

    public static Entity get(int id) throws EntityNotFoundException {
        for (Entity e : entities) {
            if (e.id == id) {
                return e;
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

    public static void update(Entity e) throws EntityNotFoundException {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).id == e.id) {
                entities.set(i, e);
                return;
            }
        }
        throw new EntityNotFoundException(e.id);
    }
}
