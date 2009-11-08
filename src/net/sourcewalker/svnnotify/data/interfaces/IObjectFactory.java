package net.sourcewalker.svnnotify.data.interfaces;

import java.net.URI;
import java.util.Date;

/**
 * @author Xperimental
 */
public interface IObjectFactory {

    IRepository createRepository(String name, URI url);

    IRevision createRevision(int revision, String author, Date timestamp,
            String message);

}
