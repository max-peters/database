package database.plugin.launcher;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitInterface {
	public void push() throws IOException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("C:\\Dateien\\Workspace\\Eclipse\\database\\.git")).readEnvironment().findGitDir().build();
		Git git = new Git(repository);
		git.add().addFilepattern(".").call();
		git.commit().setMessage("update").call();
		System.out.println(git.push().setForce(true).setRemote("server").getRemote());
		repository.close();
		git.close();
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
