package database.services.undoRedo;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.services.ServiceRegistry;

public class CancelHelper {
	private boolean lastUndo;

	public CancelHelper() {
		lastUndo = false;
	}

	public void cancel(IUndoRedo undoRedo) throws InterruptedException, SQLException, BadLocationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		System.out.println(undoRedo.canRedo());
		System.out.println(undoRedo.canUndo());
		if (lastUndo && undoRedo.canRedo()) {
			undoRedo.redoCommand();
			lastUndo = false;
		}
		else if (!lastUndo && undoRedo.canUndo()) {
			undoRedo.undoCommand();
			lastUndo = true;
		}
		else {
			terminal.printLine("no command to cancel", StringType.REQUEST, StringFormat.ITALIC);
			terminal.waitForInput();
		}
	}
}
