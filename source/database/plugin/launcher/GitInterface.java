package database.plugin.launcher;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitInterface {
	Git	database;
	Git	studium;
	Git	mail;

	public GitInterface() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		database = new Git(builder.setGitDir(new File("C:\\Dateien\\Workspace\\Eclipse\\database\\.git")).readEnvironment().findGitDir().build());
		mail = new Git(builder.setGitDir(new File("C:\\Users\\Max\\AppData\\Roaming\\Thunderbird\\Profiles\\mail\\Mail\\Local Folders\\.git")).readEnvironment().findGitDir().build());
		studium = new Git(builder.setGitDir(new File("C:\\Users\\Max\\Desktop\\studium\\.git")).readEnvironment().findGitDir().build());
	}

	public void push() throws IOException, GitAPIException {
		push(database);
		System.out.println("databse pushed");
		for (String s : studium.status().call().getModified()) {
			System.out.println(s);
		}
		// push(studium);
		// System.out.println("studium pushed");
		// push(mail);
		// System.out.println("mail pushed");
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
