package database.plugin.launcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	public Launcher() {
		super("launcher", null);
	}

	@Command(tag = "pull") public void pull() throws IOException {
		String[] command = { "git", "push", "--force" };
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File("C:\\Dateien\\Workspace\\Eclipse\\database"));
			// String s = null;
			// Charset charset = Charset.defaultCharset();
			// BufferedReader stdInput;
			Process proc;
			pb.command(command);
			proc = pb.start();
			PrintWriter printWriter = new PrintWriter(proc.getOutputStream());
			printWriter.println("***REMOVED***");
			// stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), charset));
			//
			// while ((s = stdInput.readLine()) != null) {
			// System.out.println(s);
			// }
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