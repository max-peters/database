package database.plugin.launcher;

import java.io.IOException;

import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	public Launcher() {
		super("launcher", null);
	}

	@Command(tag = "pull") public void pull() throws IOException {
		GitInterface git = new GitInterface();
		git.push();
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