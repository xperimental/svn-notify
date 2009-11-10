package net.sourcewalker.svnnotify.data.interfaces;

import java.net.URI;
import java.util.List;

/**
 * Classes implementing this interface are used to save information about a
 * repository.
 *
 * @author Xperimental
 */
public interface IRepository {

    /**
     * Returns the user-defined name of the repository.
     *
     * @return Display name of repository.
     */
    String getName();

    /**
     * Returns the server URL of the repository.
     *
     * @return Server URL of repository.
     */
    URI getURL();

    /**
     * Returns a list of revisions currently saved in the repository object.
     *
     * @return List of revisions in object.
     */
    List<IRevision> getAllRevisions();

    /**
     * Returns a specific revision.
     *
     * @param revNumber
     *            Revision number of revision to return.
     * @return Specific revision object.
     */
    IRevision getRevision(int revNumber);

    /**
     * Returns the highest revision number saved in the repository object. This
     * is by definition the number of the newest revision.
     *
     * @return Highest revision number.
     */
    int getLastRevisionNumber();

    /**
     * Updates the repository object with a list of new revision objects. The
     * new objects are saved in the repository object, duplicates (determined
     * using the revision number) are ignored. All revisions, which are really
     * new are returned in a list.
     *
     * @param update
     *            List of revision objects which should update the repository.
     * @return List of revision objects which were new to the repository.
     */
    List<IRevision> updateRepository(List<IRevision> update);

}
