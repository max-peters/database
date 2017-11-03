package database.main.userInterface;

public class OutputInformation {
	private Object output;
	private StringFormat stringFormat;
	private OutputType stringType;

	public OutputInformation(Object output, OutputType stringType, StringFormat stringFormat) {
		this.output = output;
		this.stringFormat = stringFormat;
		this.stringType = stringType;
	}

	@Override
	public boolean equals(Object object) {
		OutputInformation information;
		if (object != null && object instanceof OutputInformation) {
			information = (OutputInformation) object;
			if (output.equals(information.getOutput()) && stringFormat.equals(information.getStringFormat())
					&& stringType.equals(information.getStringType())) {
				return true;
			}
		}
		return false;
	}

	public Object getOutput() {
		return output;
	}

	public StringFormat getStringFormat() {
		return stringFormat;
	}

	public OutputType getStringType() {
		return stringType;
	}
}
