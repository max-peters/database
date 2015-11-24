package database.main.GraphicalUserInterface;

public enum StringType {
	STANDARD, BOLD;
	public String toString() {
		String type = null;
		switch (this) {
			case STANDARD:
				type = "standard";
				break;
			case BOLD:
				type = "bold";
				break;
		}
		return type;
	}
}
