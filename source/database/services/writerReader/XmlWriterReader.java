package database.services.writerReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
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

public class XmlWriterReader implements IWriterReader {
	private Element					appendTo;
	private Document				document;
	private DocumentBuilder			documentBuilder;
	private File					localStorage;
	private Map<String, IWriteRead>	register;

	public XmlWriterReader(String fileDirectory) throws FileNotFoundException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
		this.localStorage = new File(fileDirectory);
		setUp();
		if (!this.localStorage.exists()) {
			throw new FileNotFoundException();
		}
		this.register = new LinkedHashMap<>();
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
		Document document = documentBuilder.parse(localStorage);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("*");
		for (int i = 0; i < nList.getLength(); i++) {
			IWriteRead writeRead = register.get(nList.item(i).getParentNode().getNodeName());
			if (writeRead != null) {
				writeRead.read(nList.item(i));
			}
		}
	}

	@Override public void write() throws ParserConfigurationException, TransformerException {
		for (IWriteRead writeRead : register.values()) {
			writeRead.write();
		}
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

	@Override public void register(String identifier, IWriteRead writeRead) {
		register.put(identifier, writeRead);
	}
}
