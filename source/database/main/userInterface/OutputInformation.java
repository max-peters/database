package database.main.userInterface;

public class OutputInformation {
	private Object			output;
	private StringFormat	stringFormat;
	private StringType		stringType;

	public OutputInformation(Object output, StringType stringType, StringFormat stringFormat) {
		this.output = output;
		this.stringFormat = stringFormat;
		this.stringType = stringType;
	}

	public Object getOutput() {
		return output;
	}

	public StringFormat getStringFormat() {
		return stringFormat;
	}

	public StringType getStringType() {
		return stringType;
	}
}
