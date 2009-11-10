package net.sourcewalker.svnnotify;

/**
 * The application mode is used to set the general behaviour of the application.
 * It is set using command-line arguments, the default mode is
 * <code>NORMAL</code>.
 *
 * @author Xperimental
 */
public enum ApplicationMode {
    /**
     * Default application mode. Retrieves new revisions from the configured
     * repositories and displays the information to the user.
     */
    NORMAL,
    /**
     * Repository creation mode. Needs a name and URL for the new repository.
     */
    CREATE_REPO,
    /**
     * Repository deletion mode. Needs the name of the repository to delete.
     */
    DELETE_REPO,
    /**
     * Lists all configured repositories.
     */
    LIST_REPO,
    /**
     * The help mode is used, to display a small help text to the user. It is
     * also used as a fallback when there are errors in the command-line
     * arguments.
     */
    HELP
}
