package database.plugin.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	public Launcher() {
		super("launcher", null);
	}

	@Command(tag = "pull") public void pull() throws IOException {
		String[] command = { "git", "push" };
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File("C:\\Dateien\\Workspace\\Eclipse\\database"));
			pb.redirectErrorStream(true);
			Process process = pb.start();
			System.out.println("reading");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			bw.write("***REMOVED***");
		}
		catch (Exception ex) {
			System.out.println("Hi");
			System.out.println(ex.getMessage());
		}
		System.exit(0);
		// Runtime.getRuntime().exec("C:/WINDOWS/system32/cmd.exe /c start Z:/pull.bat");
	}

	@Command(tag = "push") public void push() throws IOException {
		Runtime.getRuntime().exec("C:/WINDOWS/system32/cmd.exe /c start Z:/push.bat");
	}

	@Override public void conduct(String command) throws InterruptedException, IOException {
		switch (command) {
			case "pull":
				pull();
				break;
			case "push":
				push();
				break;
		}
	}
}