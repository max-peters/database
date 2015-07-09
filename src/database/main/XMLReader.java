package database.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class XMLReader {
	Store		store;
	Document	document;

	public XMLReader(Store store) {
		this.store = store;
	}

	public void initialise() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		document = documentBuilder.newDocument();
		document.setXmlStandalone(true);
		document.setXmlVersion("1.0");
	}

	public void write() throws ParserConfigurationException, TransformerException, IOException {
		Element database = document.createElement("database");
		document.appendChild(database);
		Element plugin = document.createElement("plugin");
		database.appendChild(plugin);
		for (Plugin currentPlugin : store.getPlugins()) {
			if (currentPlugin instanceof InstancePlugin) {
				if (currentPlugin.getIdentity().equals("event")) {
					System.out.println(((InstancePlugin) currentPlugin).getList());
				}
				for (Instance instance : ((InstancePlugin) currentPlugin).getList()) {
					Element currentElement;
					if (document.getElementsByTagName(currentPlugin.getIdentity()).getLength() == 1) {
						currentElement = (Element) document.getElementsByTagName(currentPlugin.getIdentity()).item(0);
					}
					else {
						currentElement = document.createElement(currentPlugin.getIdentity());
						plugin.appendChild(currentElement);
					}
					Element parameter = document.createElement("entry");
					currentElement.appendChild(parameter);
					for (int i = 0; i < instance.getParameter().length; i++) {
						// parameter.setAttribute(instance.getParameter()[i][0], instance.getParameter()[i][1]);
					}
				}
			}
		}
		// DOMSource domSource = new DOMSource(document);
		// File fileOutput = new File("storage.xml");
		// StreamResult streamResult = new StreamResult(fileOutput);
		// TransformerFactory tf = TransformerFactory.newInstance();
		// Transformer serializer = tf.newTransformer();
		// serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		// serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		// serializer.transform(domSource, streamResult);
	}

	public void read() throws SAXException, IOException, ParserConfigurationException {
		File fXmlFile = new File("storage.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("entry");
		for (int i = 0; i < nList.getLength(); i++) {
			Plugin plugin = store.getPlugin(nList.item(i).getParentNode().getNodeName());
			if (plugin != null && plugin instanceof InstancePlugin) {
				String[][] parameter = new String[nList.item(i).getAttributes().getLength()][2];
				for (int j = 0; j < nList.item(i).getAttributes().getLength(); j++) {
					parameter[j][0] = nList.item(i).getAttributes().item(j).getNodeName();
					parameter[j][1] = nList.item(i).getAttributes().item(j).getNodeValue();
				}
				((InstancePlugin) plugin).create(parameter);
			}
		}
	}
}
