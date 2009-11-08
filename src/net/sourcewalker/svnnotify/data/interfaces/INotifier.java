package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * @author Xperimental
 */
public interface INotifier {

    void reportUpdates(IRepository repository, List<IRevision> revisions);

}
