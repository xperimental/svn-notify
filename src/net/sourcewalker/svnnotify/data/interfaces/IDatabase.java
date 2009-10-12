package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * @author Xperimental
 *
 */
public interface IDatabase {

	List<IRepository> getRepositories();
	IRepository addRepository();
	void removeRepository(IRepository repo);
	
}
