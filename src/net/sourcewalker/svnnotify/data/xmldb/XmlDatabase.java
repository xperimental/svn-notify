package net.sourcewalker.svnnotify.data.xmldb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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

	protected void load() {
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

	protected void dumpDB(Writer output) {
		XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter xmlWriter = outFactory
					.createXMLStreamWriter(output);
			xmlWriter.writeStartDocument("UTF-8", "1.0");
			if (repositories.size() == 0)
				xmlWriter.writeEmptyElement("database");
			else {
				xmlWriter.writeStartElement("database");
				for (IRepository repo : repositories) {
					List<IRevision> revs = repo.getAllRevisions();

					xmlWriter.writeStartElement("repository");
					xmlWriter.writeStartElement("name");
					xmlWriter.writeCharacters(repo.getName());
					xmlWriter.writeEndElement();
					xmlWriter.writeStartElement("url");
					xmlWriter.writeCharacters(repo.getURL().toString());
					xmlWriter.writeEndElement();
					if (revs.size() == 0)
						xmlWriter.writeEmptyElement("revisions");
					else {
						xmlWriter.writeStartElement("revisions");
						for (IRevision rev : revs) {
							xmlWriter.writeStartElement("revision");
							xmlWriter.writeAttribute("number", Integer
									.toString(rev.getRevision()));
							xmlWriter.writeStartElement("date");
							xmlWriter.writeCharacters(dateFormat.format(rev
									.getTimestamp()));
							xmlWriter.writeEndElement();
							xmlWriter.writeStartElement("author");
							xmlWriter.writeCharacters(rev.getAuthor());
							xmlWriter.writeEndElement();
							xmlWriter.writeStartElement("message");
							xmlWriter.writeCharacters(rev.getMessage());
							xmlWriter.writeEndElement();
							xmlWriter.writeEndElement();
						}
						xmlWriter.writeEndElement();
					}
					xmlWriter.writeEndElement();
				}
				xmlWriter.writeEndElement();
			}
			xmlWriter.writeEndDocument();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			FileOutputStream stream = new FileOutputStream(
					"svn-notifier.db.xml");
			OutputStreamWriter writer = new OutputStreamWriter(stream, Charset
					.forName("UTF-8"));
			dumpDB(writer);
			writer.close();
		} catch (IOException e) {
		}
	}

}
