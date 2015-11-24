package database.main.GraphicalUserInterface;

public enum StringStyle {
	REQUEST, MAIN;
	private int lineNumber = 0;

	public void setLineNumber(int lineNumber) {
		switch (this) {
			case REQUEST:
			case MAIN:
		}
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
