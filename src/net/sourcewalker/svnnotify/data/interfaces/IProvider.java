package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * @author Xperimental
 *
 */
public interface IProvider {
	
	List<IRevision> getAllRevisions(IRepository repository);
	List<IRevision> getNewRevisions(IRepository repository);

}
