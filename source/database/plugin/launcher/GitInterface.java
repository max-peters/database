package database.plugin.launcher;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitInterface {
	private Git	database;
	private Git	studium;
	private Git	mail;

	public GitInterface() throws IOException {
		database = initialise("C:\\Dateien\\Workspace\\Eclipse\\database\\.git");
		mail = initialise("C:\\Users\\Max\\AppData\\Roaming\\Thunderbird\\Profiles\\mail\\Mail\\Local Folders\\.git");
		studium = initialise("C:\\Users\\Max\\Desktop\\Studium\\.git");
	}

	public void push() throws IOException, GitAPIException {
		push(database);
		push(studium);
		push(mail);
	}

	public void pull() throws IOException, GitAPIException {
		long time = System.currentTimeMillis();
		System.out.println(System.currentTimeMillis());
		pull(database);
		pull(studium);
		pull(mail);
		System.out.println(time - System.currentTimeMillis());
	}

	private Git initialise(String repository) throws IOException {
		return new Git(new FileRepositoryBuilder().setGitDir(new File(repository)).readEnvironment().findGitDir().build());
	}

	private void push(Git git) throws NoFilepatternException, GitAPIException {
		if (git.status().call().hasUncommittedChanges()) {
			git.add().addFilepattern(".").call();
			git.commit().setMessage("update").call();
			git.push().setForce(true).setRemote("server").setCredentialsProvider(new UsernamePasswordCredentialsProvider("maxptrs@git.hidrive.strato.com", "***REMOVED***")).call();
			git.getRepository().close();
			git.close();
		}
	}

	private void pull(Git git) throws InvalidRemoteException, TransportException, GitAPIException {
		git.fetch().setRemote("server").setCredentialsProvider(new UsernamePasswordCredentialsProvider("maxptrs@git.hidrive.strato.com", "***REMOVED***")).call();
		git.reset().setMode(ResetCommand.ResetType.HARD).setRef("server/master").call();
		git.clean().setCleanDirectories(true).call();
	}
}
// public void push() {private char[] password = new char[] { 'v', 'f', 'r', '4', 'd', 'b', '2' };
// try {
// String[] command = { "git", "push" };
// ProcessBuilder pb = new ProcessBuilder(command);
// pb.directory(new File("C:\\Dateien\\Workspace\\Eclipse\\database"));
// final Process p = pb.start();
// final BufferedReader stdinReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
// final BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
// new Thread(new Runnable() {
// @Override public void run() {
// try {
// OutputStreamWriter s = new OutputStreamWriter(p.getOutputStream());
// s.write(password);
// s.write(System.getProperty("line.separator"));
// s.flush();
// }
// catch (IOException e) {
// System.out.println(e.getMessage());
// }
// }
// }).start();
// new Thread(new Runnable() {
// @Override public void run() {
// try {
// String s;
// while ((s = stdinReader.readLine()) != null) {
// System.out.println(s);
// }
// }
// catch (IOException e) {
// System.out.println(e.getMessage());
// }
// }
// }).start();
// new Thread(new Runnable() {
// @Override public void run() {
// try {
// String s;
// while ((s = stderrReader.readLine()) != null) {
// System.out.println(s);
// }
// }
// catch (IOException e) {
// System.out.println(e.getMessage());
// }
// }
// }).start();
// p.waitFor();
// }
// catch (Exception e) {
// System.out.println(e.getMessage());
// }
// }
