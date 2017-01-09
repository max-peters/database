package database.services.undoRedo;

public interface RevertableCommand {
	public void execute();
	public void revert();
}
