package net.sourcewalker.svnnotify.data.interfaces;

import java.util.Date;

/**
 * Classes implementing this interface are used to save information about a
 * revision contained in a repository.
 *
 * @author Xperimental
 */
public interface IRevision {

    /**
     * Returns the revision number of this revision.
     *
     * @return Revision number.
     */
    int getRevision();

    /**
     * Returns the author of this revision.
     *
     * @return Author of revision.
     */
    String getAuthor();

    /**
     * Returns the time this revision was created.
     *
     * @return Creation time of revision.
     */
    Date getTimestamp();

    /**
     * Returns the commit message (comment) of this revision.
     *
     * @return Commit message of revision.
     */
    String getMessage();

}
