import db.Database;
import db.exception.EntityNotFoundException;
import example.Human;

public class Main {
    public static void main(String[] args) {
        Human ali = new Human("Ali");
        Database.add(ali);

        ali.name = "Ali Hosseini";

        try {
            Human aliFromTheDatabase = (Human) Database.get(ali.id);
            System.out.println("ali's name in the database: " + aliFromTheDatabase.name);
            System.out.println("ali's name outside: " + ali.name);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}