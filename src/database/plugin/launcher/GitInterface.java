package database.plugin.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GitInterface {
	private char[]	password	= new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };

	public void push() {
		try {
			List<String> sb = new ArrayList<String>();
			sb.add("git");
			sb.add("push");
			ProcessBuilder pb = new ProcessBuilder(sb);
			final Process p = pb.start();
			final BufferedReader stdinReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			final BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			new Thread(new Runnable() {
				@Override public void run() {
					try {
						OutputStreamWriter s = new OutputStreamWriter(p.getOutputStream());
						s.write(password);
						s.write(System.getProperty("line.separator"));
						s.flush();
					}
					catch (IOException e) {
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
					}
				}
			}).start();
			p.waitFor();
		}
		catch (Exception e) {
		}
	}
}
