import db.Database;
import db.exception.InvalidEntityException;
import example.Document;

public class Main {
    public static void main(String[] args) throws InvalidEntityException {
        Document doc = new Document("Eid Eid Eid");

        Database.add(doc);

        System.out.println("Document added");
        System.out.println("id: " + doc.id);
        System.out.println("content: " + doc.content);
        System.out.println("creation date: " + doc.getCreationDate());
        System.out.println("last modification date: " + doc.getLastModificationDate());
        System.out.println();

        try {
            Thread.sleep(3000); // 3 ثانیه صبر به جای 30 ثانیه برای تست سریع‌تر
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted!");
        }

        doc.content = "This is the new content";
        Database.update(doc);

        System.out.println("Document updated");
        System.out.println("id: " + doc.id);
        System.out.println("content: " + doc.content);
        System.out.println("creation date: " + doc.getCreationDate());
        System.out.println("last modification date: " + doc.getLastModificationDate());
    }
}