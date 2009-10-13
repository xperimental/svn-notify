package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * @author Xperimental
 *
 */
public interface IDatabase {

	List<IRepository> getRepositories();
	void addRepository(IRepository repository);
	void removeRepository(IRepository repository);
	void save();
	
}
