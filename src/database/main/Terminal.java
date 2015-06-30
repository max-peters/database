package database.main;

public class Terminal {
	private GraphicalUserInterface	graphicalUserInterface;

	public Terminal(GraphicalUserInterface graphicalUserInterface) {
		this.graphicalUserInterface = graphicalUserInterface;
	}

	public String in() throws InterruptedException {
		return graphicalUserInterface.in();
	}

	public void out(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printLine(splitResult[i]);
			}
		}
	}

	public void requestOut(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printRequest(splitResult[i]);
			}
		}
	}

	public void solutionOut(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printSolution(splitResult[i]);
			}
		}
	}
}
