package db;

public interface Serializer<T extends Entity> {
    String serialize(T entity);
    T deserialize(String serialized);
}