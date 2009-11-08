package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * Classes implementing this interface are responsible for notifying the user of
 * new revisions. How the user is notified is subject to the class
 * implementation.
 *
 * @author Xperimental
 */
public interface INotifier {

    /**
     * Notify the user about new revisions in a repository. This method is
     * called once for each repository with new revisions.
     *
     * @param repository
     *            Repository with new revisions.
     * @param revisions
     *            List with all new revisions.
     */
    void reportUpdates(IRepository repository, List<IRevision> revisions);

}
