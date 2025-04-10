package db;

public abstract class Entity {
    public int id;

    public Entity() {
        // id در Database تنظیم می‌شود
    }

    public abstract Entity copy();

    public abstract int getEntityCode();
}