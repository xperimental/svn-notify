package net.sourcewalker.svnnotify.data.xmldb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sourcewalker.svnnotify.data.interfaces.IDatabase;
import net.sourcewalker.svnnotify.data.interfaces.IObjectFactory;
import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Xperimental
 * 
 */
public class XmlDatabase implements IDatabase, IObjectFactory {

	static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");
	static final String DATABASE_FILE_NAME = "svn-notifier.db.xml";

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
		try {
			FileInputStream stream = new FileInputStream(DATABASE_FILE_NAME);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document document = builder.parse(stream);
			loadFromDocument(document);
		} catch (FileNotFoundException e) {
			System.out.println("No database read!");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFromDocument(Document document) {
		Node first = document.getFirstChild();
		if (first.getNodeName().equals("database")) {
			NodeList repoNodes = first.getChildNodes();
			for (int i = 0; i < repoNodes.getLength(); i++) {
				Node repoNode = repoNodes.item(i);
				try {
					if (repoNode.getNodeName().equals("repository")) {
						NodeList repoChildren = repoNode.getChildNodes();
						String name = null;
						String url = null;
						List<IRevision> revisions = null;
						for (int j = 0; j < repoChildren.getLength(); j++) {
							Node repoChild = repoChildren.item(j);
							if (repoChild.getNodeName().equals("name")) {
								name = repoChild.getTextContent();
							} else if (repoChild.getNodeName().equals("url")) {
								url = repoChild.getTextContent();
							} else if (repoChild.getNodeName().equals(
									"revisions")) {
								revisions = parseRevisions(repoChild);
							}
						}
						if (name != null && url != null && revisions != null) {
							IRepository repo = createRepository(name, new URI(
									url));
							repo.updateRepository(revisions);
							repositories.add(repo);
						}
					}
				} catch (URISyntaxException e) {
				}
			}
		}
	}

	private List<IRevision> parseRevisions(Node revsNode) {
		List<IRevision> revisions = new ArrayList<IRevision>();
		NodeList revsChildren = revsNode.getChildNodes();
		for (int i = 0; i < revsChildren.getLength(); i++) {
			Node revsChild = revsChildren.item(i);
			try {
				if (revsChild.getNodeName().equals("revision")) {
					int number = Integer.parseInt(revsChild.getAttributes()
							.getNamedItem("number").getTextContent());
					NodeList revChildren = revsChild.getChildNodes();
					String author = null;
					String message = null;
					Date timestamp = null;
					for (int j = 0; j < revChildren.getLength(); j++) {
						Node revChild = revChildren.item(j);
						if (revChild.getNodeName().equals("author"))
							author = revChild.getTextContent();
						else if (revChild.getNodeName().equals("message"))
							message = revChild.getTextContent();
						else if (revChild.getNodeName().equals("date"))
							timestamp = DATE_FORMAT.parse(revChild
									.getTextContent());
					}
					if (author != null && message != null && timestamp != null) {
						IRevision rev = createRevision(number, author,
								timestamp, message);
						revisions.add(rev);
					}
				}
			} catch (ParseException e) {
			}
		}
		return revisions;
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
							xmlWriter.writeCharacters(DATE_FORMAT.format(rev
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
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			FileOutputStream stream = new FileOutputStream(DATABASE_FILE_NAME);
			OutputStreamWriter writer = new OutputStreamWriter(stream, Charset
					.forName("UTF-8"));
			dumpDB(writer);
			writer.close();
		} catch (IOException e) {
		}
	}

}
