package net.sourcewalker.svnnotify.data.interfaces;

import java.net.URI;
import java.util.Date;

/**
 * Classes implementing this interface provide the ability to create instances
 * of objects implementing the {@link IRepository} or {@link IRevision}
 * interfaces. Normally an {@link IObjectFactory} is paired with a matching
 * {@link IDatabase} which can save the created objects to permanent memory.
 *
 * @author Xperimental
 */
public interface IObjectFactory {

    /**
     * Creates a new repository object containing information about a repository
     * which should be checked for revisions.
     *
     * @param name
     *            Name of the repository.
     * @param url
     *            URL to reach the repository.
     * @return New {@link IRepository} object containing the provided info.
     */
    IRepository createRepository(String name, URI url);

    /**
     * Creates a new revision object containing informations about a commit.
     *
     * @param revision
     *            Revision number.
     * @param author
     *            Author of the revision (Committer).
     * @param timestamp
     *            Date and time the revision was created in the repository.
     * @param message
     *            Commit message (Comment) of the revision.
     * @return A new {@link IRevision} object containing the provided info.
     */
    IRevision createRevision(int revision, String author, Date timestamp,
            String message);

}
