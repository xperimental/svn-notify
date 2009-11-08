package net.sourcewalker.svnnotify.data.interfaces;

import java.util.List;

/**
 * This interface should be implemented by classes providing services to
 * permanently save information about different repositories and their
 * revisions.
 *
 * @author Xperimental
 */
public interface IDatabase {

    /**
     * This method returns a list of all repositories currently contained in the
     * database object.
     *
     * @return List of {@link IRepository} objects currently in database.
     */
    List<IRepository> getRepositories();

    /**
     * This method should add a repository object to the database. Note that
     * this doesn't mean that the repository is saved permanently, as the save
     * of the database is caused by calling {@link #save()}.
     *
     * @param repository
     *            Repository object to add to database.
     */
    void addRepository(IRepository repository);

    /**
     * Calling this method removed the repository object from the database, so
     * it isn't saved to permanent memory the next time {@link #save()} is
     * called.
     *
     * @param repository
     *            Repository object to remove from database.
     */
    void removeRepository(IRepository repository);

    /**
     * Save database to permanent memory.
     */
    void save();

}
