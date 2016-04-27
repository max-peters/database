package database.main.userInterface.autocompletion;

import database.main.userInterface.Terminal;

public class Autocompletion {
	private Completeable completeable;

	public Autocompletion(Completeable completeable) {
		this.completeable = completeable;
	}

	public String getLine() throws InterruptedException {
		String input = null;
		Terminal.resetLastKey();
		while (Terminal.getLastKey() != 10) {
			input = Terminal.readKey();
			Terminal.setInputText(completeable.getNewInput(input));
		}
		return input;
	}
}
