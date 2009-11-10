package net.sourcewalker.svnnotify.data.xmldb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;

/**
 * Repository class to be used with the flat-file XML database implementation in
 * {@link XmlDatabase}.
 *
 * @author Xperimental
 */
public class XmlRepository implements IRepository {

    /**
     * Contains the name of the repository.
     */
    private String name;

    /**
     * Contains the URL of the repository.
     */
    private URI url;

    /**
     * Contains a list of {@link IRevision} objects which are saved with this
     * repository object.
     */
    private List<IRevision> revisions;

    /**
     * Creates a new instance of this class with the attributes initialized to
     * the specified values. The revision list is initialized but empty.
     *
     * @param repoName
     *            Name of the repository.
     * @param repoUrl
     *            URL to reach the server of the repository.
     */
    public XmlRepository(final String repoName, final URI repoUrl) {
        this.name = repoName;
        this.url = repoUrl;
        revisions = new ArrayList<IRevision>();
    }

    @Override
    public final List<IRevision> getAllRevisions() {
        return revisions;
    }

    @Override
    public final int getLastRevisionNumber() {
        int maxRev = 0;
        for (IRevision r : revisions) {
            if (r.getRevision() > maxRev) {
                maxRev = r.getRevision();
            }
        }
        return maxRev;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final IRevision getRevision(final int revNumber) {
        IRevision result = null;
        for (IRevision r : revisions) {
            if (r.getRevision() == revNumber) {
                result = r;
                break;
            }
        }
        return result;
    }

    @Override
    public final URI getURL() {
        return url;
    }

    @Override
    public final List<IRevision> updateRepository(
            final List<IRevision> update) {
        List<IRevision> updatedRevisions = new ArrayList<IRevision>();

        for (IRevision newRev : update) {
            IRevision repoRev = getRevision(newRev.getRevision());
            if (repoRev != null) {
                boolean changed = false;
                if (!repoRev.getAuthor().equals(newRev.getAuthor())) {
                    changed = true;
                }
                if (!repoRev.getTimestamp().equals(newRev.getTimestamp())) {
                    changed = true;
                }
                if (!repoRev.getMessage().equals(newRev.getMessage())) {
                    changed = true;
                }
                if (changed) {
                    revisions.remove(repoRev);
                    revisions.add(newRev);
                    updatedRevisions.add(newRev);
                }
            } else {
                revisions.add(newRev);
                updatedRevisions.add(newRev);
            }
        }

        return updatedRevisions;
    }

}
