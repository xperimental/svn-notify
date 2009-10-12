package net.sourcewalker.svnnotify.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sourcewalker.svnnotify.data.interfaces.IObjectFactory;
import net.sourcewalker.svnnotify.data.interfaces.IProvider;
import net.sourcewalker.svnnotify.data.interfaces.IRepository;
import net.sourcewalker.svnnotify.data.interfaces.IRevision;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Xperimental
 * 
 */
public class ShellProvider implements IProvider {

	@Override
	public List<IRevision> getAllRevisions(IObjectFactory factory,
			IRepository repository) {
		return getRevisions(factory, repository.getURL().toString(), 0);
	}

	@Override
	public List<IRevision> getNewRevisions(IObjectFactory factory,
			IRepository repository) {
		return getRevisions(factory, repository.getURL().toString(), repository
				.getLastRevisionNumber());
	}

	/**
	 * @param factory
	 * @param repository
	 * @param startRevision
	 * @return
	 */
	private List<IRevision> getRevisions(IObjectFactory factory, String url,
			int startRevision) {
		List<String> params = new ArrayList<String>();
		params.add("svn");
		params.add("log");
		params.add(url);
		params.add("--xml");
		params.add("-r");
		params.add(startRevision + ":HEAD");
		try {
			Process svn = Runtime.getRuntime().exec(
					params.toArray(new String[0]));
			BufferedInputStream svnOutput = new BufferedInputStream(svn
					.getInputStream());
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document document = builder.parse(svnOutput);
			return parseXml(factory, document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param factory
	 * @param document
	 * @return
	 */
	private List<IRevision> parseXml(IObjectFactory factory, Document document) {
		List<IRevision> result = new ArrayList<IRevision>();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Node first = document.getFirstChild();
		if (first.getNodeName().equals("log")) {
			NodeList logEntries = first.getChildNodes();
			for (int i = 0; i < logEntries.getLength(); i++) {
				Node logEntry = logEntries.item(i);
				if (logEntry.getNodeName().equals("logentry")) {
					int revision = Integer.parseInt(logEntry.getAttributes()
							.getNamedItem("revision").getTextContent());
					String author = null;
					String message = null;
					Date timestamp = null;
					NodeList entryChildren = logEntry.getChildNodes();
					for (int j = 0; j < entryChildren.getLength(); j++) {
						Node child = entryChildren.item(j);
						if (child.getNodeName().equals("author")) {
							author = child.getTextContent();
						} else if (child.getNodeName().equals("date")) {
							try {
								timestamp = dateFormat.parse(child
										.getTextContent());
							} catch (DOMException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (child.getNodeName().equals("msg")) {
							message = child.getTextContent();
						}
					}
					if (author != null && message != null && timestamp != null) {
						IRevision rev = factory.createRevision(revision,
								author, timestamp, message);
						result.add(rev);
					}
				}
			}
		}

		return result;
	}

}
