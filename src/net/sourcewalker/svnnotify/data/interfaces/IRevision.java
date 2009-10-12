package net.sourcewalker.svnnotify.data.interfaces;

import java.util.Date;

/**
 * @author Xperimental
 *
 */
public interface IRevision {
	
	int getRevision();
	String getAuthor();
	Date getTimestamp();
	String getMessage();
	
	void setRevision(int revision);
	void setAuthor(String author);
	void setTimestamp(Date timestamp);
	void setMessage(String message);

}
