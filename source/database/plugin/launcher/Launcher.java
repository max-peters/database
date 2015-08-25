package database.plugin.launcher;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	GitInterface	git;

	public Launcher() throws IOException {
		super("launcher", null);
		git = new GitInterface();
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException {
		git.push();
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException {
		git.pull();
	}

	@Override public void conduct(String command) throws InterruptedException, IOException, GitAPIException {
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