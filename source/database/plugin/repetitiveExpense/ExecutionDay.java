package database.plugin.repetitiveExpense;

public enum ExecutionDay {
	FIRST, LAST, MID;
	@Override public String toString() {
		String day = null;
		switch (this) {
			case FIRST:
				day = "first";
				break;
			case LAST:
				day = "last";
				break;
			case MID:
				day = "mid";
				break;
		}
		return day;
	}

	public static ExecutionDay getExecutionDay(String string) {
		ExecutionDay day = null;
		switch (string) {
			case "first":
				day = ExecutionDay.FIRST;
				break;
			case "last":
				day = ExecutionDay.LAST;
				break;
			case "mid":
				day = ExecutionDay.MID;
		}
		return day;
	}
}
