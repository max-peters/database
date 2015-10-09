package database.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

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
import database.plugin.RequestInformation;

public class WriterReader {
	private String			storageDirectory;
	private PluginContainer	pluginContainer;
	private File			file;

	public WriterReader(PluginContainer pluginContainer) {
		this.pluginContainer = pluginContainer;
		this.storageDirectory = "Z:/storage.xml";
		this.file = new File(storageDirectory);
	}

	public boolean checkDirectory() {
		return file.exists();
	}

	public void setDirectory() {
		file = new File("storage.xml");
	}

	public void write() throws ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element database = document.createElement("database");
		document.appendChild(database);
		for (Plugin currentPlugin : pluginContainer.getPlugins()) {
			List<RequestInformation> list = currentPlugin.getInformationList();
			if (list != null && !list.isEmpty()) {
				Element element = document.createElement(currentPlugin.getIdentity());
				for (RequestInformation pair : list) {
					Element entryElement = document.createElement(pair.getName());
					for (Entry<String, String> entry : pair.getMap().entrySet()) {
						entryElement.setAttribute(entry.getKey(), entry.getValue());
						element.appendChild(entryElement);
					}
				}
				database.appendChild(element);
			}
		}
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(file);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.transform(domSource, streamResult);
	}

	public void read() throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("*");
		for (int i = 0; i < nList.getLength(); i++) {
			Plugin plugin = pluginContainer.getPlugin(nList.item(i).getParentNode().getNodeName());
			if (plugin != null) {
				RequestInformation pair = new RequestInformation(nList.item(i).getNodeName());
				for (int j = 0; j < nList.item(i).getAttributes().getLength(); j++) {
					pair.put(nList.item(i).getAttributes().item(j).getNodeName(), nList.item(i).getAttributes().item(j).getNodeValue());
				}
				plugin.readInformation(pair);
			}
		}
	}
}
