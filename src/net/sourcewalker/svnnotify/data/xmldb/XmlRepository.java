package net.sourcewalker.svnnotify.data.xmldb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;

/**
 * @author Xperimental
 */
public class XmlRepository implements IRepository {

    String name;
    URI url;
    List<IRevision> revisions;

    public XmlRepository(String name, URI url) {
        this.name = name;
        this.url = url;
        revisions = new ArrayList<IRevision>();
    }

    @Override
    public List<IRevision> getAllRevisions() {
        return revisions;
    }

    @Override
    public int getLastRevisionNumber() {
        int maxRev = 0;
        for (IRevision r : revisions) {
            if (r.getRevision() > maxRev)
                maxRev = r.getRevision();
        }
        return maxRev;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IRevision getRevision(int revNumber) {
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
    public URI getURL() {
        return url;
    }

    @Override
    public List<IRevision> updateRepository(List<IRevision> update) {
        List<IRevision> updatedRevisions = new ArrayList<IRevision>();

        for (IRevision newRev : update) {
            IRevision repoRev = getRevision(newRev.getRevision());
            if (repoRev != null) {
                boolean changed = false;
                if (!repoRev.getAuthor().equals(newRev.getAuthor()))
                    changed = true;
                if (!repoRev.getTimestamp().equals(newRev.getTimestamp()))
                    changed = true;
                if (!repoRev.getMessage().equals(newRev.getMessage()))
                    changed = true;
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
