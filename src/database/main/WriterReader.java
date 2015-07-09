package database.main;

import java.io.File;
import java.io.IOException;

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
		// FileWriter writer;
		// writer = new FileWriter(storageDirectory);
		// for (Object current : store.getListToWrite()) {
		// writer.write(current.toString() + "\r\n");
		// }
		// writer.close();
		// writer = new FileWriter(oldDirectory);
		// for (Object current : store.getStorage()) {
		// writer.write(current.toString() + "\r\n");
		// }
		// writer.close();
	}

	public void read() throws IOException {
		// Scanner scanner = new Scanner(new FileInputStream(inputFile), "UTF-8");
		// while (scanner.hasNextLine()) {
		// String line = scanner.nextLine();
		// String splitResult[] = line.split(" : ");
		// String parameters[] = splitResult[1].split(" / ");
		// Plugin plugin = store.getPlugin(splitResult[0]);
		// if (plugin != null && plugin instanceof InstancePlugin) {
		// ((InstancePlugin) plugin).create(parameters);
		// }
		// else if (splitResult[0].equals("boolean")) {
		// plugin = store.getPlugin(parameters[0]);
		// if (plugin != null && plugin instanceof InstancePlugin) {
		// ((InstancePlugin) plugin).setDisplay(Boolean.valueOf(parameters[1]));
		// }
		// }
		// else {
		// scanner.close();
		// throw new IOException("invalid line: '" + line + "'");
		// }
		// }
		// scanner.close();
		// scanner = new Scanner(new FileInputStream(new File(oldDirectory)), "UTF-8");
		// while (scanner.hasNextLine()) {
		// String line = scanner.nextLine();
		// store.addToStorage(line);
		// }
		// scanner.close();
	}
}