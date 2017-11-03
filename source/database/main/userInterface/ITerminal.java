package database.main.userInterface;

import java.sql.SQLException;
import java.util.Collection;

import javax.swing.text.BadLocationException;

import database.main.UserCancelException;
import database.services.stringComplete.IStringComplete;

public interface ITerminal {
	public void blockInput();

	public int checkRequest(Collection<String> collection, int initialPosition, String request)
			throws InterruptedException, BadLocationException;

	public default int checkRequest(Collection<String> collection, String request)
			throws InterruptedException, BadLocationException {
		return checkRequest(collection, -1, request);
	}

	public default void newLine(StringType stringType) throws BadLocationException {
		printLine(System.getProperty("line.separator"), stringType, StringFormat.STANDARD);
	}

	public void collectLine(Object output, StringFormat stringFormat, String headline);

	public void errorMessage() throws BadLocationException, InterruptedException;

	public void getLineOfCharacters(char character, StringType stringType) throws BadLocationException;

	public void printCollectedLines() throws InterruptedException, BadLocationException;

	public void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException;

	public default String request(String printOut, String regex)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, "", null, 0);
	}

	public default String request(String printOut, String regex, int levenshteinDistance)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, "", null, levenshteinDistance);
	}

	public default String request(String printOut, String regex, IStringComplete stringComplete)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, "", stringComplete, 0);
	}

	public default String request(String printOut, String regex, IStringComplete stringComplete,
			int levenshteinDistance)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, "", stringComplete, levenshteinDistance);
	}

	public default String request(String printOut, String regex, String inputText)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, inputText, null, 0);
	}

	public default String request(String printOut, String regex, String inputText, IStringComplete stringComplete)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		return request(printOut, regex, inputText, stringComplete, 0);
	}

	public String request(String printOut, String regex, String inputText, IStringComplete stringComplete,
			int levenshteinDistance)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException;

	public void update() throws BadLocationException, InterruptedException, SQLException;

	public void waitForInput() throws InterruptedException;
}
