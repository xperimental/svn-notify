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
 * @author Xperimental
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

    ApplicationMode mode = ApplicationMode.NORMAL;
    String repoName = null;
    String repoUrl = null;

    public Application(String[] args) {
        database = new XmlDatabase("database.xml");
        objectFactory = (IObjectFactory) database;
        provider = new ShellProvider();
        notifier = new GrowlNotifier();

        parseArgs(args);
    }

    /**
     * @param args
     */
    private void parseArgs(String[] args) {
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
    public void run() {
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
            if (repoName != null && repoUrl != null)
                createRepo();
            else
                showHelpScreen();
            break;
        case DELETE_REPO:
            if (repoName != null)
                deleteRepo();
            else
                showHelpScreen();
            break;
        default:
            System.out.println("Error: Application mode not defined!");
        }
    }

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

    private void listRepos() {
        System.out.println("Configured repositories:");
        List<IRepository> repos = database.getRepositories();
        for (IRepository repo : repos) {
            System.out.println(repo.getName());
            System.out.println("  URL: " + repo.getURL().toString());
            System.out.println();
        }
    }

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
                        + " out all the repositories it is configured to monitor.\n");
        System.out.println("The parameters \"--create\" and \"--delete\" are"
                + " used to create and delete repositories for monitoring.");
    }

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
                } else
                    System.out.println("\tNo revisions to notify user about!");
                System.out.println();
            }
        }
        database.save();
    }

}
