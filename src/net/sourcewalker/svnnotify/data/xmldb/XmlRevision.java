package net.sourcewalker.svnnotify.data.xmldb;

import java.util.Date;

import net.sourcewalker.svnnotify.data.interfaces.IRevision;

/**
 * Revision class to be used with the flat-file XML database implementation in
 * {@link XmlDatabase}.
 *
 * @author Xperimental
 */
public class XmlRevision implements IRevision {

    /**
     * Contains the revision number.
     */
    private int revision;

    /**
     * Contains the revision author.
     */
    private String author;

    /**
     * Contains the commit message of the revision.
     */
    private String message;

    /**
     * Contains the creation time of the revision.
     */
    private Date timestamp;

    /**
     * Create a new instance of the class with the attributes initialized to the
     * specified values.
     *
     * @param revNumber
     *            Revision number.
     * @param revAuthor
     *            Revision author.
     * @param revTime
     *            Creation time of revision.
     * @param revMessage
     *            Commit message of revision.
     */
    public XmlRevision(final int revNumber, final String revAuthor,
            final Date revTime, final String revMessage) {
        this.revision = revNumber;
        this.author = revAuthor;
        this.timestamp = revTime;
        this.message = revMessage;
    }

    @Override
    public final int getRevision() {
        return revision;
    }

    @Override
    public final String getAuthor() {
        return author;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final Date getTimestamp() {
        return timestamp;
    }

}
