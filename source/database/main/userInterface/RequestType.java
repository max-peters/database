package database.main.userInterface;

public class RequestType {
	public static final String BOOLEAN = "(true|false)";
	public static final String DATE = "DATE";
	public static final String DOUBLE = "(\\-)?[0-9]{1,13}(\\.[0-9]{1,2})?";
	public static final String EXECUTIONDAY = "(first|mid|last)";
	public static final String INTEGER = "(\\-)?[0-9]{1,8}";
	public static final String NAME = "[A-Za-z\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,:][A-Za-z\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,: ]*";
	public static final String NAME_NUMBER = "[A-Za-z0-9\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,:()][A-Za-z0-9\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,:() ]*";
	public static final String NAME_NUMBER_EMPTY = "([A-Za-z0-9\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,:()][A-Za-z0-9\u00C4\u00E4\u00D6\u00F6\u00DC\u00FC\u00DF\\-\\.,:() ]*|)";
	public static final String TIME = "TIME";
}