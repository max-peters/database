package database.main;

import java.util.ArrayList;
import java.util.List;

public class Terminal {
	private static GraphicalUserInterface	graphicalUserInterface;
	private static List<String>				list	= new ArrayList<String>();

	private Terminal() {
	}

	public static void setGraphicalUserInterface(GraphicalUserInterface graphicalUserInterface) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
	}

	public static String in() throws InterruptedException {
		return graphicalUserInterface.in();
	}

	public static void out(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printLine(splitResult[i]);
			}
		}
	}

	public static void requestOut(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printRequest(splitResult[i]);
			}
		}
	}

	public static void solutionOut(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				graphicalUserInterface.printSolution(splitResult[i]);
			}
		}
	}

	public static void collectStartInformation(String output) {
		if (output != null && output.length() != 0) {
			String splitResult[] = output.split("\r\n");
			for (int i = 0; i < splitResult.length; i++) {
				list.add(splitResult[i]);
			}
		}
	}

	public static void startOut() throws InterruptedException {
		for (String string : list) {
			graphicalUserInterface.printRequest(string);
		}
		if (!list.isEmpty()) {
			graphicalUserInterface.waitForInput();
		}
	}
}
