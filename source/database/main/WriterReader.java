package database.main;

import java.io.File;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Plugin;
import database.plugin.Storage;

public class WriterReader {
	private final File		localStorage;
	private PluginContainer	pluginContainer;
	private final File		remoteStorage;
	private Storage			storage;

	public WriterReader(PluginContainer pluginContainer, Storage storage) {
		this.storage = storage;
		this.pluginContainer = pluginContainer;
		localStorage = new File(System.getProperty("user.home") + "/Documents/storage.xml");
		remoteStorage = new File("Z:/storage.xml");
	}

	public Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element database = document.createElement("database");
		Element element = document.createElement("storage");
		document.appendChild(database);
		pluginContainer.addToDocument(document, database);
		storage.print(document, element);
		database.appendChild(element);
		return document;
	}

	public void read() throws InterruptedException, IOException, SAXException, ParserConfigurationException {
		if (!localStorage.exists()) {
			if (remoteStorage.exists() || connect() == 0) {
				readFile(remoteStorage);
			}
		}
		else {
			readFile(localStorage);
		}
	}

	public void readDocument(Document document) throws ParserConfigurationException {
		NodeList nList = document.getElementsByTagName("*");
		for (int i = 0; i < nList.getLength(); i++) {
			Plugin plugin = pluginContainer.getPlugin(nList.item(i).getParentNode().getNodeName());
			if (plugin != null) {
				plugin.read(nList.item(i));
			}
			else if (nList.item(i).getParentNode().getNodeName().equals("storage")) {
				storage.read(nList.item(i));
			}
		}
	}

	public void updateStorage() throws InterruptedException, IOException, SAXException, TransformerException, ParserConfigurationException, BadLocationException {
		if ((remoteStorage.exists() || connect() == 0) && localStorage.exists()) {
			File newestFile = remoteStorage.lastModified() < localStorage.lastModified() ? localStorage : remoteStorage;
			Terminal.printLine("loading " + newestFile.getPath(), StringType.REQUEST, StringFormat.ITALIC);
			pluginContainer.clear();
			readFile(newestFile);
			writeFile(localStorage);
			writeFile(remoteStorage);
			Terminal.printLine(newestFile.getPath() + " loaded", StringType.REQUEST, StringFormat.ITALIC);
		}
		else {
			Terminal.printLine("connection failed", StringType.REQUEST, StringFormat.ITALIC);
		}
	}

	public void write() throws ParserConfigurationException, TransformerException {
		writeFile(localStorage);
	}

	private int connect() throws InterruptedException, IOException {
		Process connection = Runtime.getRuntime().exec("cmd.exe /c net use Z: https://webdav.hidrive.strato.com/users/maxptrs/Server /user:maxptrs ***REMOVED*** /persistent:no");
		return connection.waitFor();
	}

	private void readFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(file);
		document.getDocumentElement().normalize();
		readDocument(document);
	}

	private void writeFile(File file) throws ParserConfigurationException, TransformerException {
		DOMSource domSource = new DOMSource(createDocument());
		StreamResult streamResult = new StreamResult(file);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.transform(domSource, streamResult);
	}
}
