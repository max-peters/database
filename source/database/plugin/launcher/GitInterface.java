package database.plugin.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitInterface {
	private List<Git>	repositories;

	public GitInterface() throws IOException {
		repositories = new ArrayList<Git>();
		initialise("C:\\Dateien\\Workspace\\Eclipse\\database\\.git");
		initialise("C:\\Users\\Max\\AppData\\Roaming\\Thunderbird\\Profiles\\mail\\Mail\\Local Folders\\.git");
		initialise("C:\\Users\\Max\\Desktop\\Studium\\.git");
	}

	public void push() throws IOException, GitAPIException {
		for (Git git : repositories) {
			push(git);
		}
	}

	public void pull() throws IOException, GitAPIException {
		for (Git git : repositories) {
			pull(git);
		}
	}

	private void initialise(String repository) throws IOException {
		if (new File(repository).exists()) {
			repositories.add(new Git(new FileRepositoryBuilder().setGitDir(new File(repository)).readEnvironment().findGitDir().build()));
		}
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
		if (!git.status().call().isClean()) {
			git.fetch().setRemote("server").setCredentialsProvider(new UsernamePasswordCredentialsProvider("maxptrs@git.hidrive.strato.com", "***REMOVED***")).call();
			git.reset().setMode(ResetCommand.ResetType.HARD).setRef("server/master").call();
			git.clean().setCleanDirectories(true).call();
		}
	}
}
