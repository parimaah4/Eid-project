package example;

import db.Entity;
import db.Trackable;
import java.util.Date;

public class Document extends Entity implements Trackable {
    public static final int DOCUMENT_ENTITY_CODE = 15;
    public String content;
    private Date creationDate;
    private Date lastModificationDate;

    public Document(String content) {
        this.content = content;
    }

    @Override
    public Document copy() {
        Document copyDoc = new Document(this.content);
        copyDoc.id = this.id;
        if (this.creationDate != null) {
            copyDoc.creationDate = new Date(this.creationDate.getTime());
        }
        if (this.lastModificationDate != null) {
            copyDoc.lastModificationDate = new Date(this.lastModificationDate.getTime());
        }
        return copyDoc;
    }

    @Override
    public int getEntityCode() {
        return DOCUMENT_ENTITY_CODE;
    }

    @Override
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    @Override
    public Date getCreationDate() {
        return this.creationDate;
    }

    @Override
    public void setLastModificationDate(Date date) {
        this.lastModificationDate = date;
    }

    @Override
    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }
}