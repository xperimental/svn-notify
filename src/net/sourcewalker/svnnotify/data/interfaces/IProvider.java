package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * Classes implementing this interface provide the ability to query repositories
 * for new revisions by connecting to the servers.
 *
 * @author Xperimental
 */
public interface IProvider {

    /**
     * Gets all revisions from the specified repository.
     *
     * @param factory
     *            Object factory which should be used to create new revision
     *            objects.
     * @param repository
     *            Repository to query for revisions.
     * @return List of all revisions in the repository.
     */
    List<IRevision> getAllRevisions(IObjectFactory factory,
            IRepository repository);

    /**
     * Gets new revisions from the repository server. The highest revision
     * number saved in the repository object is used to determine which
     * revisions are new.
     *
     * @param factory
     *            Object factory which should be used to create new revision
     *            objects.
     * @param repository
     *            Repository to query for new revisions.
     * @return List of new revisions since last query.
     */
    List<IRevision> getNewRevisions(IObjectFactory factory,
            IRepository repository);

}
