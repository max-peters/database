package database.services.undoRedo;

import database.plugin.Instance;

public class NewCommand implements RevertableCommand {
	private Instance instance;

	public NewCommand(Instance i) {
		this.instance = i;
	}

	@Override public void execute() {
		// TODO Auto-generated method stub
	}

	@Override public void revert() {
		// TODO Auto-generated method stub
	}
}
