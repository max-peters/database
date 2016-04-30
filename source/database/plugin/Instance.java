package database.plugin;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	public abstract void insertParameter(Element element);

	@Override public String toString() {
		String string = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {}
		Document document = dBuilder.newDocument();
		Element element = document.createElement("element");
		insertParameter(element);
		for (int i = 0; i < element.getAttributes().getLength(); i++) {
			string += element.getAttributes().item(i);
			if (i != element.getAttributes().getLength() - 1) {
				string += " ";
			}
		}
		return string;
	}
}