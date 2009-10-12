package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * @author Xperimental
 * 
 */
public interface IProvider {

	List<IRevision> getAllRevisions(IObjectFactory factory,
			IRepository repository);

	List<IRevision> getNewRevisions(IObjectFactory factory,
			IRepository repository);

}
