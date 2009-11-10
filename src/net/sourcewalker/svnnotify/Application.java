package net.sourcewalker.svnnotify;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.sourcewalker.svnnotify.data.ShellProvider;
import net.sourcewalker.svnnotify.data.interfaces.IDatabase;
import net.sourcewalker.svnnotify.data.interfaces.INotifier;
import net.sourcewalker.svnnotify.data.interfaces.IObjectFactory;
import net.sourcewalker.svnnotify.data.interfaces.IProvider;
import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;
import net.sourcewalker.svnnotify.data.xmldb.XmlDatabase;
import net.sourcewalker.svnnotify.notifier.GrowlNotifier;

/**
 * The {@link Application} class contains the central code for the application.
 * This class is {@link Runnable} and also contains a main method, which makes
 * it runnable from the command-line (as intended), but also as a Thread in
 * another application.
 *
 * @author Xperimental
 */
public class Application implements Runnable {

    /**
     * Entry point for application start.
     *
     * @param args
     *            Command-line parameters.
     */
    public static void main(final String[] args) {
        Application app = new Application(args);
        app.run();
    }

    /**
     * Contains the currently used database.
     */
    private IDatabase database;

    /**
     * Contains the currently used {@link IObjectFactory} to create objects.
     */
    private IObjectFactory objectFactory;

    /**
     * Contains the provider used to get information from repository servers.
     */
    private IProvider provider;

    /**
     * Contains the notifier used to display the repository information to the
     * user.
     */
    private INotifier notifier;

    /**
     * Contains the mode the application is run as. The mode is determined by
     * command-line parameters. If no parameter is specified by the user, the
     * mode <code>NORMAL</code> is used.
     */
    private ApplicationMode mode = ApplicationMode.NORMAL;

    /**
     * Contains the repository name provided by the user. This is used to create
     * or delete repositories. The value <code>null</code> indicates, that no
     * name was provided.
     */
    private String repoName = null;

    /**
     * Contains the repository URL provided by the user. The URL is used to
     * create new repository definitions. The value <code>null</code> indicates,
     * that no URL was provided by the user.
     */
    private String repoUrl = null;

    /**
     * Creates a new application instance using the specified command-line
     * parameters.
     *
     * @param args
     *            Array of command-line parameters passed to the application.
     */
    public Application(final String[] args) {
        database = new XmlDatabase("database.xml");
        objectFactory = (IObjectFactory) database;
        provider = new ShellProvider();
        notifier = new GrowlNotifier();

        parseArgs(args);
    }

    /**
     * Parses the supplied command-line parameters and sets the attributes of
     * the class accordingly.
     *
     * @param args
     *            Command-line parameters to parse.
     */
    private void parseArgs(final String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--help")) {
                mode = ApplicationMode.HELP;
                return;
            }
            if (arg.equalsIgnoreCase("--create")) {
                mode = ApplicationMode.CREATE_REPO;
                continue;
            }
            if (arg.equalsIgnoreCase("--delete")) {
                mode = ApplicationMode.DELETE_REPO;
                continue;
            }
            if (arg.equalsIgnoreCase("--list")) {
                mode = ApplicationMode.LIST_REPO;
                return;
            }
            if (arg.equalsIgnoreCase("--name")) {
                repoName = args[i + 1];
                continue;
            }
            if (arg.equalsIgnoreCase("--url")) {
                repoUrl = args[i + 1];
                continue;
            }
        }
    }

    @Override
    public final void run() {
        switch (mode) {
        case NORMAL:
            normalRun();
            break;
        case HELP:
            showHelpScreen();
            break;
        case LIST_REPO:
            listRepos();
            break;
        case CREATE_REPO:
            if (repoName != null && repoUrl != null) {
                createRepo();
            } else {
                showHelpScreen();
            }
            break;
        case DELETE_REPO:
            if (repoName != null) {
                deleteRepo();
            } else {
                showHelpScreen();
            }
            break;
        default:
            System.out.println("Error: Application mode not defined!");
        }
    }

    /**
     * Deletes a repository from the database. The name of the repository to
     * delete is read from the <code>repoName</code> attribute.
     */
    private void deleteRepo() {
        List<IRepository> repositories = database.getRepositories();
        IRepository delete = null;
        for (IRepository repo : repositories) {
            if (repo.getName().equals(repoName)) {
                delete = repo;
                break;
            }
        }
        if (delete != null) {
            database.removeRepository(delete);
            System.out.println("Deleted repository: " + delete.getName() + " ("
                    + delete.getURL().toString() + ")");
            database.save();
        } else {
            System.out.println("Error: Repository not found!");
        }
    }

    /**
     * Creates a new repository in the database. Name and URL of the new
     * repository are read from class attributes.
     */
    private void createRepo() {
        URI url;
        try {
            url = new URI(repoUrl);
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL!");
            return;
        }
        IRepository repo = objectFactory.createRepository(repoName, url);
        database.addRepository(repo);
        database.save();

        System.out.println("Created repository: " + repoName + " ("
                + url.toString() + ")");
    }

    /**
     * Lists the repositories currently present in the database.
     */
    private void listRepos() {
        System.out.println("Configured repositories:");
        List<IRepository> repos = database.getRepositories();
        for (IRepository repo : repos) {
            System.out.println(repo.getName());
            System.out.println("  URL: " + repo.getURL().toString());
            System.out.println();
        }
    }

    /**
     * Displays a help screen to the user, if the command-line parameters
     * provided to the application are invalid.
     */
    private void showHelpScreen() {
        System.out.println("usage:");
        System.out.println("\tsvn-notify");
        System.out.println("\tsvn-notify --help");
        System.out.println("\tsvn-notify --list");
        System.out.println("\tsvn-notify --create --name <name> --url <url>");
        System.out.println("\tsvn-notify --delete --name <name>\n");
        System.out
                .println("Running svn-notify without parameters will check the"
                        + " repositories for new revisions and alert the user,"
                        + " if there are any.\n");
        System.out.println("Running svn-notify with the \"--help\" parameter"
                + " will show this message.\n");
        System.out
                .println("The parameter \"--list\" causes svn-notify to print"
                        + " out all the repositories it is configured"
                        + " to monitor.\n");
        System.out.println("The parameters \"--create\" and \"--delete\" are"
                + " used to create and delete repositories for monitoring.");
    }

    /**
     * This method contains the logic for a normal run, which checks all
     * repositories for new revisions and displays information about them to the
     * user.
     */
    private void normalRun() {
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
                } else {
                    System.out.println("\tNo revisions to notify user about!");
                }
                System.out.println();
            }
        }
        database.save();
    }

}
