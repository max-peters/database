package database.main.userInterface;

public class RequestType {
	public static final String	BOOLEAN				= "(true|false)";
	public static final String	DATE				= "DATE";
	public static final String	DOUBLE				= "[0-9]{1,13}(\\.[0-9]{1,2})?";
	public static final String	EXECUTIONDAY		= "(first|mid|last)";
	public static final String	INTEGER				= "[0-9]{1,8}";
	public static final String	NAME				= "[A-Za-zÖöÄäÜüß\\-\\.:][A-Za-zÖöÄäÜüß\\-\\.: ]*";
	public static final String	NAME_NUMBER			= "[A-Za-zÖöÄäÜüß0-9\\-\\.:()][A-Za-zÖöÄäÜüß0-9\\-\\.:() ]*";
	public static final String	NAME_NUMBER_EMPTY	= "([A-Za-zÖöÄäÜüß0-9\\-\\.:()][A-Za-zÖöÄäÜüß0-9\\-\\.:() ]*|)";
	public static final String	TIME				= "TIME";
}
