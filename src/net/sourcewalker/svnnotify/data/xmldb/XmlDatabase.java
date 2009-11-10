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
 * This class contains a flat-file database implementation using XML. It
 * implements the {@link IDatabase} and {@link IObjectFactory} interfaces.
 *
 * @author Xperimental
 */
public class XmlDatabase implements IDatabase, IObjectFactory {

    /**
     * Constant field contains the date-time format in the XML file.
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Constant field contains the default database file name.
     */
    private static final String DATABASE_FILE_NAME = "svn-notify.db.xml";

    /**
     * Contains the file name used to save the file.
     */
    private String fileName;

    /**
     * Contains the list of repositories in the database.
     */
    private List<IRepository> repositories;

    /**
     * Create a new database instance using the filename provided. If the file
     * exists, the database is loaded from the file.
     *
     * @param databaseFileName
     *            File to load database from.
     */
    public XmlDatabase(final String databaseFileName) {
        this.fileName = databaseFileName;
        this.repositories = new ArrayList<IRepository>();

        load();
    }

    @Override
    public final void addRepository(final IRepository repository) {
        repositories.add(repository);
    }

    @Override
    public final List<IRepository> getRepositories() {
        return repositories;
    }

    @Override
    public final void removeRepository(final IRepository repository) {
        repositories.remove(repository);
    }

    /**
     * Loads the database from the file.
     */
    protected final void load() {
        try {
            fileName = DATABASE_FILE_NAME;
            FileInputStream stream = new FileInputStream(fileName);
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

    /**
     * Loads the contents from the XML document. Called by the {@link #load()}
     * method.
     *
     * @param document
     *            XML Document to load data from.
     */
    private void loadFromDocument(final Document document) {
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
                    System.err.println("Error while parsing repository URL: "
                            + e.getMessage());
                }
            }
        }
    }

    /**
     * Load revision data from a &lt;revisions&gt; node.
     *
     * @param revsNode
     *            XML node to load revisions from.
     * @return List of {@link IRevision} objects loaded from the XML node.
     */
    private List<IRevision> parseRevisions(final Node revsNode) {
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
                        if (revChild.getNodeName().equals("author")) {
                            author = revChild.getTextContent();
                        } else if (revChild.getNodeName().equals("message")) {
                            message = revChild.getTextContent();
                        } else if (revChild.getNodeName().equals("date")) {
                            timestamp = DATE_FORMAT.parse(revChild
                                    .getTextContent());
                        }
                    }
                    if (author != null && message != null
                            && timestamp != null) {
                        IRevision rev = createRevision(number, author,
                                timestamp, message);
                        revisions.add(rev);
                    }
                }
            } catch (ParseException e) {
                System.err.println("Error while parsing date of revision."
                        + " Revision ignored!");
            }
        }
        return revisions;
    }

    @Override
    public final IRepository createRepository(final String name,
            final URI url) {
        return new XmlRepository(name, url);
    }

    @Override
    public final IRevision createRevision(final int revision,
            final String author, final Date timestamp, final String message) {
        return new XmlRevision(revision, author, timestamp, message);
    }

    /**
     * Dumps the current database content to a output stream.
     *
     * @param output
     *            Writer to use for output.
     */
    protected final void dumpDB(final Writer output) {
        XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter xmlWriter = outFactory
                    .createXMLStreamWriter(output);
            xmlWriter.writeStartDocument("UTF-8", "1.0");
            if (repositories.size() == 0) {
                xmlWriter.writeEmptyElement("database");
            } else {
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
                    if (revs.size() == 0) {
                        xmlWriter.writeEmptyElement("revisions");
                    } else {
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

    @Override
    public final void save() {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);
            OutputStreamWriter writer = new OutputStreamWriter(stream, Charset
                    .forName("UTF-8"));
            dumpDB(writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error while writing database: "
                    + e.getMessage());
        }
    }

}
