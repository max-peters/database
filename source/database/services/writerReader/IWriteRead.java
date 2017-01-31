package database.services.writerReader;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface IWriteRead {
	public void read(Node node) throws ParserConfigurationException, DOMException;

	public void write();
}
