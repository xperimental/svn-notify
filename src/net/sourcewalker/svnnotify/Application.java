package net.sourcewalker.svnnotify;

import java.util.List;

import net.sourcewalker.svnnotify.data.NullNotifier;
import net.sourcewalker.svnnotify.data.ShellProvider;
import net.sourcewalker.svnnotify.data.interfaces.IDatabase;
import net.sourcewalker.svnnotify.data.interfaces.INotifier;
import net.sourcewalker.svnnotify.data.interfaces.IObjectFactory;
import net.sourcewalker.svnnotify.data.interfaces.IProvider;
import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;
import net.sourcewalker.svnnotify.data.xmldb.XmlDatabase;

/**
 * @author Xperimental
 * 
 */
public class Application implements Runnable {

	/**
	 * Entry point for application start.
	 * 
	 * @param args
	 *            Command-line parameters.
	 */
	public static void main(String[] args) {
		Application app = new Application(args);
		app.run();
	}

	IDatabase database;
	IObjectFactory objectFactory;
	IProvider provider;
	INotifier notifier;

	public Application(String[] args) {
		database = new XmlDatabase("database.xml");
		objectFactory = (IObjectFactory) database;
		provider = new ShellProvider();
		notifier = new NullNotifier();
	}

	@Override
	public void run() {
		System.out.println("Dump database:");
		((XmlDatabase) database).dumpDB(System.out);
		System.out.println("Check for new revisions:");
		for (IRepository repo : database.getRepositories()) {
			System.out.println("Checking repository " + repo.getName());
			List<IRevision> newRevisions = provider.getNewRevisions(
					objectFactory, repo);
			System.out.println("\tGot " + newRevisions.size()
					+ " revisions from server.");
			if (newRevisions.size() > 0) {
				List<IRevision> notifyRevisions = repo
						.updateRepository(newRevisions);
				if (notifyRevisions.size() > 0) {
					System.out.println("\t" + notifyRevisions.size()
							+ " revisions left for notify!");
					notifier.reportUpdates(repo, notifyRevisions);
				} else
					System.out.println("\tNo revisions to notify user about!");
				System.out.println();
			}
		}
		System.out.println("Dump database:");
		((XmlDatabase) database).dumpDB(System.out);
	}

}
