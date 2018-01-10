package database.main.userInterface;

public class OutputInformation {
	private Object output;
	private OutputType outputType;
	private StringFormat stringFormat;

	public OutputInformation(Object output, OutputType outputType, StringFormat stringFormat) {
		this.output = output;
		this.stringFormat = stringFormat;
		this.outputType = outputType;
	}

	@Override
	public boolean equals(Object object) {
		OutputInformation information;
		if (object != null && object instanceof OutputInformation) {
			information = (OutputInformation) object;
			if (output.equals(information.getOutput()) && stringFormat.equals(information.getStringFormat())
					&& outputType.equals(information.getOutputType())) {
				return true;
			}
		}
		return false;
	}

	public Object getOutput() {
		return output;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public StringFormat getStringFormat() {
		return stringFormat;
	}
}
