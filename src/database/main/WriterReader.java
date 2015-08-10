package database.main;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
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

import database.plugin.Extendable;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.Writeable;

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

	public void write() throws ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		document.setXmlStandalone(true);
		document.setXmlVersion("1.0");
		Element database = document.createElement("database");
		document.appendChild(database);
		Element plugin = document.createElement("plugin");
		database.appendChild(plugin);
		for (Plugin currentPlugin : pluginContainer.getPlugins()) {
			if (currentPlugin instanceof Writeable || currentPlugin instanceof InstancePlugin) {
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
						if (instancePlugin instanceof Extendable) {
							Extendable extendable = (Extendable) instancePlugin;
							for (InstancePlugin extention : extendable.getExtentions()) {
								if (extention.getList().contains(instance)) {
									parameter = document.createElement(extention.getIdentity());
								}
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
				else {
					Writeable writeable = (Writeable) currentPlugin;
					Element writeablePlugin = document.createElement(currentPlugin.getIdentity());
					for (String string : writeable.write()) {
						Element entry = document.createElement("entry");
						entry.setAttribute("string", string);
						writeablePlugin.appendChild(entry);
					}
					plugin.appendChild(writeablePlugin);
				}
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
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("*");
			for (int i = 0; i < nList.getLength(); i++) {
				Plugin plugin = pluginContainer.getPlugin(nList.item(i).getParentNode().getNodeName());
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
		catch (Throwable e) {
			String stackTrace = "";
			for (StackTraceElement element : e.getStackTrace()) {
				stackTrace = stackTrace + "\r\n" + element;
			}
			JOptionPane.showMessageDialog(null, e.getClass().toString() + stackTrace, "storage reset", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
