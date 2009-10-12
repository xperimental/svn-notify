package net.sourcewalker.svnnotify.data.interfaces;

import java.net.URI;
import java.util.List;

/**
 * @author Xperimental
 *
 */
public interface IRepository {

	String getName();
	URI getURL();
	List<IRevision> getAllRevisions();
	IRevision getRevision(int revNumber);
	int getLastRevisionNumber();
	List<IRevision> updateRepository(List<IRevision> update);
	
}
