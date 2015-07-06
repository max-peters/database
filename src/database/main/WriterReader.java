package database.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class WriterReader {
	private String	storageDirectory;
	private String	oldDirectory;
	private Store	store;
	private File	inputFile;

	public WriterReader(Store store) {
		this.store = store;
		this.storageDirectory = "Z:/storage.txt";
		this.oldDirectory = "Z:/old.txt";
		inputFile = new File(storageDirectory);
	}

	public boolean checkDirectory() {
		return inputFile.exists();
	}

	public void write() throws IOException {
		FileWriter writer;
		writer = new FileWriter(storageDirectory);
		for (Object current : store.getListToWrite()) {
			writer.write(current.toString() + "\r\n");
		}
		writer.close();
		writer = new FileWriter(oldDirectory);
		for (Object current : store.getStorage()) {
			writer.write(current.toString() + "\r\n");
		}
		writer.close();
	}

	public void read() throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(inputFile), "UTF-8");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String splitResult[] = line.split(" : ");
			String parameters[] = splitResult[1].split(" / ");
			if (splitResult[0].matches(store.getNameTagsAsRegex())) {
				store.getPlugin(splitResult[0]).create(parameters);
			}
			else if (splitResult[0].equals("boolean")) {
				store.getPlugin(parameters[0]).setDisplay(Boolean.valueOf(parameters[1]));
			}
		}
		scanner.close();
		scanner = new Scanner(new FileInputStream(new File(oldDirectory)), "UTF-8");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			store.addToStorage(line);
		}
		scanner.close();
	}
}