package database.main.userInterface;

import java.util.Collection;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.FormatterProvider;

public interface ITerminal {
	public void blockInput();

	public int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException;

	public void collectLine(Object output, StringFormat stringFormat, String headline);

	public void errorMessage() throws BadLocationException, InterruptedException;

	public void getLineOfCharacters(char character, StringType stringType) throws BadLocationException;

	public void printCollectedLines() throws InterruptedException, BadLocationException;

	public void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException;

	public default String request(String printOut, String regex) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, 0);
	}

	public default String request(String printOut, String regex, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", completeable, 0);
	}

	public default String request(String printOut, String regex, int levenshteinDistance) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, levenshteinDistance);
	}

	public default String request(String printOut, String regex, String inputText) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, null, 0);
	}

	public default String request(String printOut, String regex, String inputText, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, completeable, 0);
	}

	public String request(String printOut, String regex, String inputText, Completeable completeable, int levenshteinDistance) throws InterruptedException, BadLocationException;

	public void update(PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException;

	public void waitForInput() throws InterruptedException;
}
