package database.main;

import java.io.File;
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

import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.event.allDayEvent.AllDayEvent;
import database.plugin.event.birthday.Birthday;

public class WriterReader {
	private String		storageDirectory;
	private Store		store;
	private File		file;
	private Document	document;

	public WriterReader(Store store) {
		this.store = store;
		this.storageDirectory = "Z:/storage.xml";
		this.file = new File(storageDirectory);
	}

	public boolean checkDirectory() {
		return file.exists();
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
				InstancePlugin instancePlugin = (InstancePlugin) currentPlugin;
				Element currentElement = null;
				if (document.getElementsByTagName(instancePlugin.getIdentity()).getLength() == 1) {
					currentElement = (Element) document.getElementsByTagName(currentPlugin.getIdentity()).item(0);
				}
				else {
					currentElement = document.createElement(currentPlugin.getIdentity());
					plugin.appendChild(currentElement);
				}
				for (Instance instance : instancePlugin.getList()) {
					Element parameter = null;
					if (instancePlugin.getIdentity().equals("event")) {
						if (instance instanceof AllDayEvent) {
							parameter = document.createElement("allDayEvent");
						}
						else if (instance instanceof Birthday) {
							parameter = document.createElement("birthday");
						}
					}
					else {
						parameter = document.createElement("entry");
					}
					currentElement.appendChild(parameter);
					for (int i = 0; i < instance.getParameter().length; i++) {
						parameter.setAttribute(instance.getParameter()[i][0], instance.getParameter()[i][1]);
					}
				}
				Element display = document.createElement("display");
				display.setAttribute("boolean", String.valueOf(instancePlugin.getDisplay()));
				currentElement.appendChild(display);
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

	public void read() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("*");
		for (int i = 0; i < nList.getLength(); i++) {
			Plugin plugin = store.getPlugin(nList.item(i).getParentNode().getNodeName());
			if (plugin != null && plugin instanceof InstancePlugin) {
				if (nList.item(i).getNodeName().equals("entry")) {
					String[][] parameter = new String[nList.item(i).getAttributes().getLength()][2];
					for (int j = 0; j < nList.item(i).getAttributes().getLength(); j++) {
						parameter[j][0] = nList.item(i).getAttributes().item(j).getNodeName();
						parameter[j][1] = nList.item(i).getAttributes().item(j).getNodeValue();
					}
					((InstancePlugin) plugin).create(parameter);
				}
				else if (nList.item(i).getNodeName().equals("display")) {
					if (plugin != null && plugin instanceof InstancePlugin) {
						((InstancePlugin) plugin).setDisplay(Boolean.valueOf(nList.item(i).getAttributes().item(0).getNodeValue()));
					}
				}
				else {
					int j;
					String[][] parameter = new String[nList.item(i).getAttributes().getLength() + 1][2];
					for (j = 0; j < nList.item(i).getAttributes().getLength(); j++) {
						parameter[j][0] = nList.item(i).getAttributes().item(j).getNodeName();
						parameter[j][1] = nList.item(i).getAttributes().item(j).getNodeValue();
					}
					parameter[j][0] = "type";
					parameter[j][1] = nList.item(i).getNodeName();
					((InstancePlugin) plugin).create(parameter);
				}
			}
		}
	}
}
// public void write() throws IOException {
// FileWriter writer;
// writer = new FileWriter(storageDirectory);
// for (Object current : store.getListToWrite()) {
// writer.write(current.toString() + "\r\n");
// }
// writer.close();
// writer = new FileWriter(oldDirectory);
// for (Object current : store.getStorage()) {
// writer.write(current.toString() + "\r\n");
// }
// writer.close();
// }
// public void read() throws IOException {
// Scanner scanner = new Scanner(new FileInputStream(inputFile), "UTF-8");
// while (scanner.hasNextLine()) {
// String line = scanner.nextLine();
// String splitResult[] = line.split(" : ");
// String parameters[] = splitResult[1].split(" / ");
// Plugin plugin = store.getPlugin(splitResult[0]);
// if (plugin != null && plugin instanceof InstancePlugin) {
// ((InstancePlugin) plugin).create(parameters);
// }
// else if (splitResult[0].equals("boolean")) {
// plugin = store.getPlugin(parameters[0]);
// if (plugin != null && plugin instanceof InstancePlugin) {
// ((InstancePlugin) plugin).setDisplay(Boolean.valueOf(parameters[1]));
// }
// }
// else {
// scanner.close();
// throw new IOException("invalid line: '" + line + "'");
// }
// }
// scanner.close();
// scanner = new Scanner(new FileInputStream(new File(oldDirectory)), "UTF-8");
// while (scanner.hasNextLine()) {
// String line = scanner.nextLine();
// store.addToStorage(line);
// }
// scanner.close();
// }