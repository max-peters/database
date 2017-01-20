package database.services.writerReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.pluginRegistry.IPluginRegistry;

public class XmlWriterReader implements IWriterReader {
	private Element			appendTo;
	private Document		document;
	private DocumentBuilder	documentBuilder;
	private File			localStorage;

	public XmlWriterReader(String fileDirectory) throws FileNotFoundException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		localStorage = new File(fileDirectory);
		setUp();
		if (!localStorage.exists()) {
			throw new FileNotFoundException();
		}
	}

	@Override public void add(String nodeName, String leaveName, String content) {
		Element node = (Element) document.getElementsByTagName(nodeName).item(0);
		if (node == null) {
			node = document.createElement(nodeName);
		}
		Element leave = document.createElement(leaveName);
		leave.setTextContent(content);
		appendTo.appendChild(node);
		node.appendChild(leave);
	}

	@Override public void read() throws ParserConfigurationException, SAXException, IOException {
		IPluginRegistry pluginRegistry = ServiceRegistry.Instance().get(IPluginRegistry.class);
		Document document = documentBuilder.parse(localStorage);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("*");
		for (int i = 0; i < nList.getLength(); i++) {
			Plugin plugin = pluginRegistry.getPlugin(nList.item(i).getParentNode().getNodeName());
			if (plugin != null) {
				plugin.read(nList.item(i));
			}
		}
	}

	@Override public void write() throws ParserConfigurationException, TransformerException {
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(localStorage);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.transform(domSource, streamResult);
		setUp();
	}

	private void setUp() {
		document = documentBuilder.newDocument();
		appendTo = document.createElement("database");
		document.appendChild(appendTo);
	}
}
