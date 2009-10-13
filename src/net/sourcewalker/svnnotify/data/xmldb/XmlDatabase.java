package net.sourcewalker.svnnotify.data.xmldb;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourcewalker.svnnotify.data.interfaces.IDatabase;
import net.sourcewalker.svnnotify.data.interfaces.IObjectFactory;
import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;

/**
 * @author Xperimental
 * 
 */
public class XmlDatabase implements IDatabase, IObjectFactory {

	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	String fileName;
	List<IRepository> repositories;

	public XmlDatabase(String fileName) {
		this.fileName = fileName;
		this.repositories = new ArrayList<IRepository>();

		load();
	}

	@Override
	public void addRepository(IRepository repository) {
		repositories.add(repository);
	}

	@Override
	public List<IRepository> getRepositories() {
		return repositories;
	}

	@Override
	public void removeRepository(IRepository repository) {
		repositories.remove(repository);
	}

	private void load() {
		// Create some demo data
	}

	@Override
	public IRepository createRepository(String name, URI url) {
		return new XmlRepository(name, url);
	}

	@Override
	public IRevision createRevision(int revision, String author,
			Date timestamp, String message) {
		return new XmlRevision(revision, author, timestamp, message);
	}

	/**
	 * @param out
	 */
	public void dumpDB(PrintStream output) {
		output.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		if (repositories.size() == 0)
			output.println("<database />");
		else {
			output.println("<database>");
			for (IRepository repo : repositories) {
				List<IRevision> revs = repo.getAllRevisions();

				output.println("  <repository>");
				output.println("    <name>" + repo.getName() + "</name>");
				output.println("    <url>" + repo.getURL().toString()
						+ "</url>");
				if (revs.size() == 0)
					output.println("    <revisions />");
				else {
					output.println("    <revisions>");
					for (IRevision rev : revs) {
						output.println("      <revision number=\""
								+ rev.getRevision() + "\">");
						output.println("        <date>"
								+ dateFormat.format(rev.getTimestamp())
								+ "</date>");
						output.println("        <author>" + rev.getAuthor()
								+ "</author>");
						output.println("        <message>" + rev.getMessage()
								+ "</message>");
						output.println("      </revision>");
					}
					output.println("    </revisions>");
				}
				output.println("  </repository>");
			}
			output.println("</database>");
		}
	}

}
