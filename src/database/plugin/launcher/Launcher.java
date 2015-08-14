package database.plugin.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	public Launcher() {
		super("launcher", null);
	}

	@Command(tag = "pull") public void pull() throws IOException {
		//
		//
		//
		// IF %LOCAL% == %REMOTE% (
		// echo databse up-to-date
		// ) ELSE (
		// IF %REMOTE% == %BASE% (
		// git push --force
		// )
		// )
		//
		// String[][] commands = { { "cd", "C:\\Dateien\\Workspace\\Eclipse\\database" }, { "git", "add -A" }, { "git", "commit -m \"update\"" },
		// { "for /f %%i in ('git rev-parse @') do set LOCAL=%%i" }, { "for /f %%i in ('git rev-parse @{u}') do set REMOTE=%%i" }, {
		// "for /f %%i in ('git merge-base @ @{u}') do set BASE=%%i" },
		// { "IF %LOCAL% == %REMOTE% (echo databse up-to-date) ELSE (IF %REMOTE% == %BASE% (git push --force))" } };
		String[][] commands = { { "git", "add", "-A" }, { "git", "commit", "-m \"update\"" }, { "git", "push", "--force", "***REMOVED***" }, { "git", "status" } };
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File("C:\\Dateien\\Workspace\\Eclipse\\database"));
			String s = null;
			Charset charset = Charset.defaultCharset();
			BufferedReader stdInput;
			Process proc;
			for (String[] command : commands) {
				pb.command(command);
				proc = pb.start();
				stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream(), charset));
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
			}
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