package net.sourcewalker.svnnotify.data.xmldb;

import java.util.Date;

import net.sourcewalker.svnnotify.data.interfaces.IRevision;

/**
 * @author Xperimental
 */
public class XmlRevision implements IRevision {

    int revision;
    String author;
    String message;
    Date timestamp;

    /**
     * @param revision
     * @param author
     * @param timestamp
     * @param message
     */
    public XmlRevision(int revision, String author, Date timestamp,
            String message) {
        this.revision = revision;
        this.author = author;
        this.timestamp = timestamp;
        this.message = message;
    }

    public int getRevision() {
        return revision;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
