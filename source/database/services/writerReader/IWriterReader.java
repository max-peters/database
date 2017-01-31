package database.services.writerReader;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public interface IWriterReader {
	public void add(String nodeName, String leaveName, String content);

	public void read() throws ParserConfigurationException, SAXException, IOException;

	public void register(String identifier, IWriteRead writeRead);

	public void write() throws ParserConfigurationException, TransformerException;
}
