package database.main;

public enum Category {
	SUBJECT, REFILLING, BIRTHDAY, EXPENSE, DATE, TASK;
	public String toString() {
		String stringRepresentation = null;
		if (this.equals(SUBJECT)) {
			stringRepresentation = "subject";
		}
		else if (this.equals(REFILLING)) {
			stringRepresentation = "refilling";
		}
		else if (this.equals(BIRTHDAY)) {
			stringRepresentation = "birthday";
		}
		else if (this.equals(EXPENSE)) {
			stringRepresentation = "expense";
		}
		else if (this.equals(DATE)) {
			stringRepresentation = "date";
		}
		else if (this.equals(TASK)) {
			stringRepresentation = "task";
		}
		return stringRepresentation;
	}
}
