package database.main.userInterface;

public class RequestRegexPattern {
	public static final String	DATE			= "DATE";
	public static final String	TIME			= "TIME";
	public static final String	NAME			= "[A-ZÖÄÜa-zöäüß\\-\\. ]+";
	public static final String	NAME_NUMBER		= "[A-ZÖÄÜa-zöäüß0-9\\-\\. ]+";
	public static final String	INTEGER			= "[0-9]{1,8}";
	public static final String	DOUBLE			= "[0-9]{1,13}(\\.[0-9]{1,2})?";
	public static final String	EXECUTIONDAY	= "(first|mid|last)";
}
