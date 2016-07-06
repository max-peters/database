package test;

import java.util.Collection;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.Completeable;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.FormatterProvider;

public class TestTerminal implements ITerminal {
	public TestTerminal() {
		// TODO Auto-generated constructor stub
	}

	@Override public void blockInput() {
		// TODO Auto-generated method stub
	}

	@Override public int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override public void collectLine(Object output, StringFormat stringFormat, String headline) {
		// TODO Auto-generated method stub
	}

	@Override public void errorMessage() throws BadLocationException, InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override public void getLineOfCharacters(char character, StringType stringType) throws BadLocationException {
		// TODO Auto-generated method stub
	}

	@Override public void printCollectedLines() throws InterruptedException, BadLocationException {
		// TODO Auto-generated method stub
	}

	@Override public void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		// TODO Auto-generated method stub
	}

	@Override public String request(String printOut, String regex, String inputText, Completeable completeable, int levenshteinDistance)	throws InterruptedException,
																																			BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override public void update(PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override public void waitForInput() throws InterruptedException {
		// TODO Auto-generated method stub
	}
}
