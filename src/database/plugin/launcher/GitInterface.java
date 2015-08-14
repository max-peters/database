package database.plugin.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GitInterface {
	private char[]	password	= new char[] { 'v', 'f', 'r', '4', 'd', 'b', '2' };

	public void push() {
		try {
			String[] command = { "git", "push" };
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File("C:\\Dateien\\Workspace\\Eclipse\\database"));
			final Process p = pb.start();
			final BufferedReader stdinReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			final BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			Thread.sleep(100);
			new Thread(new Runnable() {
				@Override public void run() {
					try {
						OutputStreamWriter s = new OutputStreamWriter(p.getOutputStream());
						s.write(password);
						s.write(System.getProperty("line.separator"));
						s.flush();
					}
					catch (IOException e) {
						System.out.println("error");
					}
				}
			}).start();
			new Thread(new Runnable() {
				@Override public void run() {
					try {
						String s;
						while ((s = stdinReader.readLine()) != null) {
							System.out.println(s);
						}
					}
					catch (IOException e) {
						System.out.println("error");
					}
				}
			}).start();
			new Thread(new Runnable() {
				@Override public void run() {
					try {
						String s;
						while ((s = stderrReader.readLine()) != null) {
							System.out.println(s);
						}
					}
					catch (IOException e) {
						System.out.println("error");
					}
				}
			}).start();
			p.waitFor();
		}
		catch (Exception e) {
			System.out.println("error");
		}
	}
}
